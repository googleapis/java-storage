/*
 * Copyright 2025 Google LLC
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

package com.google.cloud.storage.spi.v1;

import static com.google.common.truth.Truth.assertThat;

import com.google.api.client.http.EmptyContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Cors;
import com.google.cloud.storage.Cors.Origin;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.TemporaryBucket;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.registry.Generator;
import com.google.cloud.storage.spi.v1.StorageRpc.Option;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@CrossRun(
    backends = {Backend.PROD},
    transports = {Transport.HTTP})
public final class ITStartResumableUploadTest {

  @Inject public Storage storage;
  @Inject public Generator generator;

  @Test
  public void cors() throws Exception {
    try (TemporaryBucket tmpBucket =
        TemporaryBucket.newBuilder()
            .setStorage(storage)
            .setBucketInfo(
                BucketInfo.newBuilder("java-storage-" + UUID.randomUUID())
                    .setCors(
                        ImmutableList.of(
                            Cors.newBuilder()
                                .setOrigins(ImmutableList.of(Origin.of("fake.fake.fake")))
                                .setMethods(ImmutableList.copyOf(HttpMethod.values()))
                                .setMaxAgeSeconds(15 * 60) // 15 minutes
                                .build()))
                    .build())
            .build()) {

      BucketInfo bucket = tmpBucket.getBucket();

      String location =
          ((StorageRpc) storage.getOptions().getRpc())
              .open(
                  new StorageObject()
                      .setBucket(bucket.getName())
                      .setName("hackery/" + UUID.randomUUID()),
                  ImmutableMap.of(Option.IF_GENERATION_MATCH, 0L));
      assertThat(location).isNotEmpty();

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      {
        HttpRequestFactory fac = new NetHttpTransport().createRequestFactory();
        HttpRequest req = fac.buildPutRequest(new GenericUrl(location), new EmptyContent());
        req.getHeaders().set("User-Agent", "unauthed");
        req.getHeaders().set("Content-Range", "bytes */0");
        HttpResponse res = req.execute();

        try (InputStream inputStream = res.getContent()) {
          ByteStreams.copy(inputStream, out);
        }
      }
      String json = new String(out.toByteArray(), StandardCharsets.UTF_8);
      StorageObject storageObject = new JacksonFactory().fromString(json, StorageObject.class);
      assertThat(storageObject).isNotNull();
    }
  }
}
