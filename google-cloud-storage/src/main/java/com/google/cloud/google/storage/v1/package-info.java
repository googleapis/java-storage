/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * The interfaces provided are listed below, along with usage samples.
 *
 * <p>======================= StorageClient =======================
 *
 * <p>Service Description: Manages Google Cloud Storage resources.
 *
 * <p>Sample for StorageClient:
 *
 * <pre>{@code
 * try (StorageClient storageClient = StorageClient.create()) {
 *   DeleteBucketAccessControlRequest request =
 *       DeleteBucketAccessControlRequest.newBuilder()
 *           .setBucket("bucket-1378203158")
 *           .setEntity("entity-1298275357")
 *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
 *           .build();
 *   storageClient.deleteBucketAccessControl(request);
 * }
 * }</pre>
 */
@Generated("by gapic-generator-java")
package com.google.cloud.google.storage.v1;

import javax.annotation.Generated;
