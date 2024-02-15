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
        final int appBuffer = Integer.parseInt(args[4]);
        Storage storage;
        if (transport.equals("grpc")) {
            System.out.println("Using gRPC over DP");
            storage = StorageOptions.grpc()
                    .setAttemptDirectPath(true)
                    .build().getService();
        } else
        if (transport.equals("no-dp-grpc")) {
            System.out.println("Using gRPC over Cloud Path");
            storage = StorageOptions.grpc().build().getService();
        } else {
            System.out.println("Using JSON");
            storage = StorageOptions.http()
                    .build().getService();
        }

        final BlobId blobId = BlobId.of(bucketName, objectName);
        System.out.println("Starting...");
        System.out.println("Zero-copy");
        HashFunction hashFunction = Hashing.crc32c();
        ByteBuffer buffer = ByteBuffer.allocate(appBuffer);
        for (int i = 0; i < numberOfReads; i++) {
            try {
                Hasher hasher = hashFunction.newHasher();
                ReadChannel r = storage.reader(blobId);
                int totalBytesRead = 0;
                while (r.isOpen()) {
                    int bytesRead = r.read(buffer);
                    if (bytesRead == -1 || bytesRead == 0) {
                        break;
                    }
                    totalBytesRead += bytesRead;
                    buffer.flip();
                    hasher.putBytes(buffer);
                    buffer.clear();
                }
                System.out.println("Hasher: " + hasher.hash());
                System.out.println("Downlaoded(" + i + "): " + totalBytesRead + ": 100MiB?" + (totalBytesRead == 1024 * 1024 * 100));
            } catch (IllegalArgumentException e) {
                System.out.println("IllegalArgumentException occurred: unstable continue on");
            }
        }
        System.out.println("Finished...");
    }
}