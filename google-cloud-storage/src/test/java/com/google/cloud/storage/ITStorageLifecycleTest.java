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

import com.google.cloud.NoCredentials;
import com.google.cloud.storage.conformance.retry.TestBench;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * The interaction of {@link com.google.cloud.ServiceOptions} instance caching has differing
 * behavior depending on whether {@link StorageOptions#grpc()} or {@link StorageOptions#http()} are
 * used.
 *
 * <p>Define some tests to ensue we are correctly integrating with the caching lifecycle
 */
// Not in com.google.cloud.storage.it because we're testing package local things
public final class ITStorageLifecycleTest {
  @ClassRule(order = 1)
  public static final TestBench TEST_BENCH =
      TestBench.newBuilder().setContainerName("lifecycle-test").build();

  @Test
  public void grpc() throws Exception {
    GrpcStorageOptions options =
        StorageOptions.grpc()
            .setHost(TEST_BENCH.getGRPCBaseUri())
            .setCredentials(NoCredentials.getInstance())
            .setProjectId("test-project-id")
            .build();

    Storage service1 = options.getService();
    Storage service2 = options.getService();

    // ensure both instances are the same
    assertThat(service2).isSameInstanceAs(service1);

    // make sure and RPCs can be done
    List<Bucket> buckets1 =
        StreamSupport.stream(service1.list().iterateAll().spliterator(), false)
            .collect(Collectors.toList());
    assertThat(buckets1).isEmpty();
    List<Bucket> buckets2 =
        StreamSupport.stream(service2.list().iterateAll().spliterator(), false)
            .collect(Collectors.toList());
    assertThat(buckets2).isEmpty();

    // close the instance
    service1.close();

    // expect a new instance to be returned
    Storage service3 = options.getService();

    assertThat(service3).isNotSameInstanceAs(service1);
    // make sure and RPCs can be done
    List<Bucket> buckets3 =
        StreamSupport.stream(service3.list().iterateAll().spliterator(), false)
            .collect(Collectors.toList());
    assertThat(buckets3).isEmpty();
  }

  @Test
  public void http() throws Exception {
    HttpStorageOptions options =
        StorageOptions.http()
            .setHost(TEST_BENCH.getBaseUri())
            .setCredentials(NoCredentials.getInstance())
            .setProjectId("test-project-id")
            .build();

    Storage service1 = options.getService();
    Storage service2 = options.getService();

    // ensure both instances are the same
    assertThat(service2).isSameInstanceAs(service1);
    // make sure and RPCs can be done
    List<Bucket> buckets1 =
        StreamSupport.stream(service1.list().iterateAll().spliterator(), false)
            .collect(Collectors.toList());
    assertThat(buckets1).isEmpty();
    List<Bucket> buckets2 =
        StreamSupport.stream(service2.list().iterateAll().spliterator(), false)
            .collect(Collectors.toList());
    assertThat(buckets2).isEmpty();

    service1.close(); // this should be a no-op for http

    // expect the original instance to still be returned
    Storage service3 = options.getService();

    assertThat(service3).isSameInstanceAs(service1);
    // make sure and RPCs can be done
    List<Bucket> buckets3 =
        StreamSupport.stream(service3.list().iterateAll().spliterator(), false)
            .collect(Collectors.toList());
    assertThat(buckets3).isEmpty();
  }
}
