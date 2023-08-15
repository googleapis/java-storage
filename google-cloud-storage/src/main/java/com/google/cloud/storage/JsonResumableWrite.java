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

import static com.google.common.base.Preconditions.checkArgument;

import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.common.base.MoreObjects;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

final class JsonResumableWrite implements Serializable {
  private static final long serialVersionUID = 7934407897802252292L;
  private static final Gson gson = new Gson();

  @MonotonicNonNull private transient StorageObject object;
  @MonotonicNonNull private final Map<StorageRpc.Option, ?> options;

  @MonotonicNonNull private final String signedUrl;

  @NonNull private final String uploadId;
  private final long beginOffset;

  private volatile String objectJson;

  private JsonResumableWrite(
      StorageObject object,
      Map<StorageRpc.Option, ?> options,
      String signedUrl,
      @NonNull String uploadId,
      long beginOffset) {
    this.object = object;
    this.options = options;
    this.signedUrl = signedUrl;
    this.uploadId = uploadId;
    this.beginOffset = beginOffset;
  }

  public @NonNull String getUploadId() {
    return uploadId;
  }

  public long getBeginOffset() {
    return beginOffset;
  }

  public JsonResumableWrite withBeginOffset(long newBeginOffset) {
    checkArgument(
        newBeginOffset >= beginOffset,
        "New beginOffset must be >= existing beginOffset (%s >= %s)",
        newBeginOffset,
        beginOffset);
    return new JsonResumableWrite(object, options, signedUrl, uploadId, newBeginOffset);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof JsonResumableWrite)) {
      return false;
    }
    JsonResumableWrite that = (JsonResumableWrite) o;
    return beginOffset == that.beginOffset
        && Objects.equals(object, that.object)
        && Objects.equals(options, that.options)
        && Objects.equals(signedUrl, that.signedUrl)
        && Objects.equals(uploadId, that.uploadId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(object, options, signedUrl, uploadId, beginOffset);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("object", object)
        .add("options", options)
        .add("signedUrl", signedUrl)
        .add("uploadId", uploadId)
        .add("beginOffset", beginOffset)
        .toString();
  }

  private String getObjectJson() {
    if (objectJson == null) {
      synchronized (this) {
        if (objectJson == null) {
          objectJson = gson.toJson(object);
        }
      }
    }
    return objectJson;
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    String ignore = getObjectJson();
    out.defaultWriteObject();
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    JsonReader jsonReader = gson.newJsonReader(new StringReader(this.objectJson));
    this.object = gson.fromJson(jsonReader, StorageObject.class);
  }

  static JsonResumableWrite of(
      StorageObject req, Map<StorageRpc.Option, ?> options, String uploadId, long beginOffset) {
    return new JsonResumableWrite(req, options, null, uploadId, beginOffset);
  }

  static JsonResumableWrite of(String signedUrl, String uploadId, long beginOffset) {
    return new JsonResumableWrite(null, null, signedUrl, uploadId, beginOffset);
  }
}
