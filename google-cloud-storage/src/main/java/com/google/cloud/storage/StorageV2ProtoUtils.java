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

import static com.google.common.base.Preconditions.checkArgument;

import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.storage.v2.ReadObjectRequest;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class StorageV2ProtoUtils {

  private static final String VALIDATION_TEMPLATE = "0 <= offset <= limit (0 <= %s <= %s)";

  private static final Pattern PROTO_TO_STRING_NEW_LINES = Pattern.compile(" *\\n *");

  private StorageV2ProtoUtils() {}

  @NonNull
  static ReadObjectRequest seekReadObjectRequest(
      @NonNull ReadObjectRequest request, @Nullable Long offset, @Nullable Long limit) {
    validate(offset, limit);
    ReadObjectRequest req = request;

    boolean setOffset = offset != null && (offset > 0 || offset != req.getReadOffset());
    boolean setLimit = limit != null && (limit < Long.MAX_VALUE || limit != req.getReadLimit());
    if (setOffset || setLimit) {
      ReadObjectRequest.Builder b = req.toBuilder();
      if (setOffset) {
        b.setReadOffset(offset);
      }
      if (setLimit) {
        b.setReadLimit(limit);
      }
      req = b.build();
    }
    return req;
  }

  private static void validate(@Nullable Long offset, @Nullable Long limit) {
    boolean offsetNull = offset == null;
    boolean limitNull = limit == null;

    if (offsetNull && limitNull) {
      return;
    }

    if (!offsetNull) {
      checkArgument(0 <= offset, VALIDATION_TEMPLATE, offset, limit);
    }

    if (!limitNull) {
      checkArgument(0 <= limit, VALIDATION_TEMPLATE, offset, limit);
    }

    if (!offsetNull && !limitNull) {
      checkArgument(offset <= limit, VALIDATION_TEMPLATE, offset, limit);
    }
  }

  @NonNull
  static String fmtProto(@NonNull final MessageOrBuilder msg) {
    final Message message = resolve(msg);
    return "{ "
        + PROTO_TO_STRING_NEW_LINES.matcher(message.toString()).replaceAll(" ").trim()
        + " }";
  }

  static Message resolve(@NonNull MessageOrBuilder msg) {
    final Message message;
    if (msg instanceof Message) {
      message = (Message) msg;
    } else {
      message = ((Message.Builder) msg).build();
    }
    return message;
  }
}
