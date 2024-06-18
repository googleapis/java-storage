package com.google.cloud.storage;/*
 * Copyright 2024 Google LLC
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

import com.google.api.core.ApiFunction;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.cloud.opentelemetry.metric.GoogleCloudMetricExporter;
import com.google.cloud.opentelemetry.metric.MetricConfiguration;
import com.google.cloud.opentelemetry.metric.MonitoredResourceDescription;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.grpc.ManagedChannelBuilder;
import io.grpc.opentelemetry.GrpcOpenTelemetry;
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

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OpenTelemetryBootstrappingUtils {

    static void enableGrpcMetrics(
            InstantiatingGrpcChannelProvider.Builder channelProviderBuilder, String endpoint, String projectId, String universeDomain) {
        String metricServiceEndpoint = getCloudMonitoringEndpoint(endpoint, universeDomain);
        SdkMeterProvider provider = createMeterProvider(metricServiceEndpoint, projectId);

        OpenTelemetrySdk openTelemetrySdk =
                OpenTelemetrySdk.builder().setMeterProvider(provider).buildAndRegisterGlobal();
        GrpcOpenTelemetry grpcOpenTelemetry =
                GrpcOpenTelemetry.newBuilder()
                        .sdk(openTelemetrySdk)
                        .enableMetrics(
                                ImmutableList.of(
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
                                        "grpc.xds_client.resources"))
                        .build();
        ApiFunction<ManagedChannelBuilder, ManagedChannelBuilder> channelConfigurator =
                channelProviderBuilder.getChannelConfigurator();
        channelProviderBuilder.setChannelConfigurator(
                b -> {
                    grpcOpenTelemetry.configureChannelBuilder(b);
                    if (channelConfigurator != null) {
                        return channelConfigurator.apply(b);
                    }
                    return b;
                });
    }

    @VisibleForTesting
    static String getCloudMonitoringEndpoint(String endpoint, String universeDomain) {
        String metricServiceEndpoint = "monitoring.googleapis.com";

        // use contains instead of equals because endpoint has a port in it
        if (universeDomain != null && endpoint.contains("storage." + universeDomain)) {
            metricServiceEndpoint = "monitoring." + universeDomain;
        } else if (!endpoint.contains("storage.googleapis.com")) {
            String canonicalEndpoint = "storage.googleapis.com";
            String privateEndpoint = "private.googleapis.com";
            String restrictedEndpoint = "restricted.googleapis.com";
            if (universeDomain != null) {
                canonicalEndpoint = "storage." + universeDomain;
                privateEndpoint = "private." + universeDomain;
                restrictedEndpoint = "restricted." + universeDomain;
            }
            String match =
                    ImmutableList.of(canonicalEndpoint, privateEndpoint, restrictedEndpoint).stream()
                            .filter(s -> endpoint.contains(s) || endpoint.contains("google-c2p:///" + s))
                            .collect(Collectors.joining());
            if (!StringUtils.isNullOrEmpty(match)) {
                metricServiceEndpoint = match;
            }
        }
        return metricServiceEndpoint + ":" + endpoint.split(":")[1];
    }

    @VisibleForTesting
    static SdkMeterProvider createMeterProvider(String metricServiceEndpoint, String projectId) {
        GCPResourceProvider resourceProvider = new GCPResourceProvider();
        Attributes detectedAttributes = resourceProvider.getAttributes();

        MonitoredResourceDescription monitoredResourceDescription =
                new MonitoredResourceDescription(
                        "generic_task",
                        ImmutableSet.of("project_id", "location", "namespace", "job", "task_id"));
        // When the gcs_client MR is available, do this instead:
        // new MonitoredResourceDescription(
        //      "gcs_client", ImmutableSet.of("project_id", "location", "cloud_platform", "host_id",
        // "instance_id", "api"));

        MetricExporter cloudMonitoringExporter =
                GoogleCloudMetricExporter.createWithConfiguration(
                        MetricConfiguration.builder()
                                .setMonitoredResourceDescription(monitoredResourceDescription)
                                .setMetricServiceEndpoint(metricServiceEndpoint)
                                // .setUseServiceTimeSeries(true)
                                .build());

        String detectedProjectId = detectedAttributes.get(AttributeKey.stringKey("cloud.account.id"));
        SdkMeterProviderBuilder providerBuilder =
                SdkMeterProvider.builder()
                        .registerMetricReader(
                                // Set collection interval to 20 seconds.
                                // See https://cloud.google.com/monitoring/quotas#custom_metrics_quotas
                                // Rate at which data can be written to a single time series: one point each 10
                                // seconds.
                                PeriodicMetricReader.builder(cloudMonitoringExporter)
                                        .setInterval(java.time.Duration.ofSeconds(20))
                                        .build())
                        .setResource(
                                Resource.create(
                                        Attributes.builder()
                                                .put("gcp.resource_type", "generic_task")
                                                .put("job", detectedAttributes.get(AttributeKey.stringKey("host.id")))
                                                .put(
                                                        "task_id",
                                                        detectedAttributes.get(
                                                                AttributeKey.stringKey("gcp.gce.instance.hostname")))
                                                .put("namespace", "gcs_client_instance")
                                                .put(
                                                        "location",
                                                        detectedAttributes.get(AttributeKey.stringKey("cloud.region")))
                                                .put("project_id", detectedProjectId == null ? projectId : detectedProjectId)

                                                /**
                                                 * Uncomment when gcs_client MR is available .put("cloud_platform",
                                                 * detectedAttributes.get(AttributeKey.stringKey("cloud.platform")))
                                                 * .put("host_id",
                                                 * detectedAttributes.get(AttributeKey.stringKey("host.id")))
                                                 * .put("instance_id", UUID.randomUUID().toString()) .put("api", "grpc")
                                                 */
                                                .build()));

        addHistogramView(
                providerBuilder, latencyHistogramBoundaries(), "grpc.client.attempt.duration", "s");
        addHistogramView(
                providerBuilder,
                sizeHistogramBoundaries(),
                "grpc.client.attempt.rcvd_total_compressed_message_size",
                "By");
        addHistogramView(
                providerBuilder,
                sizeHistogramBoundaries(),
                "grpc.client.attempt.sent_total_compressed_message_size",
                "By");

        return providerBuilder.build();
    }

    private static void addHistogramView(
            SdkMeterProviderBuilder provider, List<Double> boundaries, String name, String unit) {
        InstrumentSelector instrumentSelector =
                InstrumentSelector.builder()
                        .setType(InstrumentType.HISTOGRAM)
                        .setUnit(unit)
                        .setName(name)
                        .setMeterName("grpc-java")
                        .setMeterSchemaUrl("")
                        .build();
        View view =
                View.builder()
                        .setName(name)
                        .setDescription(
                                "A view of "
                                        + name
                                        + " with histogram boundaries more appropriate for Google Cloud Storage RPCs")
                        .setAggregation(Aggregation.explicitBucketHistogram(boundaries))
                        .build();
        provider.registerView(instrumentSelector, view);
    }

    private static List<Double> latencyHistogramBoundaries() {
        List<Double> boundaries = new ArrayList<>();
        BigDecimal boundary = new BigDecimal(0, MathContext.UNLIMITED);
        BigDecimal increment = new BigDecimal("0.002", MathContext.UNLIMITED); // 2ms

        // 2ms buckets for the first 100ms, so we can have higher resolution for uploads and downloads
        // in the
        // 100 KiB range
        for (int i = 0; i != 50; i++) {
            boundaries.add(boundary.doubleValue());
            boundary = boundary.add(increment);
        }

        // For the remaining buckets do 10 10ms, 10 20ms, and so on, up until 5 minutes
        increment = new BigDecimal("0.01", MathContext.UNLIMITED); // 10 ms
        for (int i = 0; i != 150 && boundary.compareTo(new BigDecimal(300)) < 1; i++) {
            boundaries.add(boundary.doubleValue());
            if (i != 0 && i % 10 == 0) {
                increment = increment.multiply(new BigDecimal(2));
            }
            boundary = boundary.add(increment);
        }

        return boundaries;
    }

    private static List<Double> sizeHistogramBoundaries() {
        long kb = 1024;
        long mb = 1024 * kb;
        long gb = 1024 * mb;

        List<Double> boundaries = new ArrayList<>();
        long boundary = 0;
        long increment = 128 * kb;

        // 128 KiB increments up to 4MiB, then exponential growth
        while (boundaries.size() < 200 && boundary <= 16 * gb) {
            boundaries.add((double) boundary);
            boundary += increment;
            if (boundary >= 4 * mb) {
                increment *= 2;
            }
        }
        return boundaries;
    }
}
