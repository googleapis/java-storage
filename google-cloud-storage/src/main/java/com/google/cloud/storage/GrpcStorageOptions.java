/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.storage;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import com.google.api.core.ApiClock;
import com.google.api.core.ApiFunction;
import com.google.api.core.BetaApi;
import com.google.api.core.InternalApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcCallSettings;
import com.google.api.gax.grpc.GrpcInterceptorProvider;
import com.google.api.gax.grpc.GrpcStubCallableFactory;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.retrying.StreamResumptionStrategy;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.NoHeaderProvider;
import com.google.api.gax.rpc.RequestParamsBuilder;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.api.gax.rpc.internal.QuotaProjectIdHidingCredentials;
import com.google.api.pathtemplate.PathTemplate;
import com.google.auth.Credentials;
import com.google.cloud.NoCredentials;
import com.google.cloud.ServiceFactory;
import com.google.cloud.ServiceOptions;
import com.google.cloud.ServiceRpc;
import com.google.cloud.TransportOptions;
import com.google.cloud.Tuple;
import com.google.cloud.grpc.GrpcTransportOptions;
import com.google.cloud.opentelemetry.metric.GoogleCloudMetricExporter;
import com.google.cloud.opentelemetry.metric.MetricConfiguration;
import com.google.cloud.opentelemetry.metric.MonitoredResourceDescription;
import com.google.cloud.spi.ServiceRpcFactory;
import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.UnifiedOpts.Opts;
import com.google.cloud.storage.UnifiedOpts.UserProject;
import com.google.cloud.storage.spi.StorageRpcFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import com.google.protobuf.UnsafeByteOperations;
import com.google.storage.v2.ReadObjectRequest;
import com.google.storage.v2.ReadObjectResponse;
import com.google.storage.v2.StorageClient;
import com.google.storage.v2.StorageSettings;
import com.google.storage.v2.stub.GrpcStorageCallableFactory;
import com.google.storage.v2.stub.GrpcStorageStub;
import com.google.storage.v2.stub.StorageStub;
import com.google.storage.v2.stub.StorageStubSettings;
import io.grpc.ClientInterceptor;
import io.grpc.Detachable;
import io.grpc.HasByteBuffer;
import io.grpc.KnownLength;
import io.grpc.ManagedChannelBuilder;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.opentelemetry.GrpcOpenTelemetry;
import io.grpc.protobuf.ProtoUtils;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.internal.StringUtils;
import io.opentelemetry.contrib.gcp.resource.GCPResourceProvider;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.Aggregation;
import io.opentelemetry.sdk.metrics.InstrumentSelector;
import io.opentelemetry.sdk.metrics.InstrumentType;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder;
import io.opentelemetry.sdk.metrics.View;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.threeten.bp.Duration;

/** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
@BetaApi
@TransportCompatibility(Transport.GRPC)
public final class GrpcStorageOptions extends StorageOptions
    implements Retrying.RetryingDependencies {

  private static final long serialVersionUID = -4499446543857945349L;
  private static final String GCS_SCOPE = "https://www.googleapis.com/auth/devstorage.full_control";
  private static final Set<String> SCOPES = ImmutableSet.of(GCS_SCOPE);
  private static final String DEFAULT_HOST = "https://storage.googleapis.com";

  private final GrpcRetryAlgorithmManager retryAlgorithmManager;
  private final Duration terminationAwaitDuration;
  private final boolean attemptDirectPath;
  private final boolean enableMetrics;
  private final GrpcInterceptorProvider grpcInterceptorProvider;
  private final BlobWriteSessionConfig blobWriteSessionConfig;

  private GrpcStorageOptions(Builder builder, GrpcStorageDefaults serviceDefaults) {
    super(builder, serviceDefaults);
    this.retryAlgorithmManager =
        new GrpcRetryAlgorithmManager(
            MoreObjects.firstNonNull(
                builder.storageRetryStrategy, serviceDefaults.getStorageRetryStrategy()));
    this.terminationAwaitDuration =
        MoreObjects.firstNonNull(
            builder.terminationAwaitDuration, serviceDefaults.getTerminationAwaitDuration());
    this.attemptDirectPath = builder.attemptDirectPath;
    this.enableMetrics = builder.enableMetrics;
    this.grpcInterceptorProvider = builder.grpcInterceptorProvider;
    this.blobWriteSessionConfig = builder.blobWriteSessionConfig;
  }

  @Override
  protected Set<String> getScopes() {
    return SCOPES;
  }

  @InternalApi
  GrpcRetryAlgorithmManager getRetryAlgorithmManager() {
    return retryAlgorithmManager;
  }

  @InternalApi
  Duration getTerminationAwaitDuration() {
    return terminationAwaitDuration;
  }

  @InternalApi
  StorageSettings getStorageSettings() throws IOException {
    return resolveSettingsAndOpts().x();
  }

  /**
   * We have to perform several introspections and detections to cross-wire/support several features
   * that are either gapic primitives, ServiceOption primitives or GCS semantic requirements.
   *
   * <h2>Requester Pays, {@code quota_project_id} and {@code userProject}</h2>
   *
   * When using the JSON Api operations destined for requester pays buckets can identify the project
   * for billing and quota attribution by specifying either {@code userProject} query parameter or
   * {@code x-goog-user-project} HTTP Header.
   *
   * <p>If the credentials being used contain the property {@code quota_project_id} this value will
   * automatically be set to the {@code x-goog-user-project} header for both JSON and GAPIC. In the
   * case of JSON this isn't an issue, as any {@code userProject} query parameter takes precedence.
   * However, in gRPC/GAPIC there isn't a {@code userProject} query parameter, instead we are adding
   * {@code x-goog-user-project} to the request context as metadata. If the credentials set the
   * request metadata and we set the request metadata it results in two different entries in the
   * request. This creates ambiguity for GCS which then rejects the request.
   *
   * <p>To account for this and to provide a similar level of precedence we are introspecting the
   * credentials and service options to save any {@code quota_project_id} into an {@link
   * UserProject} which is then used by {@link GrpcStorageImpl} to resolve individual request
   * metadata.
   *
   * <h3>The precedence we provide is as follows</h3>
   *
   * <ol>
   *   <li>Any "userProject" Option provided to an individual method
   *   <li>Any Non-empty value for {@link #getQuotaProjectId()}
   *   <li>Any {@code x-goog-user-project} provided by {@link #credentials}
   * </ol>
   */
  private Tuple<StorageSettings, Opts<UserProject>> resolveSettingsAndOpts() throws IOException {
    String endpoint = getHost();
    URI uri = URI.create(endpoint);
    String scheme = uri.getScheme();
    int port = uri.getPort();
    // Gax routes the endpoint into a method which can't handle schemes,
    // unless for Direct Google Access try and strip here if we can
    switch (scheme) {
      case "http":
        endpoint = String.format("%s:%s", uri.getHost(), port > 0 ? port : 80);
        break;
      case "https":
        endpoint = String.format("%s:%s", uri.getHost(), port > 0 ? port : 443);
        break;
    }

    Opts<UserProject> defaultOpts = Opts.empty();
    CredentialsProvider credentialsProvider;
    Preconditions.checkState(credentials != null, "Unable to resolve credentials");
    if (credentials instanceof NoCredentials) {
      credentialsProvider = NoCredentialsProvider.create();
    } else {
      boolean foundQuotaProject = false;
      if (credentials.hasRequestMetadata()) {
        try {
          Map<String, List<String>> requestMetadata = credentials.getRequestMetadata(uri);
          for (Entry<String, List<String>> e : requestMetadata.entrySet()) {
            String key = e.getKey();
            if ("x-goog-user-project".equals(key.trim().toLowerCase(Locale.ENGLISH))) {
              List<String> value = e.getValue();
              if (!value.isEmpty()) {
                foundQuotaProject = true;
                defaultOpts = Opts.from(UnifiedOpts.userProject(value.get(0)));
                break;
              }
            }
          }
        } catch (IllegalStateException e) {
          // This happens when an instance of OAuth2Credentials attempts to refresh its
          // access token during our attempt at getting request metadata.
          // This is most easily reproduced by OAuth2Credentials.create(null);
          // see com.google.auth.oauth2.OAuth2Credentials.refreshAccessToken
          if (!e.getMessage().startsWith("OAuth2Credentials")) {
            throw e;
          }
        }
      }
      if (foundQuotaProject) {
        // fix for https://github.com/googleapis/java-storage/issues/1736
        credentialsProvider =
            FixedCredentialsProvider.create(new QuotaProjectIdHidingCredentials(credentials));
      } else {
        credentialsProvider = FixedCredentialsProvider.create(credentials);
      }
    }

    boolean isTm =
        Arrays.stream(Thread.currentThread().getStackTrace())
            .anyMatch(
                ste -> ste.getClassName().startsWith("com.google.cloud.storage.transfermanager"));

    HeaderProvider internalHeaderProvider =
        StorageSettings.defaultApiClientHeaderProviderBuilder()
            .setClientLibToken(ServiceOptions.getGoogApiClientLibName(), getLibraryVersion())
            .build();
    if (isTm) {
      internalHeaderProvider =
          XGoogApiClientHeaderProvider.of(
              internalHeaderProvider, ImmutableList.of("gccl-gcs-cmd/tm"));
    }

    StorageSettings.Builder builder =
        new GapicStorageSettingsBuilder(StorageSettings.newBuilder().build())
            .setInternalHeaderProvider(internalHeaderProvider)
            .setEndpoint(endpoint)
            .setCredentialsProvider(credentialsProvider)
            .setClock(getClock());

    // this MUST come after credentials, service options set value has higher priority than creds
    String quotaProjectId = this.getQuotaProjectId();
    if (quotaProjectId != null && !quotaProjectId.isEmpty()) {
      defaultOpts = Opts.from(UnifiedOpts.userProject(quotaProjectId));
    }

    builder.setHeaderProvider(this.getMergedHeaderProvider(new NoHeaderProvider()));

    InstantiatingGrpcChannelProvider.Builder channelProviderBuilder =
        InstantiatingGrpcChannelProvider.newBuilder()
            .setEndpoint(endpoint)
            .setAllowNonDefaultServiceAccount(true)
            .setAttemptDirectPath(attemptDirectPath);

    if (!NoopGrpcInterceptorProvider.INSTANCE.equals(grpcInterceptorProvider)) {
      channelProviderBuilder.setInterceptorProvider(grpcInterceptorProvider);
    }

    if (attemptDirectPath) {
      channelProviderBuilder.setAttemptDirectPathXds();
    }

    if (scheme.equals("http")) {
      channelProviderBuilder.setChannelConfigurator(ManagedChannelBuilder::usePlaintext);
    }

    if(enableMetrics) {
      enableGrpcMetrics(channelProviderBuilder, endpoint);
    }

    builder.setTransportChannelProvider(channelProviderBuilder.build());
    RetrySettings baseRetrySettings = getRetrySettings();
    RetrySettings readRetrySettings =
        baseRetrySettings
            .toBuilder()
            // when performing a read via ReadObject, the ServerStream will have a default relative
            // deadline set of `requestStartTime() + totalTimeout`, meaning if the specified
            // RetrySettings have a totalTimeout of 10 seconds -- which should be plenty for
            // metadata RPCs -- the entire ReadObject stream would need to complete within 10
            // seconds.
            // To allow read streams to have longer lifespans, crank up their timeouts, instead rely
            // on idleTimeout below.
            .setLogicalTimeout(Duration.ofDays(28))
            .build();
    Duration totalTimeout = baseRetrySettings.getTotalTimeout();
    Set<Code> startResumableWriteRetryableCodes =
        builder.startResumableWriteSettings().getRetryableCodes();

    // retries for unary methods are generally handled at a different level, except
    // StartResumableWrite
    builder.applyToAllUnaryMethods(
        input -> {
          input.setSimpleTimeoutNoRetries(totalTimeout);
          return null;
        });

    // configure the settings for StartResumableWrite
    builder
        .startResumableWriteSettings()
        .setRetrySettings(baseRetrySettings)
        .setRetryableCodes(startResumableWriteRetryableCodes);
    // for ReadObject we are configuring the server stream handling to do its own retries, so wire
    // things through. Retryable codes will be controlled closer to the use site as idempotency
    // considerations need to be made.
    builder
        .readObjectSettings()
        .setRetrySettings(readRetrySettings)
        // even though we might want to default to the empty set for retryable codes, don't ever
        // actually do this. Doing so prevents any retry capability from being wired into the stream
        // pipeline, ever.
        // For our use, we will always set it one way or the other to ensure it's appropriate
        // DO NOT: .setRetryableCodes(Collections.emptySet())
        .setResumptionStrategy(new ReadObjectResumptionStrategy())
        // for reads, the stream can be held open for a long time in order to read all bytes,
        // this is totally valid. instead we want to monitor if the stream is doing work and if not
        // timeout.
        .setIdleTimeout(totalTimeout);
    return Tuple.of(builder.build(), defaultOpts);
  }

  private void enableGrpcMetrics(InstantiatingGrpcChannelProvider.Builder channelProviderBuilder, String endpoint) {
    String metricServiceEndpoint = getCloudMonitoringEndpoint(endpoint);
    SdkMeterProvider provider = createMeterProvider(metricServiceEndpoint);

    OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder().setMeterProvider(provider).buildAndRegisterGlobal();
    GrpcOpenTelemetry grpcOpenTelemetry = GrpcOpenTelemetry.newBuilder().sdk(openTelemetrySdk)
            .enableMetrics(Arrays.asList(
                    "grpc.lb.wrr.rr_fallback",
                    "grpc.lb.wrr.endpoint_weight_not_yet_usable",
                    "grpc.lb.wrr.endpoint_weight_stale",
                    "grpc.lb.wrr.endpoint_weights",
                    "grpc.lb.rls.cache_entries",
                    "grpc.lb.rls.cache_size",
                    "grpc.lb.rls.default_target_picks",
                    "grpc.lb.rls.target_picks",
                    "grpc.lb.rls.failed_picks",
                    "grpc.xds_client.connected",
                    "grpc.xds_client.server_failure",
                    "grpc.xds_client.resource_updates_valid",
                    "grpc.xds_client.resource_updates_invalid",
                    "grpc.xds_client.resources"
            ))
            .build();
    ApiFunction<ManagedChannelBuilder, ManagedChannelBuilder> channelConfigurator = channelProviderBuilder.getChannelConfigurator();
    channelProviderBuilder.setChannelConfigurator(b -> {
      grpcOpenTelemetry.configureChannelBuilder(b);
      if (channelConfigurator != null) {
        return channelConfigurator.apply(b);
      }
      return b;
    });
  }

  @VisibleForTesting
  String getCloudMonitoringEndpoint(String endpoint) {
    String metricServiceEndpoint = "monitoring.googleapis.com";

    String universeDomain = this.getUniverseDomain();

    // use contains instead of equals because endpoint has a port in it
    if(universeDomain != null && endpoint.contains("storage." + universeDomain)) {
      metricServiceEndpoint = "monitoring." + universeDomain;
    }
    else if(!endpoint.contains("storage.googleapis.com")) {
      String canonicalEndpoint = "storage.googleapis.com";
      String privateEndpoint = "private.googleapis.com";
      String restrictedEndpoint = "restricted.googleapis.com";
      if(universeDomain != null) {
        canonicalEndpoint = "storage." + universeDomain;
        privateEndpoint = "private." + universeDomain;
        restrictedEndpoint = "restricted." + universeDomain;
      }
      String match = ImmutableList.of(canonicalEndpoint, privateEndpoint, restrictedEndpoint)
              .stream()
              .filter(s -> endpoint.contains(s) || endpoint.contains("google-c2p:///" + s))
              .collect(Collectors.joining());
      if(!StringUtils.isNullOrEmpty(match)) {
        metricServiceEndpoint = match;
      }
    }
    return metricServiceEndpoint + ":" + endpoint.split(":")[1];
  }

  @VisibleForTesting
  SdkMeterProvider createMeterProvider(String metricServiceEndpoint) {
    GCPResourceProvider resourceProvider = new GCPResourceProvider();
    Attributes detectedAttributes = resourceProvider.getAttributes();

    MonitoredResourceDescription monitoredResourceDescription = new MonitoredResourceDescription("generic_task",
            ImmutableSet.of("project_id", "location", "namespace", "job", "task_id"));
    // When the gcs_client MR is available, do this instead:
    //new MonitoredResourceDescription(
    //      "gcs_client", ImmutableSet.of("project_id", "location", "cloud_platform", "host_id", "instance_id", "api"));


    MetricExporter cloudMonitoringExporter =
            GoogleCloudMetricExporter.createWithConfiguration(MetricConfiguration.builder()
                    .setMonitoredResourceDescription(monitoredResourceDescription)
                    .setMetricServiceEndpoint(metricServiceEndpoint )
                    //.setUseServiceTimeSeries(true)
                    .build());

    String projectId = detectedAttributes.get(AttributeKey.stringKey("cloud.account.id"));
    SdkMeterProviderBuilder providerBuilder = SdkMeterProvider.builder()
            .registerMetricReader(
                    // Set collection interval to 20 seconds.
                    // See https://cloud.google.com/monitoring/quotas#custom_metrics_quotas
                    // Rate at which data can be written to a single time series: one point each 10
                    // seconds.
                    PeriodicMetricReader.builder(cloudMonitoringExporter)
                            .setInterval(java.time.Duration.ofSeconds(20))
                            .build())
            .setResource(Resource.create(Attributes.builder()
                    .put("gcp.resource_type", "generic_task")
                    .put("job", detectedAttributes.get(AttributeKey.stringKey("host.id")))
                    .put("task_id", detectedAttributes.get(AttributeKey.stringKey("gcp.gce.instance.hostname")))
                    .put("namespace", "gcs_client_instance")
                    .put("location", detectedAttributes.get(AttributeKey.stringKey("cloud.region")))
                    .put("project_id", projectId == null ? this.getProjectId() : projectId)

                    /** Uncomment when gcs_client MR is available
                     .put("cloud_platform", detectedAttributes.get(AttributeKey.stringKey("cloud.platform")))
                     .put("host_id", detectedAttributes.get(AttributeKey.stringKey("host.id")))
                     .put("instance_id", UUID.randomUUID().toString())
                     .put("api", "grpc")
                     **/
                    .build()));

    addHistogramView(providerBuilder, latencyHistogramBoundaries(), "grpc.client.attempt.duration", "s");
    addHistogramView(providerBuilder, sizeHistogramBoundaries(), "grpc.client.attempt.rcvd_total_compressed_message_size", "By");
    addHistogramView(providerBuilder, sizeHistogramBoundaries(), "grpc.client.attempt.sent_total_compressed_message_size", "By");

    return providerBuilder.build();
  }

  private void addHistogramView(SdkMeterProviderBuilder provider, List<Double> boundaries, String name, String unit) {
    InstrumentSelector instrumentSelector = InstrumentSelector.builder()
            .setType(InstrumentType.HISTOGRAM)
            .setUnit(unit)
            .setName(name)
            .setMeterName("grpc-java")
            .setMeterSchemaUrl("")
            .build();
    View view = View.builder()
            .setName(name)
            .setDescription("A view of " + name + " with histogram boundaries more appropriate for Google Cloud Storage RPCs")
            .setAggregation(Aggregation.explicitBucketHistogram(boundaries))
            .build();
    provider.registerView(instrumentSelector, view);
  }

  private List<Double> latencyHistogramBoundaries() {
    List<Double> boundaries = new ArrayList<>();
    BigDecimal boundary = new BigDecimal(0, MathContext.UNLIMITED);
    BigDecimal increment = new BigDecimal("0.002", MathContext.UNLIMITED); // 2ms

    // 2ms buckets for the first 100ms, so we can have higher resolution for uploads and downloads in the
    // 100 KiB range
    for(int i = 0; i != 50; i++) {
      boundaries.add(boundary.doubleValue());
      boundary = boundary.add(increment);
    }

    // For the remaining buckets do 10 10ms, 10 20ms, and so on, up until 5 minutes
    increment = new BigDecimal("0.01", MathContext.UNLIMITED); //10 ms
    for(int i = 0; i != 150 && boundary.compareTo(new BigDecimal(300)) < 1; i++) {
      boundaries.add(boundary.doubleValue());
      if(i != 0 && i % 10 == 0) {
        increment = increment.multiply(new BigDecimal(2));
      }
      boundary = boundary.add(increment);
    }

    return boundaries;
  }

  private List<Double> sizeHistogramBoundaries() {
    long kb = 1024;
    long mb = 1024 * kb;
    long gb = 1024 * mb;

    List<Double> boundaries = new ArrayList<>();
    long boundary = 0;
    long increment = 128 * kb;

    // 128 KiB increments up to 4MiB, then exponential growth
    while (boundaries.size() < 200 && boundary <= 16 * gb) {
      boundaries.add((double)boundary);
      boundary += increment;
      if(boundary >= 4 * mb) {
        increment *= 2;
      }
    }
    return boundaries;
  }

  /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
  @BetaApi
  @Override
  public GrpcStorageOptions.Builder toBuilder() {
    return new GrpcStorageOptions.Builder(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        retryAlgorithmManager,
        terminationAwaitDuration,
        attemptDirectPath,
        enableMetrics,
        grpcInterceptorProvider,
        blobWriteSessionConfig,
        baseHashCode());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof GrpcStorageOptions)) {
      return false;
    }
    GrpcStorageOptions that = (GrpcStorageOptions) o;
    return attemptDirectPath == that.attemptDirectPath
        && enableMetrics == that.enableMetrics
        && Objects.equals(retryAlgorithmManager, that.retryAlgorithmManager)
        && Objects.equals(terminationAwaitDuration, that.terminationAwaitDuration)
        && Objects.equals(grpcInterceptorProvider, that.grpcInterceptorProvider)
        && Objects.equals(blobWriteSessionConfig, that.blobWriteSessionConfig)
        && this.baseEquals(that);
  }

  /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
  @BetaApi
  public static GrpcStorageOptions.Builder newBuilder() {
    return new GrpcStorageOptions.Builder().setHost(DEFAULT_HOST);
  }

  /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
  @BetaApi
  public static GrpcStorageOptions getDefaultInstance() {
    return newBuilder().build();
  }

  /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
  @BetaApi
  public static GrpcStorageOptions.GrpcStorageDefaults defaults() {
    return GrpcStorageOptions.GrpcStorageDefaults.INSTANCE;
  }

  // since our new GrpcStorageImpl can "close" we need to help ServiceOptions know whether it can
  // use it's cached instance.
  @Override
  protected boolean shouldRefreshService(Storage cachedService) {
    if (cachedService instanceof GrpcStorageImpl) {
      GrpcStorageImpl service = (GrpcStorageImpl) cachedService;
      return service.isClosed();
    }
    return super.shouldRefreshService(cachedService);
  }

  /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
  @BetaApi
  public static final class Builder extends StorageOptions.Builder {

    private StorageRetryStrategy storageRetryStrategy;
    private Duration terminationAwaitDuration;
    private boolean attemptDirectPath = GrpcStorageDefaults.INSTANCE.isAttemptDirectPath();
    private boolean enableMetrics = GrpcStorageDefaults.INSTANCE.isEnableMetrics();
    private GrpcInterceptorProvider grpcInterceptorProvider =
        GrpcStorageDefaults.INSTANCE.grpcInterceptorProvider();
    private BlobWriteSessionConfig blobWriteSessionConfig =
        GrpcStorageDefaults.INSTANCE.getDefaultStorageWriterConfig();

    Builder() {}

    Builder(StorageOptions options) {
      super(options);
      GrpcStorageOptions gso = (GrpcStorageOptions) options;
      this.storageRetryStrategy = gso.getRetryAlgorithmManager().retryStrategy;
      this.terminationAwaitDuration = gso.getTerminationAwaitDuration();
      this.attemptDirectPath = gso.attemptDirectPath;
      this.enableMetrics = gso.enableMetrics;
      this.grpcInterceptorProvider = gso.grpcInterceptorProvider;
      this.blobWriteSessionConfig = gso.blobWriteSessionConfig;
    }

    /**
     * Set the maximum duration in which to await termination of any outstanding requests when
     * calling {@link Storage#close()}
     *
     * @param terminationAwaitDuration a non-null Duration to use
     * @return the builder
     * @since 2.14.0 This new api is in preview and is subject to breaking changes.
     */
    @BetaApi
    public Builder setTerminationAwaitDuration(Duration terminationAwaitDuration) {
      this.terminationAwaitDuration =
          requireNonNull(terminationAwaitDuration, "terminationAwaitDuration must be non null");
      return this;
    }

    /**
     * Option which signifies the client should attempt to connect to gcs via Direct Google Access.
     *
     * <p><i>NOTE</i>There is no need to specify a new endpoint via {@link #setHost(String)} as the
     * underlying code will translate the normal {@code https://storage.googleapis.com:443} into the
     * proper Direct Google Access URI for you.
     *
     * @since 2.14.0 This new api is in preview and is subject to breaking changes.
     */
    @BetaApi
    public GrpcStorageOptions.Builder setAttemptDirectPath(boolean attemptDirectPath) {
      this.attemptDirectPath = attemptDirectPath;
      return this;
    }
    /**
     * Option for whether this client should emit metrics to Cloud Monitoring.
     * Enabled by default. Emitting metrics is free and requires minimal CPU
     * and memory, but can be disabled by setting this to false.
     *
     * @since 2.41.0 This new api is in preview and is subject to breaking changes.
     */
    @BetaApi
    public GrpcStorageOptions.Builder setEnableMetrics(boolean enableMetrics) {
      this.enableMetrics = enableMetrics;
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setTransportOptions(TransportOptions transportOptions) {
      if (!(transportOptions instanceof GrpcTransportOptions)) {
        throw new IllegalArgumentException("Only gRPC transport is allowed.");
      }
      super.setTransportOptions(transportOptions);
      return this;
    }

    /**
     * Override the default retry handling behavior with an alternate strategy.
     *
     * @param storageRetryStrategy a non-null storageRetryStrategy to use
     * @return the builder
     * @see StorageRetryStrategy#getDefaultStorageRetryStrategy()
     * @since 2.14.0 This new api is in preview and is subject to breaking changes.
     */
    @BetaApi
    public GrpcStorageOptions.Builder setStorageRetryStrategy(
        StorageRetryStrategy storageRetryStrategy) {
      this.storageRetryStrategy =
          requireNonNull(storageRetryStrategy, "storageRetryStrategy must be non null");
      return this;
    }

    @Override
    protected GrpcStorageOptions.Builder self() {
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setServiceFactory(
        ServiceFactory<Storage, StorageOptions> serviceFactory) {
      super.setServiceFactory(serviceFactory);
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setClock(ApiClock clock) {
      super.setClock(clock);
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setProjectId(String projectId) {
      super.setProjectId(projectId);
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setHost(String host) {
      super.setHost(host);
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setCredentials(Credentials credentials) {
      super.setCredentials(credentials);
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setRetrySettings(RetrySettings retrySettings) {
      super.setRetrySettings(retrySettings);
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setServiceRpcFactory(
        ServiceRpcFactory<StorageOptions> serviceRpcFactory) {
      throw new UnsupportedOperationException(
          "GrpcStorageOptions does not support setting a custom instance of ServiceRpcFactory");
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setHeaderProvider(HeaderProvider headerProvider) {
      super.setHeaderProvider(headerProvider);
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setClientLibToken(String clientLibToken) {
      super.setClientLibToken(clientLibToken);
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setQuotaProjectId(String quotaProjectId) {
      super.setQuotaProjectId(quotaProjectId);
      return this;
    }

    /** @since 2.22.3 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    public GrpcStorageOptions.Builder setGrpcInterceptorProvider(
        @NonNull GrpcInterceptorProvider grpcInterceptorProvider) {
      requireNonNull(grpcInterceptorProvider, "grpcInterceptorProvider must be non null");
      this.grpcInterceptorProvider = grpcInterceptorProvider;
      return this;
    }

    /**
     * @see BlobWriteSessionConfig
     * @see BlobWriteSessionConfigs
     * @see Storage#blobWriteSession(BlobInfo, BlobWriteOption...)
     * @see GrpcStorageDefaults#getDefaultStorageWriterConfig()
     * @since 2.26.0 This new api is in preview and is subject to breaking changes.
     */
    @BetaApi
    public GrpcStorageOptions.Builder setBlobWriteSessionConfig(
        @NonNull BlobWriteSessionConfig blobWriteSessionConfig) {
      requireNonNull(blobWriteSessionConfig, "blobWriteSessionConfig must be non null");
      checkArgument(
          blobWriteSessionConfig instanceof BlobWriteSessionConfig.GrpcCompatible,
          "The provided instance of BlobWriteSessionConfig is not compatible with gRPC transport.");
      this.blobWriteSessionConfig = blobWriteSessionConfig;
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions build() {
      GrpcStorageOptions options = new GrpcStorageOptions(this, defaults());
      // TODO: Remove when https://github.com/googleapis/sdk-platform-java/issues/2911 is resolved
      if (options.getUniverseDomain() != null) {
        this.setHost("https://storage." + options.getUniverseDomain());
        return new GrpcStorageOptions(this, defaults());
      }
      return options;
    }
  }

  /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
  @BetaApi
  public static final class GrpcStorageDefaults extends StorageDefaults {
    static final GrpcStorageDefaults INSTANCE = new GrpcStorageOptions.GrpcStorageDefaults();
    static final StorageFactory STORAGE_FACTORY = new GrpcStorageFactory();
    static final StorageRpcFactory STORAGE_RPC_FACTORY = new GrpcStorageRpcFactory();
    static final GrpcInterceptorProvider INTERCEPTOR_PROVIDER =
        NoopGrpcInterceptorProvider.INSTANCE;

    private GrpcStorageDefaults() {}

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public StorageFactory getDefaultServiceFactory() {
      return STORAGE_FACTORY;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public StorageRpcFactory getDefaultRpcFactory() {
      return STORAGE_RPC_FACTORY;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcTransportOptions getDefaultTransportOptions() {
      return GrpcTransportOptions.newBuilder().build();
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    public StorageRetryStrategy getStorageRetryStrategy() {
      return StorageRetryStrategy.getDefaultStorageRetryStrategy();
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    public Duration getTerminationAwaitDuration() {
      return Duration.ofMinutes(1);
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    public boolean isAttemptDirectPath() {
      return false;
    }

    /** @since 2.41.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    public boolean isEnableMetrics() {
      return true;
    }

    /** @since 2.22.3 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    public GrpcInterceptorProvider grpcInterceptorProvider() {
      return INTERCEPTOR_PROVIDER;
    }

    /** @since 2.26.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    public BlobWriteSessionConfig getDefaultStorageWriterConfig() {
      return BlobWriteSessionConfigs.getDefault();
    }
  }

  /**
   * Internal implementation detail, only public to allow for {@link java.io.Serializable}
   * compatibility in {@link com.google.cloud.ServiceOptions}.
   *
   * <p>To access an instance of this class instead use {@link
   * GrpcStorageOptions.GrpcStorageDefaults#getDefaultServiceFactory()
   * GrpcStorageOptions.defaults().getDefaultServiceFactory()}.
   *
   * @see GrpcStorageOptions#defaults()
   * @see GrpcStorageOptions.GrpcStorageDefaults#getDefaultServiceFactory()
   * @since 2.14.0 This new api is in preview and is subject to breaking changes.
   */
  @InternalApi
  @BetaApi
  public static class GrpcStorageFactory implements StorageFactory {

    /**
     * Internal implementation detail, only public to allow for {@link java.io.Serializable}
     * compatibility in {@link com.google.cloud.ServiceOptions}.
     *
     * <p>To access an instance of this class instead use {@link
     * GrpcStorageOptions.GrpcStorageDefaults#getDefaultServiceFactory()
     * GrpcStorageOptions.defaults().getDefaultServiceFactory()}.
     *
     * @see GrpcStorageOptions#defaults()
     * @see GrpcStorageOptions.GrpcStorageDefaults#getDefaultServiceFactory()
     * @deprecated instead use {@link
     *     GrpcStorageOptions.GrpcStorageDefaults#getDefaultServiceFactory()
     *     GrpcStorageOptions.defaults().getDefaultServiceFactory()}
     * @since 2.14.0 This new api is in preview and is subject to breaking changes.
     */
    // this class needs to be public due to ServiceOptions forName'ing it in it's readObject method
    @InternalApi
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    @BetaApi
    public GrpcStorageFactory() {}

    @Override
    public Storage create(StorageOptions options) {
      if (options instanceof GrpcStorageOptions) {
        GrpcStorageOptions grpcStorageOptions = (GrpcStorageOptions) options;
        try {
          Tuple<StorageSettings, Opts<UserProject>> t = grpcStorageOptions.resolveSettingsAndOpts();
          StorageSettings storageSettings = t.x();
          Opts<UserProject> defaultOpts = t.y();
          if (ZeroCopyReadinessChecker.isReady()) {
            StorageStubSettings stubSettings =
                (StorageStubSettings) storageSettings.getStubSettings();
            ClientContext clientContext = ClientContext.create(stubSettings);
            GrpcStorageCallableFactory grpcStorageCallableFactory =
                new GrpcStorageCallableFactory();
            InternalZeroCopyGrpcStorageStub stub =
                new InternalZeroCopyGrpcStorageStub(
                    stubSettings, clientContext, grpcStorageCallableFactory);
            StorageClient client = new InternalStorageClient(stub);
            return new GrpcStorageImpl(
                grpcStorageOptions,
                client,
                stub.getObjectMediaResponseMarshaller,
                grpcStorageOptions.blobWriteSessionConfig.createFactory(Clock.systemUTC()),
                defaultOpts);
          } else {
            StorageClient client = StorageClient.create(storageSettings);
            return new GrpcStorageImpl(
                grpcStorageOptions,
                client,
                ResponseContentLifecycleManager.noop(),
                grpcStorageOptions.blobWriteSessionConfig.createFactory(Clock.systemUTC()),
                defaultOpts);
          }

        } catch (IOException e) {
          throw new IllegalStateException(
              "Unable to instantiate gRPC com.google.cloud.storage.Storage client.", e);
        }
      } else {
        throw new IllegalArgumentException("Only GrpcStorageOptions supported");
      }
    }
  }

  /**
   * Internal implementation detail, only public to allow for {@link java.io.Serializable}
   * compatibility in {@link com.google.cloud.ServiceOptions}.
   *
   * <p>To access an instance of this class instead use {@link
   * GrpcStorageOptions.GrpcStorageDefaults#getDefaultRpcFactory()
   * GrpcStorageOptions.defaults().getDefaultRpcFactory()}.
   *
   * @see GrpcStorageOptions#defaults()
   * @see GrpcStorageOptions.GrpcStorageDefaults#getDefaultRpcFactory()
   * @since 2.14.0 This new api is in preview and is subject to breaking changes.
   */
  @InternalApi
  @BetaApi
  @Deprecated
  public static class GrpcStorageRpcFactory implements StorageRpcFactory {

    /**
     * Internal implementation detail, only public to allow for {@link java.io.Serializable}
     * compatibility in {@link com.google.cloud.ServiceOptions}.
     *
     * <p>To access an instance of this class instead use {@link
     * GrpcStorageOptions.GrpcStorageDefaults#getDefaultRpcFactory()
     * GrpcStorageOptions.defaults().getDefaultRpcFactory()}.
     *
     * @see GrpcStorageOptions#defaults()
     * @see GrpcStorageOptions.GrpcStorageDefaults#getDefaultRpcFactory()
     * @deprecated instead use {@link GrpcStorageOptions.GrpcStorageDefaults#getDefaultRpcFactory()
     *     GrpcStorageOptions.defaults().getDefaultRpcFactory()}
     * @since 2.14.0 This new api is in preview and is subject to breaking changes.
     */
    // this class needs to be public due to ServiceOptions forName'ing it in it's readObject method
    @InternalApi
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    @BetaApi
    public GrpcStorageRpcFactory() {}

    @Override
    public ServiceRpc create(StorageOptions options) {
      throw new IllegalStateException("No supported for grpc");
    }
  }

  // TODO: See if we can change gax to allow shifting this to callable.withContext so it doesn't
  //   have to be set globally
  private static class ReadObjectResumptionStrategy
      implements StreamResumptionStrategy<ReadObjectRequest, ReadObjectResponse> {
    private long readOffset = 0;

    @NonNull
    @Override
    public StreamResumptionStrategy<ReadObjectRequest, ReadObjectResponse> createNew() {
      return new ReadObjectResumptionStrategy();
    }

    @NonNull
    @Override
    public ReadObjectResponse processResponse(ReadObjectResponse response) {
      readOffset += response.getChecksummedData().getContent().size();
      return response;
    }

    @Nullable
    @Override
    public ReadObjectRequest getResumeRequest(ReadObjectRequest originalRequest) {
      if (readOffset != 0) {
        return originalRequest.toBuilder().setReadOffset(readOffset).build();
      }
      return originalRequest;
    }

    @Override
    public boolean canResume() {
      return true;
    }
  }

  // setInternalHeaderProvider is protected so we need to open its scope in order to set it
  // we are adding an entry for gccl which is set via this provider
  private static final class GapicStorageSettingsBuilder extends StorageSettings.Builder {
    private GapicStorageSettingsBuilder(StorageSettings settings) {
      super(settings);
    }

    @Override
    protected StorageSettings.Builder setInternalHeaderProvider(
        HeaderProvider internalHeaderProvider) {
      return super.setInternalHeaderProvider(internalHeaderProvider);
    }
  }

  private static final class NoopGrpcInterceptorProvider
      implements GrpcInterceptorProvider, Serializable {
    private static long serialVersionUID = -8523033236999805349L;
    private static final NoopGrpcInterceptorProvider INSTANCE = new NoopGrpcInterceptorProvider();

    @Override
    public List<ClientInterceptor> getInterceptors() {
      return ImmutableList.of();
    }

    /** prevent java serialization from using a new instance */
    private Object readResolve() {
      return INSTANCE;
    }
  }

  private static final class InternalStorageClient extends StorageClient {

    private InternalStorageClient(StorageStub stub) {
      super(stub);
    }
  }

  private static final class InternalZeroCopyGrpcStorageStub extends GrpcStorageStub
      implements AutoCloseable {
    private final ReadObjectResponseZeroCopyMessageMarshaller getObjectMediaResponseMarshaller;

    private final ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse>
        serverStreamingCallable;

    private InternalZeroCopyGrpcStorageStub(
        StorageStubSettings settings,
        ClientContext clientContext,
        GrpcStubCallableFactory callableFactory)
        throws IOException {
      super(settings, clientContext, callableFactory);

      this.getObjectMediaResponseMarshaller =
          new ReadObjectResponseZeroCopyMessageMarshaller(ReadObjectResponse.getDefaultInstance());

      MethodDescriptor<ReadObjectRequest, ReadObjectResponse> readObjectMethodDescriptor =
          MethodDescriptor.<ReadObjectRequest, ReadObjectResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName("google.storage.v2.Storage/ReadObject")
              .setRequestMarshaller(ProtoUtils.marshaller(ReadObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(getObjectMediaResponseMarshaller)
              .build();

      GrpcCallSettings<ReadObjectRequest, ReadObjectResponse> readObjectTransportSettings =
          GrpcCallSettings.<ReadObjectRequest, ReadObjectResponse>newBuilder()
              .setMethodDescriptor(readObjectMethodDescriptor)
              .setParamsExtractor(
                  request -> {
                    RequestParamsBuilder builder = RequestParamsBuilder.create();
                    // todo: this is fragile to proto annotation changes, and would require manual
                    // maintenance
                    builder.add(request.getBucket(), "bucket", PathTemplate.create("{bucket=**}"));
                    return builder.build();
                  })
              .build();

      this.serverStreamingCallable =
          callableFactory.createServerStreamingCallable(
              readObjectTransportSettings, settings.readObjectSettings(), clientContext);
    }

    @Override
    public ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> readObjectCallable() {
      return serverStreamingCallable;
    }
  }

  @VisibleForTesting
  static class ReadObjectResponseZeroCopyMessageMarshaller
      implements MethodDescriptor.PrototypeMarshaller<ReadObjectResponse>,
          ResponseContentLifecycleManager,
          Closeable {
    private final Map<ReadObjectResponse, InputStream> unclosedStreams;
    private final Parser<ReadObjectResponse> parser;
    private final MethodDescriptor.PrototypeMarshaller<ReadObjectResponse> baseMarshaller;

    ReadObjectResponseZeroCopyMessageMarshaller(ReadObjectResponse defaultInstance) {
      parser = defaultInstance.getParserForType();
      baseMarshaller =
          (MethodDescriptor.PrototypeMarshaller<ReadObjectResponse>)
              ProtoUtils.marshaller(defaultInstance);
      unclosedStreams = Collections.synchronizedMap(new IdentityHashMap<>());
    }

    @Override
    public Class<ReadObjectResponse> getMessageClass() {
      return baseMarshaller.getMessageClass();
    }

    @Override
    public ReadObjectResponse getMessagePrototype() {
      return baseMarshaller.getMessagePrototype();
    }

    @Override
    public InputStream stream(ReadObjectResponse value) {
      return baseMarshaller.stream(value);
    }

    @Override
    public ReadObjectResponse parse(InputStream stream) {
      CodedInputStream cis = null;
      try {
        if (stream instanceof KnownLength
            && stream instanceof Detachable
            && stream instanceof HasByteBuffer
            && ((HasByteBuffer) stream).byteBufferSupported()) {
          int size = stream.available();
          // Stream is now detached here and should be closed later.
          stream = ((Detachable) stream).detach();
          // This mark call is to keep buffer while traversing buffers using skip.
          stream.mark(size);
          List<ByteString> byteStrings = new ArrayList<>();
          while (stream.available() != 0) {
            ByteBuffer buffer = ((HasByteBuffer) stream).getByteBuffer();
            byteStrings.add(UnsafeByteOperations.unsafeWrap(buffer));
            stream.skip(buffer.remaining());
          }
          stream.reset();
          cis = ByteString.copyFrom(byteStrings).newCodedInput();
          cis.enableAliasing(true);
          cis.setSizeLimit(Integer.MAX_VALUE);
        }
      } catch (IOException e) {
        throw Status.INTERNAL
            .withDescription("Error parsing input stream for ReadObject")
            .withCause(e)
            .asRuntimeException();
      }
      if (cis != null) {
        // fast path (no memory copy)
        ReadObjectResponse message;
        try {
          message = parseFrom(cis);
        } catch (InvalidProtocolBufferException ipbe) {
          throw Status.INTERNAL
              .withDescription("Invalid protobuf byte sequence for ReadObject")
              .withCause(ipbe)
              .asRuntimeException();
        }
        unclosedStreams.put(message, stream);
        return message;
      } else {
        // slow path
        return baseMarshaller.parse(stream);
      }
    }

    private ReadObjectResponse parseFrom(CodedInputStream stream)
        throws InvalidProtocolBufferException {
      ReadObjectResponse message = parser.parseFrom(stream);
      try {
        stream.checkLastTagWas(0);
        return message;
      } catch (InvalidProtocolBufferException e) {
        e.setUnfinishedMessage(message);
        throw e;
      }
    }

    @Override
    public ResponseContentLifecycleHandle get(ReadObjectResponse response) {
      InputStream stream = unclosedStreams.remove(response);
      return new ResponseContentLifecycleHandle(response, stream);
    }

    @Override
    public void close() throws IOException {
      closeAllStreams(unclosedStreams.values());
    }

    /**
     * In the event closing the streams results in multiple streams throwing IOExceptions, collect
     * them all as suppressed exceptions on the first occurrence.
     */
    @VisibleForTesting
    static void closeAllStreams(Collection<InputStream> inputStreams) throws IOException {
      IOException ioException =
          inputStreams.stream()
              .map(
                  stream -> {
                    try {
                      stream.close();
                      return null;
                    } catch (IOException e) {
                      return e;
                    }
                  })
              .filter(Objects::nonNull)
              .reduce(
                  null,
                  (l, r) -> {
                    if (l != null) {
                      l.addSuppressed(r);
                      return l;
                    } else {
                      return r;
                    }
                  },
                  (l, r) -> l);

      if (ioException != null) {
        throw ioException;
      }
    }
  }

  static final class ZeroCopyReadinessChecker {
    private static final boolean isZeroCopyReady;

    static {
      // Check whether io.grpc.Detachable exists?
      boolean detachableClassExists = false;
      try {
        // Try to load Detachable interface in the package where KnownLength is in.
        // This can be done directly by looking up io.grpc.Detachable but rather
        // done indirectly to handle the case where gRPC is being shaded in a
        // different package.
        String knownLengthClassName = KnownLength.class.getName();
        String detachableClassName =
            knownLengthClassName.substring(0, knownLengthClassName.lastIndexOf('.') + 1)
                + "Detachable";
        Class<?> detachableClass = Class.forName(detachableClassName);
        detachableClassExists = (detachableClass != null);
      } catch (ClassNotFoundException ex) {
        // leaves detachableClassExists false
      }
      // Check whether com.google.protobuf.UnsafeByteOperations exists?
      boolean unsafeByteOperationsClassExists = false;
      try {
        // Same above
        String messageLiteClassName = MessageLite.class.getName();
        String unsafeByteOperationsClassName =
            messageLiteClassName.substring(0, messageLiteClassName.lastIndexOf('.') + 1)
                + "UnsafeByteOperations";
        Class<?> unsafeByteOperationsClass = Class.forName(unsafeByteOperationsClassName);
        unsafeByteOperationsClassExists = (unsafeByteOperationsClass != null);
      } catch (ClassNotFoundException ex) {
        // leaves unsafeByteOperationsClassExists false
      }
      isZeroCopyReady = detachableClassExists && unsafeByteOperationsClassExists;
    }

    public static boolean isReady() {
      return isZeroCopyReady;
    }
  }
}
