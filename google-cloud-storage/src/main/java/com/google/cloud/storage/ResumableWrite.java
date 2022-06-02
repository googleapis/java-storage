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

import com.google.storage.v2.StartResumableWriteRequest;
import com.google.storage.v2.StartResumableWriteResponse;
import java.util.Objects;

final class ResumableWrite {

  private final StartResumableWriteRequest req;
  private final StartResumableWriteResponse res;

  public ResumableWrite(StartResumableWriteRequest req, StartResumableWriteResponse res) {
    this.req = req;
    this.res = res;
  }

  public StartResumableWriteRequest getReq() {
    return req;
  }

  public StartResumableWriteResponse getRes() {
    return res;
  }

  @Override
  public String toString() {
    return "Start{" + "req=" + req + ", res=" + res + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ResumableWrite)) {
      return false;
    }
    ResumableWrite resumableWrite = (ResumableWrite) o;
    return Objects.equals(req, resumableWrite.req) && Objects.equals(res, resumableWrite.res);
  }

  @Override
  public int hashCode() {
    return Objects.hash(req, res);
  }
}
