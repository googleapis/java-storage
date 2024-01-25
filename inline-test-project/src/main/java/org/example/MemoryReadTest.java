/*
 * Copyright 2024 Google LLC
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

package org.example;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import java.nio.ByteBuffer;

public class MemoryReadTest {

    public static void main(String[] args) throws Exception {
        final int numberOfReads = Integer.parseInt(args[0]);
        final String bucketName = args[1];
        final String objectName = args[2];
        final String transport = args[3];
        Storage storage;
        if (transport.equals("grpc")) {
            System.out.println("Using grpc");
             storage = StorageOptions.grpc()
                    .setAttemptDirectPath(true)
                    .build().getService();
        } else {
            System.out.println("Using json");
            storage = StorageOptions.http()
                    .build().getService();
        }
        final BlobId blobId = BlobId.of(bucketName, objectName);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        System.out.println("Starting...");
        for (int i = 0; i < numberOfReads; i++) {
            ReadChannel r = storage.reader(blobId);
            int totalBytesRead = 0;
            while (r.isOpen()) {
                int bytesRead = r.read(buffer);
                if (bytesRead == -1) {
                    break;
                }
                totalBytesRead += bytesRead;
                buffer.clear();
            }
            System.out.println("Downlaoded(" + i + "): " + totalBytesRead);
        }
        System.out.println("Finished...");
    }
}