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
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertArrayEquals;
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
import com.google.cloud.storage.DataGenerator;
import com.google.cloud.storage.HttpStorageOptions;
import com.google.cloud.storage.PackagePrivateMethodWorkarounds;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.annotations.SingleBackend;
import com.google.cloud.storage.it.runner.registry.Generator;
import com.google.cloud.storage.it.runner.registry.TestBench;
import com.google.cloud.storage.it.runner.registry.TestBench.RetryTestResource;
import com.google.cloud.storage.spi.StorageRpcFactory;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.cloud.storage.spi.v1.StorageRpc.Option;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.AbstractInvocationHandler;
import com.google.common.reflect.Reflection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

@RunWith(StorageITRunner.class)
@SingleBackend(Backend.TEST_BENCH)
public final class ITBlobWriteChannelTest {

  private static final Logger LOGGER = Logger.getLogger(ITBlobWriteChannelTest.class.getName());
  private static final String NOW_STRING;
  private static final String BLOB_STRING_CONTENT = "Hello Google Cloud Storage!";

  static {
    Instant now = Clock.systemUTC().instant();
    DateTimeFormatter formatter =
        DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
    NOW_STRING = formatter.format(now);
  }

  @Inject public TestBench testBench;

  @Inject public Generator generator;

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

  @Test
  public void blobWriteChannel_handlesRecoveryOnLastChunkWhenGenerationIsPresent_multipleChunks()
      throws IOException {
    int _2MiB = 256 * 1024;
    int contentSize = 292_617;

    blobWriteChannel_handlesRecoveryOnLastChunkWhenGenerationIsPresent(_2MiB, contentSize);
  }

  @Test
  public void blobWriteChannel_handlesRecoveryOnLastChunkWhenGenerationIsPresent_singleChunk()
      throws IOException {
    int _4MiB = 256 * 1024 * 2;
    int contentSize = 292_617;

    blobWriteChannel_handlesRecoveryOnLastChunkWhenGenerationIsPresent(_4MiB, contentSize);
  }

  @Test
  public void testWriteChannelExistingBlob() throws IOException {
    HttpStorageOptions baseStorageOptions =
        StorageOptions.http()
            .setCredentials(NoCredentials.getInstance())
            .setHost(testBench.getBaseUri())
            .setProjectId("test-project-id")
            .build();
    Storage storage = baseStorageOptions.getService();
    Instant now = Clock.systemUTC().instant();
    DateTimeFormatter formatter =
        DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
    String nowString = formatter.format(now);
    BucketInfo bucketInfo = BucketInfo.of(generator.randomBucketName());
    String blobPath = String.format("%s/%s/blob", generator.randomObjectName(), nowString);
    BlobId blobId = BlobId.of(bucketInfo.getName(), blobPath);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    storage.create(bucketInfo);
    storage.create(blobInfo);
    byte[] stringBytes;
    try (WriteChannel writer = storage.writer(blobInfo)) {
      stringBytes = BLOB_STRING_CONTENT.getBytes(UTF_8);
      writer.write(ByteBuffer.wrap(stringBytes));
    }
    assertArrayEquals(stringBytes, storage.readAllBytes(blobInfo.getBlobId()));
    assertTrue(storage.delete(bucketInfo.getName(), blobInfo.getName()));
  }

  private void doJsonUnexpectedEOFTest(int contentSize, int cappedByteCount) throws IOException {
    String blobPath = String.format("%s/%s/blob", generator.randomObjectName(), NOW_STRING);

    BucketInfo bucketInfo = BucketInfo.of(generator.randomBucketName());
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
                              LOGGER.fine(
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

    ByteBuffer content = DataGenerator.base64Characters().genByteBuffer(contentSize);
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

  private void blobWriteChannel_handlesRecoveryOnLastChunkWhenGenerationIsPresent(
      int chunkSize, int contentSize) throws IOException {
    Instant now = Clock.systemUTC().instant();
    DateTimeFormatter formatter =
        DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
    String nowString = formatter.format(now);
    BucketInfo bucketInfo = BucketInfo.of(generator.randomBucketName());
    String blobPath = String.format("%s/%s/blob", generator.randomObjectName(), nowString);
    BlobId blobId = BlobId.of(bucketInfo.getName(), blobPath);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

    ByteBuffer contentGen1 = DataGenerator.base64Characters().genByteBuffer(contentSize);
    ByteBuffer contentGen2 = DataGenerator.base64Characters().genByteBuffer(contentSize);
    ByteBuffer contentGen2Expected = contentGen2.duplicate();
    HttpStorageOptions baseStorageOptions =
        StorageOptions.http()
            .setCredentials(NoCredentials.getInstance())
            .setHost(testBench.getBaseUri())
            .setProjectId("test-project-id")
            .build();
    Storage storage = baseStorageOptions.getService();
    storage.create(bucketInfo);
    WriteChannel ww = storage.writer(blobInfo);
    ww.setChunkSize(chunkSize);
    ww.write(contentGen1);
    ww.close();

    Blob blobGen1 = storage.get(blobId);

    final AtomicBoolean exceptionThrown = new AtomicBoolean(false);

    Storage testStorage =
        baseStorageOptions
            .toBuilder()
            .setServiceRpcFactory(
                new StorageRpcFactory() {
                  /**
                   * Here we're creating a proxy of StorageRpc where we can delegate all calls to
                   * the normal implementation, except in the case of {@link
                   * StorageRpc#writeWithResponse(String, byte[], int, long, int, boolean)} where
                   * {@code lastChunk == true}. We allow the call to execute, but instead of
                   * returning the result we throw an IOException to simulate a prematurely close
                   * connection. This behavior is to ensure appropriate handling of a completed
                   * upload where the ACK wasn't received. In particular, if an upload is initiated
                   * against an object where an {@link Option#IF_GENERATION_MATCH} simply calling
                   * get on an object can result in a 404 because the object that is created while
                   * the BlobWriteChannel is executing will be a new generation.
                   */
                  @Override
                  public StorageRpc create(final StorageOptions options) {
                    return Reflection.newProxy(
                        StorageRpc.class,
                        new AbstractInvocationHandler() {
                          final StorageRpc delegate = (StorageRpc) baseStorageOptions.getRpc();

                          @Override
                          protected Object handleInvocation(
                              Object proxy, java.lang.reflect.Method method, Object[] args)
                              throws Throwable {
                            if ("writeWithResponse".equals(method.getName())) {
                              Object result = method.invoke(delegate, args);
                              boolean lastChunk = (boolean) args[5];
                              // if we're on the lastChunk simulate a connection failure which
                              // happens after the request was processed but before response could
                              // be received by the client.
                              if (lastChunk) {
                                exceptionThrown.set(true);
                                throw StorageException.translate(
                                    new IOException("simulated Connection closed prematurely"));
                              } else {
                                return result;
                              }
                            }
                            return method.invoke(delegate, args);
                          }
                        });
                  }
                })
            .build()
            .getService();
    try (WriteChannel w = testStorage.writer(blobGen1, BlobWriteOption.generationMatch())) {
      w.setChunkSize(chunkSize);

      w.write(contentGen2);
    }

    assertTrue("Expected an exception to be thrown for the last chunk", exceptionThrown.get());

    Blob blobGen2 = storage.get(blobId);
    assertEquals(contentSize, (long) blobGen2.getSize());
    assertNotEquals(blobInfo.getGeneration(), blobGen2.getGeneration());
    ByteArrayOutputStream actualData = new ByteArrayOutputStream();
    blobGen2.downloadTo(actualData);
    assertEquals(contentGen2Expected, ByteBuffer.wrap(actualData.toByteArray()));
  }
}
