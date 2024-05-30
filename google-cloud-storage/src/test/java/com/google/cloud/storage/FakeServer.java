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

import com.google.cloud.NoCredentials;
import com.google.cloud.storage.it.GrpcPlainRequestLoggingInterceptor;
import com.google.storage.v2.StorageGrpc;
import com.google.storage.v2.StorageSettings;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

final class FakeServer implements AutoCloseable {

  private final Server server;
  private final GrpcStorageOptions grpcStorageOptions;

  FakeServer(Server server, GrpcStorageOptions grpcStorageOptions) {
    this.server = server;
    this.grpcStorageOptions = grpcStorageOptions;
  }

  GrpcStorageOptions getGrpcStorageOptions() {
    return grpcStorageOptions;
  }

  StorageSettings storageSettings() throws IOException {
    return grpcStorageOptions.getStorageSettings();
  }

  @Override
  public void close() throws InterruptedException {
    server.shutdownNow().awaitTermination(10, TimeUnit.SECONDS);
  }

  static FakeServer of(StorageGrpc.StorageImplBase service) throws IOException {
    InetSocketAddress address = new InetSocketAddress("localhost", 0);
    Server server = NettyServerBuilder.forAddress(address).addService(service).build();
    server.start();
    String endpoint = String.format("%s:%d", address.getHostString(), server.getPort());
    GrpcStorageOptions grpcStorageOptions =
        StorageOptions.grpc()
            .setHost("http://" + endpoint)
            .setProjectId("test-proj")
            .setCredentials(NoCredentials.getInstance())
            .setGrpcInterceptorProvider(GrpcPlainRequestLoggingInterceptor.getInterceptorProvider())
            .build();
    return new FakeServer(server, grpcStorageOptions);
  }
}
