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

package com.google.cloud.storage.it;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.google.api.client.json.JsonParser;
import com.google.api.gax.rpc.FixedHeaderProvider;
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.NoCredentials;
import com.google.cloud.WriteChannel;
import com.google.cloud.conformance.storage.v1.InstructionList;
import com.google.cloud.conformance.storage.v1.Method;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.DataGeneration;
import com.google.cloud.storage.PackagePrivateMethodWorkarounds;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.conformance.retry.TestBench;
import com.google.cloud.storage.conformance.retry.TestBench.RetryTestResource;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.Reflection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

public final class ITBlobWriteChannelTest {
  private static final Logger LOGGER = Logger.getLogger(ITBlobWriteChannelTest.class.getName());
  private static final String NOW_STRING;

  static {
    Instant now = Clock.systemUTC().instant();
    DateTimeFormatter formatter =
        DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
    NOW_STRING = formatter.format(now);
  }

  @ClassRule
  public static final TestBench testBench =
      TestBench.newBuilder().setContainerName("blob-write-channel-test").build();

  @Rule public final TestName testName = new TestName();

  @Rule public final DataGeneration dataGeneration = new DataGeneration(new Random(1234567890));

  /**
   * Test for unexpected EOF at the beginning of trying to read the json response.
   *
   * <p>The error of this case shows up as an IllegalArgumentException rather than a json parsing
   * error which comes from {@link JsonParser}{@code #startParsing()} which fails to find a node to
   * start parsing.
   */
  @Test
  public void testJsonEOF_0B() throws IOException {
    int contentSize = 512 * 1024;
    int cappedByteCount = 0;

    doJsonUnexpectedEOFTest(contentSize, cappedByteCount);
  }

  /** Test for unexpected EOF 10 bytes into the json response */
  @Test
  public void testJsonEOF_10B() throws IOException {
    int contentSize = 512 * 1024;
    int cappedByteCount = 10;

    doJsonUnexpectedEOFTest(contentSize, cappedByteCount);
  }

  private void doJsonUnexpectedEOFTest(int contentSize, int cappedByteCount) throws IOException {
    String blobPath = String.format("%s/%s/blob", testName.getMethodName(), NOW_STRING);

    BucketInfo bucketInfo = BucketInfo.of(dataGeneration.getBucketName());
    BlobInfo blobInfoGen0 = BlobInfo.newBuilder(bucketInfo, blobPath, 0L).build();

    RetryTestResource retryTestResource =
        RetryTestResource.newRetryTestResource(
            Method.newBuilder().setName("storage.objects.insert").build(),
            InstructionList.newBuilder()
                .addInstructions(
                    String.format("return-broken-stream-final-chunk-after-%dB", cappedByteCount))
                .build());
    RetryTestResource retryTest = testBench.createRetryTest(retryTestResource);

    StorageOptions baseOptions =
        StorageOptions.http()
            .setCredentials(NoCredentials.getInstance())
            .setHost(testBench.getBaseUri())
            .setProjectId("project-id")
            .build();
    StorageRpc noHeader = (StorageRpc) baseOptions.getRpc();
    StorageRpc yesHeader =
        (StorageRpc)
            baseOptions
                .toBuilder()
                .setHeaderProvider(
                    FixedHeaderProvider.create(ImmutableMap.of("x-retry-test-id", retryTest.id)))
                .build()
                .getRpc();
    //noinspection UnstableApiUsage
    StorageOptions storageOptions =
        baseOptions
            .toBuilder()
            .setServiceRpcFactory(
                options ->
                    Reflection.newProxy(
                        StorageRpc.class,
                        (proxy, method, args) -> {
                          try {
                            if ("writeWithResponse".equals(method.getName())) {
                              boolean lastChunk = (boolean) args[5];
                              LOGGER.info(
                                  String.format(
                                      "writeWithResponse called. (lastChunk = %b)", lastChunk));
                              if (lastChunk) {
                                return method.invoke(yesHeader, args);
                              }
                            }
                            return method.invoke(noHeader, args);
                          } catch (Exception e) {
                            if (e.getCause() != null) {
                              throw e.getCause();
                            } else {
                              throw e;
                            }
                          }
                        }))
            .build();

    Storage testStorage = storageOptions.getService();

    testStorage.create(bucketInfo);

    ByteBuffer content = dataGeneration.randByteBuffer(contentSize);
    // create a duplicate to preserve the initial offset and limit for assertion later
    ByteBuffer expected = content.duplicate();

    WriteChannel w = testStorage.writer(blobInfoGen0, BlobWriteOption.generationMatch());
    w.write(content);
    w.close();

    RetryTestResource postRunState = testBench.getRetryTest(retryTest);
    assertTrue(postRunState.completed);

    Optional<StorageObject> optionalStorageObject =
        PackagePrivateMethodWorkarounds.maybeGetStorageObjectFunction().apply(w);

    assertTrue(optionalStorageObject.isPresent());
    StorageObject storageObject = optionalStorageObject.get();
    assertThat(storageObject.getName()).isEqualTo(blobInfoGen0.getName());

    // construct a new blob id, without a generation, so we get the latest when we perform a get
    BlobId blobIdGen1 = BlobId.of(storageObject.getBucket(), storageObject.getName());
    Blob blobGen2 = testStorage.get(blobIdGen1);
    assertEquals(contentSize, (long) blobGen2.getSize());
    assertNotEquals(blobInfoGen0.getGeneration(), blobGen2.getGeneration());
    ByteArrayOutputStream actualData = new ByteArrayOutputStream();
    blobGen2.downloadTo(actualData);
    ByteBuffer actual = ByteBuffer.wrap(actualData.toByteArray());
    assertEquals(expected, actual);
  }
}
