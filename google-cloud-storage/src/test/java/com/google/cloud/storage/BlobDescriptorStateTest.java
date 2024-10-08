/*
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

package com.google.cloud.storage;

import static com.google.cloud.storage.TestUtils.assertAll;
import static com.google.common.truth.Truth.assertThat;

import com.google.api.core.SettableApiFuture;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.cloud.storage.BlobDescriptorState.OpenArguments;
import com.google.cloud.storage.BlobDescriptorStreamRead.AccumulatingRead;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.ByteString;
import com.google.storage.v2.BidiReadHandle;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.BidiReadObjectResponse;
import com.google.storage.v2.BidiReadObjectSpec;
import com.google.storage.v2.CommonObjectRequestParams;
import com.google.storage.v2.Object;
import com.google.storage.v2.ReadRange;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public final class BlobDescriptorStateTest {

  @Test
  public void getOpenArguments_includesAllRelevantModifications() throws Exception {
    BidiReadObjectRequest base =
        BidiReadObjectRequest.newBuilder()
            .setReadObjectSpec(
                BidiReadObjectSpec.newBuilder()
                    .setBucket("projects/_/buckets/my-bucket")
                    .setObject("my-object")
                    .setCommonObjectRequestParams(
                        CommonObjectRequestParams.newBuilder()
                            .setEncryptionKeyBytes(ByteString.copyFromUtf8("asdf"))
                            .setEncryptionAlgorithm("SHA-256")
                            .setEncryptionKeySha256Bytes(ByteString.copyFromUtf8("FDSA"))))
            .build();

    BidiReadObjectResponse resp =
        BidiReadObjectResponse.newBuilder()
            .setMetadata(
                Object.newBuilder()
                    .setBucket("projects/_/buckets/my-bucket")
                    .setName("my-object")
                    .setGeneration(387)
                    .setSize(98_765_432))
            .setReadHandle(
                BidiReadHandle.newBuilder().setHandle(ByteString.copyFromUtf8("read_handle_1")))
            .build();

    BlobDescriptorState state = new BlobDescriptorState(GrpcCallContext.createDefault(), base);

    state.setMetadata(resp.getMetadata());
    state.setBidiReadHandle(resp.getReadHandle());

    RetryContext neverRetry = RetryContext.neverRetry();
    SettableApiFuture<byte[]> f1 = SettableApiFuture.create();
    SettableApiFuture<byte[]> f2 = SettableApiFuture.create();

    AccumulatingRead<byte[]> r1 =
        BlobDescriptorStreamRead.createByteArrayAccumulatingRead(
            1, RangeSpec.of(3, 4), neverRetry, f1);
    AccumulatingRead<byte[]> r2 =
        BlobDescriptorStreamRead.createByteArrayAccumulatingRead(
            2, RangeSpec.of(19, 14), neverRetry, f2);

    state.putOutstandingRead(1, r1);
    state.putOutstandingRead(2, r2);

    OpenArguments expected =
        OpenArguments.of(
            GrpcCallContext.createDefault()
                .withExtraHeaders(
                    ImmutableMap.of(
                        "x-goog-request-params",
                        ImmutableList.of("bucket=projects/_/buckets/my-bucket"))),
            BidiReadObjectRequest.newBuilder()
                .setReadObjectSpec(
                    BidiReadObjectSpec.newBuilder()
                        .setBucket("projects/_/buckets/my-bucket")
                        .setObject("my-object")
                        .setGeneration(387)
                        .setCommonObjectRequestParams(
                            CommonObjectRequestParams.newBuilder()
                                .setEncryptionKeyBytes(ByteString.copyFromUtf8("asdf"))
                                .setEncryptionAlgorithm("SHA-256")
                                .setEncryptionKeySha256Bytes(ByteString.copyFromUtf8("FDSA")))
                        .setReadHandle(
                            BidiReadHandle.newBuilder()
                                .setHandle(ByteString.copyFromUtf8("read_handle_1"))))
                .addReadRanges(
                    ReadRange.newBuilder().setReadId(1).setReadOffset(3).setReadLength(4).build())
                .addReadRanges(
                    ReadRange.newBuilder().setReadId(2).setReadOffset(19).setReadLength(14).build())
                .build());

    OpenArguments actual = state.getOpenArguments();
    assertAll(
        () -> assertThat(actual.getReq()).isEqualTo(expected.getReq()),
        () ->
            assertThat(actual.getCtx().getExtraHeaders())
                .isEqualTo(expected.getCtx().getExtraHeaders()));
  }

  @Test
  public void redirectTokenPresentInHeadersIfNonNull() {
    BidiReadObjectRequest base =
        BidiReadObjectRequest.newBuilder()
            .setReadObjectSpec(
                BidiReadObjectSpec.newBuilder()
                    .setBucket("projects/_/buckets/my-bucket")
                    .setObject("my-object"))
            .build();

    BlobDescriptorState state = new BlobDescriptorState(GrpcCallContext.createDefault(), base);

    state.setRoutingToken("token-1");

    OpenArguments openArguments = state.getOpenArguments();
    GrpcCallContext ctx = openArguments.getCtx();
    Map<String, List<String>> extraHeaders = ctx.getExtraHeaders();
    Map<String, List<String>> expected =
        ImmutableMap.of(
            "x-goog-request-params",
            ImmutableList.of("bucket=projects/_/buckets/my-bucket&routing_token=token-1"));

    assertThat(extraHeaders).isEqualTo(expected);
  }

  @Test
  public void redirectTokenNotPresentInHeadersIfNull() {
    BidiReadObjectRequest base =
        BidiReadObjectRequest.newBuilder()
            .setReadObjectSpec(
                BidiReadObjectSpec.newBuilder()
                    .setBucket("projects/_/buckets/my-bucket")
                    .setObject("my-object"))
            .build();

    BlobDescriptorState state = new BlobDescriptorState(GrpcCallContext.createDefault(), base);

    state.setRoutingToken(null);

    OpenArguments openArguments = state.getOpenArguments();
    GrpcCallContext ctx = openArguments.getCtx();
    Map<String, List<String>> extraHeaders = ctx.getExtraHeaders();
    Map<String, List<String>> expected =
        ImmutableMap.of(
            "x-goog-request-params", ImmutableList.of("bucket=projects/_/buckets/my-bucket"));

    assertThat(extraHeaders).isEqualTo(expected);
  }

  @Test
  public void redirectTokenMustNotBeUrlEncoded() {
    BidiReadObjectRequest base =
        BidiReadObjectRequest.newBuilder()
            .setReadObjectSpec(
                BidiReadObjectSpec.newBuilder()
                    .setBucket("projects/_/buckets/my-bucket")
                    .setObject("my-object"))
            .build();

    BlobDescriptorState state = new BlobDescriptorState(GrpcCallContext.createDefault(), base);

    state.setRoutingToken("token%20with%2furl%20encoding");

    OpenArguments openArguments = state.getOpenArguments();
    GrpcCallContext ctx = openArguments.getCtx();
    Map<String, List<String>> extraHeaders = ctx.getExtraHeaders();
    Map<String, List<String>> expected =
        ImmutableMap.of(
            "x-goog-request-params",
            ImmutableList.of(
                "bucket=projects/_/buckets/my-bucket&routing_token=token%20with%2furl%20encoding"));

    assertThat(extraHeaders).isEqualTo(expected);
  }
}
