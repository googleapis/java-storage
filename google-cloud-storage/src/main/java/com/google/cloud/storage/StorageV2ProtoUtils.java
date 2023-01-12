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

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.Printer;
import com.google.storage.v2.BucketAccessControl;
import com.google.storage.v2.ObjectAccessControl;
import com.google.storage.v2.ReadObjectRequest;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;

final class StorageV2ProtoUtils {

  private static final String VALIDATION_TEMPLATE =
      "offset >= 0 && limit >= 0 (%s >= 0 && %s >= 0)";

  private static final Printer PROTO_PRINTER =
      JsonFormat.printer().omittingInsignificantWhitespace().preservingProtoFieldNames();

  private StorageV2ProtoUtils() {}

  // TODO: can we eliminate this method all together?
  @NonNull
  static ReadObjectRequest seekReadObjectRequest(
      @NonNull ReadObjectRequest request, @NonNull ByteRangeSpec byteRangeSpec) {

    long offset = byteRangeSpec.beginOffset();
    long limit = byteRangeSpec.length();
    ReadObjectRequest req = request;

    boolean setOffset = (offset > 0 && offset != req.getReadOffset());
    boolean setLimit = (limit < ByteRangeSpec.EFFECTIVE_INFINITY && limit != req.getReadLimit());
    if (setOffset || setLimit) {
      req = byteRangeSpec.seekReadObjectRequest(req.toBuilder()).build();
    }
    return req;
  }

  @NonNull
  static String fmtProto(@NonNull final MessageOrBuilder msg) {
    try {
      return PROTO_PRINTER.print(msg);
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * When evaluating an {@link ObjectAccessControl} entity, look at both {@code entity} (generally
   * project number format) and {@code entity_alt} (generally project id format).
   */
  static Predicate<ObjectAccessControl> objectAclEntityOrAltEq(String s) {
    return oAcl -> oAcl.getEntity().equals(s) || oAcl.getEntityAlt().equals(s);
  }

  /**
   * When evaluating an {@link BucketAccessControl} entity, look at both {@code entity} (generally
   * project number format) and {@code entity_alt} (generally project id format).
   */
  static Predicate<BucketAccessControl> bucketAclEntityOrAltEq(String s) {
    return oAcl -> oAcl.getEntity().equals(s) || oAcl.getEntityAlt().equals(s);
  }
}
