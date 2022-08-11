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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assume.assumeTrue;

import com.google.api.gax.grpc.GrpcCallContext;
import com.google.cloud.storage.UnifiedOpts.NoOpObjectTargetOpt;
import com.google.cloud.storage.UnifiedOpts.ObjectTargetOpt;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public final class UnifiedOptsGrpcTest {

  @RunWith(Enclosed.class)
  public static final class Opt {

    public static final class Crc32cMatchTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class DecryptionKeyTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class DelimiterTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class DisableGzipContentTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class EncryptionKeyTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class EndOffsetTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class FieldsTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class GenerationMatchTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class GenerationNotMatchTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class KmsKeyNameTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class Md5MatchTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class MetagenerationMatchTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class MetagenerationNotMatchTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class PageSizeTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class PageTokenTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class PredefinedAclTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class PredefinedDefaultObjectAclTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class PrefixTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class ProjectIdTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class ProjectionTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class SourceGenerationMatchTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class SourceGenerationNotMatchTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class SourceMetagenerationMatchTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class SourceMetagenerationNotMatchTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class RequestedPolicyVersionTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class ReturnRawInputStreamTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class ServiceAccountTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class SetContentTypeTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class ShowDeletedKeysTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class StartOffsetTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class UserProjectTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }

    public static final class VersionsFilterTest {

      @Test
      public void name() {
        assumeTrue(false);
      }
    }
  }

  @RunWith(Enclosed.class)
  public static final class Extractor {

    public static final class Crc32cMatchExtractorTest {

      @Test
      public void extractFromBlobInfo_nonNull() {
        BlobInfo info = BlobInfo.newBuilder("b", "o").setCrc32c("crc32c").build();
        ObjectTargetOpt opt = UnifiedOpts.crc32cMatchExtractor().extractFromBlobInfo(info);
        assertThat(opt).isEqualTo(UnifiedOpts.crc32cMatch("crc32c"));
      }

      @Test
      public void extractFromBlobInfo_null() {
        BlobInfo info = BlobInfo.newBuilder("b", "o").build();
        ObjectTargetOpt opt = UnifiedOpts.crc32cMatchExtractor().extractFromBlobInfo(info);
        assertThat(opt).isEqualTo(NoOpObjectTargetOpt.INSTANCE);
      }

      @Test
      public void extractFromBlobId_noop() {
        ObjectTargetOpt opt = UnifiedOpts.crc32cMatchExtractor().extractFromBlobId(null);
        assertThat(opt).isEqualTo(NoOpObjectTargetOpt.INSTANCE);
      }
    }

    public static final class DetectContentTypeTest {

      @Test
      public void hasExtension() {
        BlobInfo info1 = BlobInfo.newBuilder("bucket", "obj.txt").build();
        ObjectTargetOpt opt = UnifiedOpts.detectContentType().extractFromBlobInfo(info1);

        assertThat(opt).isEqualTo(UnifiedOpts.setContentType("text/plain"));
      }

      @Test
      public void noopIfAlreadySpecified() {
        BlobInfo info1 =
            BlobInfo.newBuilder("bucket", "obj.txt").setContentType("text/plain").build();
        ObjectTargetOpt opt = UnifiedOpts.detectContentType().extractFromBlobInfo(info1);

        assertThat(opt).isEqualTo(NoOpObjectTargetOpt.INSTANCE);
      }

      @Test
      public void baseCaseIsApplicationOctetStream() {
        BlobInfo info1 = BlobInfo.newBuilder("bucket", "obj").build();
        ObjectTargetOpt opt = UnifiedOpts.detectContentType().extractFromBlobInfo(info1);

        assertThat(opt).isEqualTo(UnifiedOpts.setContentType("application/octet-stream"));
      }
    }

    public static final class GenerationMatchExtractorTest {
      @Test
      public void extractFromBlobInfo_nonNull() {
        BlobInfo info = BlobInfo.newBuilder("b", "o", 3L).build();
        ObjectTargetOpt opt = UnifiedOpts.generationMatchExtractor().extractFromBlobInfo(info);
        assertThat(opt).isEqualTo(UnifiedOpts.generationMatch(3L));
      }

      @Test
      public void extractFromBlobInfo_null() {
        BlobInfo info = BlobInfo.newBuilder("b", "o").build();
        assertThat(
                assertThrows(
                    IllegalArgumentException.class,
                    () -> UnifiedOpts.generationMatchExtractor().extractFromBlobInfo(info)))
            .hasMessageThat()
            .contains("ifGenerationMatch");
      }

      @Test
      public void extractFromBlobId_nonNull() {
        BlobId id = BlobId.of("b", "o", 3L);
        ObjectTargetOpt opt = UnifiedOpts.generationMatchExtractor().extractFromBlobId(id);
        assertThat(opt).isEqualTo(UnifiedOpts.generationMatch(3L));
      }

      @Test
      public void extractFromBlobId_null() {
        BlobId id = BlobId.of("b", "o");
        assertThat(
                assertThrows(
                    IllegalArgumentException.class,
                    () -> UnifiedOpts.generationMatchExtractor().extractFromBlobId(id)))
            .hasMessageThat()
            .contains("ifGenerationMatch");
      }
    }

    public static final class GenerationNotMatchExtractorTest {
      @Test
      public void extractFromBlobInfo_nonNull() {
        BlobInfo info = BlobInfo.newBuilder("b", "o", 3L).build();
        ObjectTargetOpt opt = UnifiedOpts.generationNotMatchExtractor().extractFromBlobInfo(info);
        assertThat(opt).isEqualTo(UnifiedOpts.generationNotMatch(3L));
      }

      @Test
      public void extractFromBlobInfo_null() {
        BlobInfo info = BlobInfo.newBuilder("b", "o").build();
        assertThat(
                assertThrows(
                    IllegalArgumentException.class,
                    () -> UnifiedOpts.generationNotMatchExtractor().extractFromBlobInfo(info)))
            .hasMessageThat()
            .contains("ifGenerationNotMatch");
      }

      @Test
      public void extractFromBlobId_nonNull() {
        BlobId id = BlobId.of("b", "o", 3L);
        ObjectTargetOpt opt = UnifiedOpts.generationNotMatchExtractor().extractFromBlobId(id);
        assertThat(opt).isEqualTo(UnifiedOpts.generationNotMatch(3L));
      }

      @Test
      public void extractFromBlobId_null() {
        BlobId id = BlobId.of("b", "o");
        assertThat(
                assertThrows(
                    IllegalArgumentException.class,
                    () -> UnifiedOpts.generationNotMatchExtractor().extractFromBlobId(id)))
            .hasMessageThat()
            .contains("ifGenerationNotMatch");
      }
    }

    public static final class Md5MatchExtractorTest {

      @Test
      public void extractFromBlobInfo_nonNull() {
        BlobInfo info = BlobInfo.newBuilder("b", "o").setMd5("md5").build();
        ObjectTargetOpt opt = UnifiedOpts.md5MatchExtractor().extractFromBlobInfo(info);
        assertThat(opt).isEqualTo(UnifiedOpts.md5Match("md5"));
      }

      @Test
      public void extractFromBlobInfo_null() {
        BlobInfo info = BlobInfo.newBuilder("b", "o").build();
        ObjectTargetOpt opt = UnifiedOpts.md5MatchExtractor().extractFromBlobInfo(info);
        assertThat(opt).isEqualTo(NoOpObjectTargetOpt.INSTANCE);
      }

      @Test
      public void extractFromBlobId_noop() {
        ObjectTargetOpt opt = UnifiedOpts.md5MatchExtractor().extractFromBlobId(null);
        assertThat(opt).isEqualTo(NoOpObjectTargetOpt.INSTANCE);
      }
    }

    public static final class MetagenerationMatchExtractorTest {
      @Test
      public void extractFromBlobInfo_nonNull() {
        BlobInfo info = BlobInfo.newBuilder("b", "o").setMetageneration(1L).build();
        ObjectTargetOpt opt = UnifiedOpts.metagenerationMatchExtractor().extractFromBlobInfo(info);
        assertThat(opt).isEqualTo(UnifiedOpts.metagenerationMatch(1L));
      }

      @Test
      public void extractFromBlobInfo_null() {
        BlobInfo info = BlobInfo.newBuilder("b", "o").build();
        assertThat(
                assertThrows(
                    IllegalArgumentException.class,
                    () -> UnifiedOpts.metagenerationMatchExtractor().extractFromBlobInfo(info)))
            .hasMessageThat()
            .contains("ifMetagenerationMatch");
      }

      @Test
      public void extractFromBlobId_noop() {
        ObjectTargetOpt opt = UnifiedOpts.metagenerationMatchExtractor().extractFromBlobId(null);
        assertThat(opt).isEqualTo(NoOpObjectTargetOpt.INSTANCE);
      }

      @Test
      public void extractFromBucketInfo_nonNull() {
        BucketInfo info = BucketInfo.newBuilder("b").setMetageneration(1L).build();
        ObjectTargetOpt opt =
            UnifiedOpts.metagenerationMatchExtractor().extractFromBucketInfo(info);
        assertThat(opt).isEqualTo(UnifiedOpts.metagenerationMatch(1L));
      }

      @Test
      public void extractFromBucketInfo_null() {
        BucketInfo info = BucketInfo.newBuilder("b").build();
        assertThat(
                assertThrows(
                    IllegalArgumentException.class,
                    () -> UnifiedOpts.metagenerationMatchExtractor().extractFromBucketInfo(info)))
            .hasMessageThat()
            .contains("ifMetagenerationMatch");
      }
    }

    public static final class MetagenerationNotMatchExtractorTest {
      @Test
      public void extractFromBlobInfo_nonNull() {
        BlobInfo info = BlobInfo.newBuilder("b", "o").setMetageneration(1L).build();
        ObjectTargetOpt opt =
            UnifiedOpts.metagenerationNotMatchExtractor().extractFromBlobInfo(info);
        assertThat(opt).isEqualTo(UnifiedOpts.metagenerationNotMatch(1L));
      }

      @Test
      public void extractFromBlobInfo_null() {
        BlobInfo info = BlobInfo.newBuilder("b", "o").build();
        assertThat(
                assertThrows(
                    IllegalArgumentException.class,
                    () -> UnifiedOpts.metagenerationNotMatchExtractor().extractFromBlobInfo(info)))
            .hasMessageThat()
            .contains("ifMetagenerationNotMatch");
      }

      @Test
      public void extractFromBlobId_noop() {
        ObjectTargetOpt opt = UnifiedOpts.metagenerationNotMatchExtractor().extractFromBlobId(null);
        assertThat(opt).isEqualTo(NoOpObjectTargetOpt.INSTANCE);
      }

      @Test
      public void extractFromBucketInfo_nonNull() {
        BucketInfo info = BucketInfo.newBuilder("b").setMetageneration(1L).build();
        ObjectTargetOpt opt =
            UnifiedOpts.metagenerationNotMatchExtractor().extractFromBucketInfo(info);
        assertThat(opt).isEqualTo(UnifiedOpts.metagenerationNotMatch(1L));
      }

      @Test
      public void extractFromBucketInfo_null() {
        BucketInfo info = BucketInfo.newBuilder("b").build();
        assertThat(
                assertThrows(
                    IllegalArgumentException.class,
                    () ->
                        UnifiedOpts.metagenerationNotMatchExtractor().extractFromBucketInfo(info)))
            .hasMessageThat()
            .contains("ifMetagenerationNotMatch");
      }
    }
  }

  public static final class NoOpObjectTargetOptTest {
    @Test
    public void grpcMetadataMapper() {
      GrpcCallContext ctx1 = GrpcCallContext.createDefault();
      GrpcCallContext ctx2 = NoOpObjectTargetOpt.INSTANCE.getGrpcMetadataMapper().apply(ctx1);
      assertThat(ctx2).isEqualTo(ctx1);
      assertThat(ctx2).isSameInstanceAs(ctx1);
    }

    @Test
    public void blobInfo() {
      BlobInfo.Builder b1 = BlobInfo.newBuilder("b", "o");
      // "checkpoint" our builder to allow for comparison
      BlobInfo expected = b1.build();
      BlobInfo.Builder b2 = NoOpObjectTargetOpt.INSTANCE.blobInfo().apply(b1);
      assertThat(b2.build()).isEqualTo(expected);
      assertThat(b2).isSameInstanceAs(b1);
    }
  }
}
