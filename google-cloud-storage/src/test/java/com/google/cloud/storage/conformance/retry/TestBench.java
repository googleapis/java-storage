/*
 * Copyright 2021 Google LLC
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

package com.google.cloud.storage.conformance.retry;

import static com.google.cloud.RetryHelper.runWithRetries;
import static java.util.Objects.requireNonNull;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.core.NanoClock;
import com.google.api.gax.retrying.BasicResultRetryAlgorithm;
import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.RetryHelper.RetryHelperException;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.threeten.bp.Duration;

/**
 * A JUnit 4 {@link TestRule} which integrates with the <a target="_blank" rel="noopener noreferrer"
 * href="https://github.com/googleapis/storage-testbench">storage-testbench</a> by pulling the
 * docker image, starting the container, providing methods for interacting with the {@code
 * /retry_test} rest api, stopping the container.
 *
 * <p>This rule expects to be bound as an {@link org.junit.ClassRule @ClassRule} field.
 */
final class TestBench implements TestRule {

  private static final Logger LOGGER = Logger.getLogger(TestBench.class.getName());

  private final boolean ignorePullError;
  private final String baseUri;
  private final String dockerImageName;
  private final String dockerImageTag;
  private final CleanupStrategy cleanupStrategy;

  private final Gson gson;
  private final HttpRequestFactory requestFactory;

  private TestBench(
      boolean ignorePullError,
      String baseUri,
      String dockerImageName,
      String dockerImageTag,
      CleanupStrategy cleanupStrategy) {
    this.ignorePullError = ignorePullError;
    this.baseUri = baseUri;
    this.dockerImageName = dockerImageName;
    this.dockerImageTag = dockerImageTag;
    this.cleanupStrategy = cleanupStrategy;
    this.gson = new Gson();
    this.requestFactory =
        new NetHttpTransport.Builder()
            .build()
            .createRequestFactory(
                request -> {
                  request.setCurlLoggingEnabled(false);
                  request.getHeaders().setAccept("application/json");
                  request.getHeaders().setUserAgent("java-conformance-tests/");
                });
  }

  String getBaseUri() {
    return baseUri;
  }

  RetryTestResource createRetryTest(RetryTestResource retryTestResource) throws IOException {
    GenericUrl url = new GenericUrl(baseUri + "/retry_test");
    String jsonString = gson.toJson(retryTestResource);
    HttpContent content =
        new ByteArrayContent("application/json", jsonString.getBytes(StandardCharsets.UTF_8));
    HttpRequest req = requestFactory.buildPostRequest(url, content);
    HttpResponse resp = req.execute();
    RetryTestResource result = gson.fromJson(resp.parseAsString(), RetryTestResource.class);
    resp.disconnect();
    return result;
  }

  void deleteRetryTest(RetryTestResource retryTestResource) throws IOException {
    GenericUrl url = new GenericUrl(baseUri + "/retry_test/" + retryTestResource.id);
    HttpRequest req = requestFactory.buildDeleteRequest(url);
    HttpResponse resp = req.execute();
    resp.disconnect();
  }

  RetryTestResource getRetryTest(RetryTestResource retryTestResource) throws IOException {
    GenericUrl url = new GenericUrl(baseUri + "/retry_test/" + retryTestResource.id);
    HttpRequest req = requestFactory.buildGetRequest(url);
    HttpResponse resp = req.execute();
    RetryTestResource result = gson.fromJson(resp.parseAsString(), RetryTestResource.class);
    resp.disconnect();
    return result;
  }

  List<RetryTestResource> listRetryTests() throws IOException {
    GenericUrl url = new GenericUrl(baseUri + "/retry_tests");
    HttpRequest req = requestFactory.buildGetRequest(url);
    HttpResponse resp = req.execute();
    JsonObject result = gson.fromJson(resp.parseAsString(), JsonObject.class);
    JsonArray retryTest = (JsonArray) result.get("retry_test");
    ImmutableList.Builder<RetryTestResource> b = ImmutableList.builder();
    for (JsonElement e : retryTest) {
      b.add(gson.fromJson(e, RetryTestResource.class));
    }
    resp.disconnect();
    return b.build();
  }

  @Override
  public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        Path tempDirectory = Files.createTempDirectory("retry-conformance-server");
        Path outPath = tempDirectory.resolve("stdout");
        Path errPath = tempDirectory.resolve("stderr");

        File outFile = outPath.toFile();
        File errFile = errPath.toFile();
        LOGGER.info("Redirecting server stdout to: " + outFile.getAbsolutePath());
        LOGGER.info("Redirecting server stderr to: " + errFile.getAbsolutePath());
        String dockerImage = String.format("%s:%s", dockerImageName, dockerImageTag);
        // First try and pull the docker image, this validates docker is available and running
        // on the host, as well as gives time for the image to be downloaded independently of
        // trying to start the container. (Below, when we first start the container we then attempt
        // to issue a call against the api before we yield to run our tests.)
        try {
          Process p =
              new ProcessBuilder()
                  .command("docker", "pull", dockerImage)
                  .redirectOutput(outFile)
                  .redirectError(errFile)
                  .start();
          p.waitFor(5, TimeUnit.MINUTES);
          if (!ignorePullError && p.exitValue() != 0) {
            dumpServerLogs(outFile, errFile);
            throw new IllegalStateException(
                String.format(
                    "Non-zero status while attempting to pull docker image '%s'", dockerImage));
          }
        } catch (InterruptedException | IllegalThreadStateException e) {
          dumpServerLogs(outFile, errFile);
          throw new IllegalStateException(
              String.format("Timeout while attempting to pull docker image '%s'", dockerImage));
        }

        int port = URI.create(baseUri).getPort();
        final Process process =
            new ProcessBuilder()
                .command(
                    "docker",
                    "run",
                    "-i",
                    "--rm",
                    "--publish",
                    port + ":9000",
                    "--name=retry-conformance-server",
                    dockerImage)
                .redirectOutput(outFile)
                .redirectError(errFile)
                .start();
        boolean success = false;
        try {
          // wait a small amount of time for the server to come up before probing
          Thread.sleep(500);
          // wait for the server to come up
          List<RetryTestResource> existingResources =
              runWithRetries(
                  TestBench.this::listRetryTests,
                  RetrySettings.newBuilder()
                      .setTotalTimeout(Duration.ofSeconds(30))
                      .setInitialRetryDelay(Duration.ofMillis(500))
                      .setRetryDelayMultiplier(1.5)
                      .setMaxRetryDelay(Duration.ofSeconds(5))
                      .build(),
                  new BasicResultRetryAlgorithm<List<RetryTestResource>>() {
                    @Override
                    public boolean shouldRetry(
                        Throwable previousThrowable, List<RetryTestResource> previousResponse) {
                      return previousThrowable instanceof SocketException;
                    }
                  },
                  NanoClock.getDefaultClock());
          if (!existingResources.isEmpty()) {
            LOGGER.info(
                "Test Server already has retry tests in it, is it running outside the tests?");
          }
          base.evaluate();
          success = true;
        } catch (RetryHelperException e) {
          dumpServerLogs(outFile, errFile);
          throw new IllegalStateException(
              "Failed to connect to server within a reasonable amount of time. Host url: "
                  + baseUri,
              e.getCause());
        } finally {
          process.destroy();
          if (cleanupStrategy == CleanupStrategy.ALWAYS
              || (success && cleanupStrategy == CleanupStrategy.ONLY_ON_SUCCESS)) {
            Files.delete(errPath);
            Files.delete(outPath);
            Files.delete(tempDirectory);
          }
        }
      }
    };
  }

  private void dumpServerLogs(File outFile, File errFile) throws IOException {
    try {
      LOGGER.warning("Dumping contents of stdout");
      dumpServerLog("stdout", outFile);
    } finally {
      LOGGER.warning("Dumping contents of stderr");
      dumpServerLog("stderr", errFile);
    }
  }

  private void dumpServerLog(String prefix, File out) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(out))) {
      String line;
      while ((line = reader.readLine()) != null) {
        LOGGER.warning("<" + prefix + "> " + line);
      }
    }
  }

  static Builder newBuilder() {
    return new Builder();
  }

  static final class RetryTestResource {
    public String id;
    public Boolean completed;
    public JsonObject instructions;

    @Override
    public String toString() {
      return "RetryTestResource{"
          + "id='"
          + id
          + '\''
          + ", completed="
          + completed
          + ", instructions="
          + instructions
          + '}';
    }
  }

  static final class Builder {
    private static final String DEFAULT_BASE_URI = "http://localhost:9000";
    private static final String DEFAULT_IMAGE_NAME =
        "gcr.io/cloud-devrel-public-resources/storage-testbench";
    private static final String DEFAULT_IMAGE_TAG = "v0.8.0";

    private boolean ignorePullError;
    private String baseUri;
    private String dockerImageName;
    private String dockerImageTag;
    private CleanupStrategy cleanupStrategy;

    public Builder() {
      this(false, DEFAULT_BASE_URI, DEFAULT_IMAGE_NAME, DEFAULT_IMAGE_TAG, CleanupStrategy.ALWAYS);
    }

    public Builder(
        boolean ignorePullError,
        String baseUri,
        String dockerImageName,
        String dockerImageTag,
        CleanupStrategy cleanupStrategy) {
      this.ignorePullError = ignorePullError;
      this.baseUri = baseUri;
      this.dockerImageName = dockerImageName;
      this.dockerImageTag = dockerImageTag;
      this.cleanupStrategy = cleanupStrategy;
    }

    public Builder setCleanupStraegy(CleanupStrategy cleanupStrategy) {
      this.cleanupStrategy = requireNonNull(cleanupStrategy, "cleanupStrategy must be non null");
      return this;
    }

    public Builder setIgnorePullError(boolean ignorePullError) {
      this.ignorePullError = ignorePullError;
      return this;
    }

    public Builder setBaseUri(String baseUri) {
      this.baseUri = requireNonNull(baseUri, "host must be non null");
      return this;
    }

    public Builder setDockerImageName(String dockerImageName) {
      this.dockerImageName = requireNonNull(dockerImageName, "dockerImageName must be non null");
      return this;
    }

    public Builder setDockerImageTag(String dockerImageTag) {
      this.dockerImageTag = requireNonNull(dockerImageTag, "dockerImageTag must be non null");
      return this;
    }

    public TestBench build() {
      return new TestBench(
          ignorePullError, baseUri, dockerImageName, dockerImageTag, cleanupStrategy);
    }
  }
}
