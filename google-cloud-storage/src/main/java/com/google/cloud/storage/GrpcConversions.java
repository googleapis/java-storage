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

import static com.google.cloud.storage.Utils.todo;

import com.google.cloud.storage.Conversions.Codec;
import com.google.storage.v2.Object;

final class GrpcConversions {
  static final GrpcConversions INSTANCE = new GrpcConversions();

  private final Codec<?, ?> entityCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> objectAclCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> bucketAclCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> hmacKeyMetadataCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> hmacKeyCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> serviceAccountCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> corsCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> loggingCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> iamConfigurationCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> lifecycleRuleCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> deleteRuleCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> bucketInfoCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> customerEncryptionCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<BlobId, Object> blobIdCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> blobInfoCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> notificationInfoCodec = Codec.of(Utils::todo, Utils::todo);

  private GrpcConversions() {}

  Codec<?, ?> entity() {
    return todo();
  }

  Codec<?, ?> objectAcl() {
    return todo();
  }

  Codec<?, ?> bucketAcl() {
    return todo();
  }

  Codec<?, ?> hmacKeyMetadata() {
    return todo();
  }

  Codec<?, ?> hmacKey() {
    return todo();
  }

  Codec<?, ?> serviceAccount() {
    return todo();
  }

  Codec<?, ?> cors() {
    return todo();
  }

  Codec<?, ?> logging() {
    return todo();
  }

  Codec<?, ?> iamConfiguration() {
    return todo();
  }

  Codec<?, ?> lifecycleRule() {
    return todo();
  }

  Codec<?, ?> deleteRule() {
    return todo();
  }

  Codec<?, ?> bucketInfo() {
    return todo();
  }

  Codec<?, ?> customerEncryption() {
    return todo();
  }

  Codec<BlobId, Object> blobId() {
    return blobIdCodec;
  }

  Codec<?, ?> blobInfo() {
    return todo();
  }

  Codec<?, ?> notificationInfo() {
    return todo();
  }
}
