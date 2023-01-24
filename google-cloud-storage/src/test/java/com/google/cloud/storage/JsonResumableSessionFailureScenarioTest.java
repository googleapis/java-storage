/*
 * Copyright 2023 Google LLC
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

import static com.google.cloud.storage.JsonResumableSessionFailureScenario.isContinue;
import static com.google.cloud.storage.JsonResumableSessionFailureScenario.isOk;
import static com.google.common.truth.Truth.assertThat;

import com.google.api.client.http.EmptyContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.services.storage.model.StorageObject;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

public final class JsonResumableSessionFailureScenarioTest {
  private static final GsonFactory gson = GsonFactory.getDefaultInstance();

  @Test
  public void isOk_200() {
    assertThat(isOk(200)).isTrue();
  }

  @Test
  public void isOk_201() {
    assertThat(isOk(201)).isTrue();
  }

  @Test
  public void isContinue_308() {
    assertThat(isContinue(308)).isTrue();
  }

  @Test
  public void toStorageException_ioExceptionDuringContentResolutionAddedAsSuppressed()
      throws IOException {
    HttpRequest req =
        new MockHttpTransport()
            .createRequestFactory()
            .buildPutRequest(new GenericUrl("http://localhost:80980"), new EmptyContent());
    req.getHeaders().setContentLength(0L).setContentRange(HttpContentRange.of(0).getHeaderValue());

    HttpResponse resp = req.execute();
    resp.getHeaders().setContentType("text/plain; charset=utf-8").setContentLength(5L);

    StorageException storageException =
        JsonResumableSessionFailureScenario.SCENARIO_1.toStorageException(
            "uploadId",
            resp,
            new Cause(),
            () -> {
              throw new Kaboom();
            });

    assertThat(storageException.getCode()).isEqualTo(0);
    assertThat(storageException).hasCauseThat().isInstanceOf(Cause.class);
    assertThat(storageException.getSuppressed()).isNotEmpty();
    assertThat(storageException.getSuppressed()[0]).isInstanceOf(StorageException.class);
    assertThat(storageException.getSuppressed()[0]).hasCauseThat().isInstanceOf(Kaboom.class);
  }

  @Test
  public void multilineResponseBodyIsProperlyPrefixed() throws Exception {
    StorageObject so = new StorageObject();
    so.setName("object-name")
        .setSize(BigInteger.ZERO)
        .setGeneration(1L)
        .setMetageneration(2L)
        .setMetadata(
            ImmutableMap.of(
                "k1", "v1",
                "k2", "v2"));
    final String json = gson.toPrettyString(so);

    byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
    HttpRequest req =
        new MockHttpTransport()
            .createRequestFactory()
            .buildPutRequest(new GenericUrl("http://localhost:80980"), new EmptyContent());
    req.getHeaders().setContentLength(0L);

    HttpResponse resp = req.execute();
    resp.getHeaders()
        .setContentType("application/json; charset=utf-8")
        .setContentLength((long) bytes.length);

    StorageException storageException =
        JsonResumableSessionFailureScenario.SCENARIO_0.toStorageException(
            "uploadId", resp, null, () -> json);

    assertThat(storageException.getCode()).isEqualTo(0);
    assertThat(storageException).hasMessageThat().contains("\t|<   \"generation\": \"1\",\n");
  }

  @Test
  public void xGoogStoredHeadersIncludedIfPresent() throws IOException {
    HttpRequest req =
        new MockHttpTransport()
            .createRequestFactory()
            .buildPutRequest(new GenericUrl("http://localhost:80980"), new EmptyContent());
    req.getHeaders().setContentLength(0L);

    HttpResponse resp = req.execute();
    resp.getHeaders()
        .set("X-Goog-Stored-Content-Length", "5")
        .set("x-goog-stored-content-encoding", "identity")
        .set("X-GOOG-STORED-SOMETHING", "blah")
        .setContentLength(0L);

    StorageException storageException =
        JsonResumableSessionFailureScenario.SCENARIO_0.toStorageException(
            "uploadId", resp, null, () -> null);

    assertThat(storageException.getCode()).isEqualTo(0);
    assertThat(storageException).hasMessageThat().contains("|< x-goog-stored-content-length: 5");
    assertThat(storageException)
        .hasMessageThat()
        .contains("|< x-goog-stored-content-encoding: identity");
    assertThat(storageException).hasMessageThat().contains("|< x-goog-stored-something: blah");
  }

  private static final class Cause extends RuntimeException {

    private Cause() {
      super("Cause");
    }
  }

  private static final class Kaboom extends IOException {

    private Kaboom() {
      super("Kaboom!!!");
    }
  }
}
