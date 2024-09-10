/*
 * Copyright 2024 Google LLC
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

package com.google.cloud.storage.it;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ITUniverseDomainTest {

    private static Storage storage;
    private static String bucketName;

    private static final String TEST_UNIVERSE_DOMAIN = System.getenv("TEST_UNIVERSE_DOMAIN");
    private static final String TEST_PROJECT_ID = System.getenv("TEST_UNIVERSE_PROJECT_ID");
    private static final String TEST_UNIVERSE_LOCATION = System.getenv("TEST_UNIVERSE_LOCATION");
    private static final String CREDENTIAL_PATH = System.getenv("TEST_UNIVERSE_DOMAIN_CREDENTIAL");

    @BeforeClass
    public static void setUp() throws IOException {
        GoogleCredentials creds = ServiceAccountCredentials.fromStream(Files.newInputStream(Paths.get(CREDENTIAL_PATH)))
                .toBuilder()
                .build()
                .createWithUseJwtAccessWithScope(true)
                .createScoped("https://www.googleapis.com/auth/cloud-platform");

        storage = StorageOptions.newBuilder()
                .setUniverseDomain(TEST_UNIVERSE_DOMAIN)
                .setProjectId(TEST_PROJECT_ID)
                .setCredentials(creds)
                .build()
                .getService();

        bucketName = "universedomaintest" + UUID.randomUUID();
    }

    @Test
    public void universeDomainTests() {
        storage.create(BucketInfo.newBuilder(bucketName).setLocation(TEST_UNIVERSE_LOCATION).build());
        String content = "hello";
        storage.create(BlobInfo.newBuilder(bucketName, content).build(), content.getBytes(StandardCharsets.UTF_8));

        Blob blob = storage.get(bucketName, content);
        assertEquals(content, new String(blob.getContent(), StandardCharsets.UTF_8));

        storage.delete(bucketName, content);
        assertNull(storage.get(bucketName, content));
    }

    @AfterClass
    public static void tearDown() {
        if (storage != null) {
            for (Blob blob : storage.list(bucketName).iterateAll()) {
                storage.delete(blob.getBlobId());
            }
            storage.delete(bucketName);
        }
    }
}
