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
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.threeten.bp.Duration;

final class TestServer implements TestRule {

  private static final Logger LOGGER = Logger.getLogger(TestServer.class.getName());

  private final String host;
  private final String dockerImageName;
  private final String dockerImageTag;

  private final Gson gson;
  private final HttpRequestFactory requestFactory;

  private TestServer(String host, String dockerImageName, String dockerImageTag) {
    this.host = host;
    this.dockerImageName = dockerImageName;
    this.dockerImageTag = dockerImageTag;
    this.gson = new Gson();
    this.requestFactory = new NetHttpTransport.Builder().build().createRequestFactory();
  }

  String getHost() {
    return host;
  }

  RetryTestResource createRetryTest(RetryTestResource retryTestResource) throws IOException {
    GenericUrl url = new GenericUrl(host + "/retry_test");
    String jsonString = gson.toJson(retryTestResource);
    HttpContent content =
        new ByteArrayContent("application/json", jsonString.getBytes(StandardCharsets.UTF_8));
    HttpRequest req = requestFactory.buildPostRequest(url, content);
    setCommonReqProperties(req);
    HttpResponse resp = req.execute();
    RetryTestResource result = gson.fromJson(resp.parseAsString(), RetryTestResource.class);
    resp.disconnect();
    return result;
  }

  void deleteRetryTest(RetryTestResource retryTestResource) throws IOException {
    GenericUrl url = new GenericUrl(host + "/retry_test/" + retryTestResource.id);
    HttpRequest req = requestFactory.buildDeleteRequest(url);
    setCommonReqProperties(req);
    HttpResponse resp = req.execute();
    resp.disconnect();
  }

  RetryTestResource getRetryTest(RetryTestResource retryTestResource) throws IOException {
    GenericUrl url = new GenericUrl(host + "/retry_test/" + retryTestResource.id);
    HttpRequest req = requestFactory.buildGetRequest(url);
    HttpResponse resp = req.execute();
    setCommonReqProperties(req);
    RetryTestResource result = gson.fromJson(resp.parseAsString(), RetryTestResource.class);
    resp.disconnect();
    return result;
  }

  List<RetryTestResource> listRetryTests() throws IOException {
    GenericUrl url = new GenericUrl(host + "/retry_tests");
    HttpRequest req = requestFactory.buildGetRequest(url);
    setCommonReqProperties(req);
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
        File outFile = tempDirectory.resolve("stdout").toFile();
        File errFile = tempDirectory.resolve("stderr").toFile();
        LOGGER.info("Redirecting server stdout to: " + outFile.getAbsolutePath());
        LOGGER.info("Redirecting server stderr to: " + errFile.getAbsolutePath());
        final Process process =
            new ProcessBuilder()
                .command(
                    "docker",
                    "run",
                    "-i",
                    "--rm",
                    "--net=host",
                    "--name=retry-conformance-server",
                    String.format("%s:%s", dockerImageName, dockerImageTag))
                .redirectOutput(outFile)
                .redirectError(errFile)
                .start();
        try {
          // wait for the server to come up
          List<RetryTestResource> existingResources =
              runWithRetries(
                  new Callable<List<RetryTestResource>>() {
                    @Override
                    public List<RetryTestResource> call() throws Exception {
                      return listRetryTests();
                    }
                  },
                  RetrySettings.newBuilder()
                      .setTotalTimeout(Duration.ofMinutes(1))
                      .setInitialRetryDelay(Duration.ofMillis(20))
                      .setMaxRetryDelay(Duration.ofMillis(500))
                      .build(),
                  new BasicResultRetryAlgorithm<List<RetryTestResource>>() {
                    @Override
                    public boolean shouldRetry(
                        Throwable previousThrowable, List<RetryTestResource> previousResponse) {
                      return previousThrowable instanceof ConnectException;
                    }
                  },
                  NanoClock.getDefaultClock());
          if (!existingResources.isEmpty()) {
            LOGGER.info(
                "Test Server already has retry tests in it, is it running outside the tests?");
          }
          base.evaluate();
        } finally {
          process.destroy();
        }
      }
    };
  }

  static Builder newBuilder() {
    return new Builder();
  }

  private void setCommonReqProperties(HttpRequest req) {
    req.setCurlLoggingEnabled(false);
    req.getHeaders().setAccept("application/json");
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
    private static final String DEFAULT_HOST = "http://localhost:9000";
    private static final String DEFAULT_IMAGE_NAME =
        "gcr.io/cloud-devrel-kokoro-resources/conformance-tests/storage/retry";
    private static final String DEFAULT_IMAGE_TAG = "2f404c1_server-790904f124";

    private String host;
    private String dockerImageName;
    private String dockerImageTag;

    public Builder() {
      this(DEFAULT_HOST, DEFAULT_IMAGE_NAME, DEFAULT_IMAGE_TAG);
    }

    public Builder(String host, String dockerImageName, String dockerImageTag) {
      this.host = host;
      this.dockerImageName = dockerImageName;
      this.dockerImageTag = dockerImageTag;
    }

    public Builder setHost(String host) {
      this.host = requireNonNull(host, "host must be non null");
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

    public TestServer build() {
      return new TestServer(host, dockerImageName, dockerImageTag);
    }
  }
}
