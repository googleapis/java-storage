/*
 * Copyright 2015 Google LLC
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

package com.google.cloud.storage.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.api.gax.paging.Page;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.ImmutableList;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.Duration;

public class RemoteStorageHelperTest {

  private static final String BUCKET_NAME = "bucket-name";
  private static final String PROJECT_ID = "project-id";
  private static final String JSON_KEY =
      "{\n"
          + "  \"private_key_id\": \"somekeyid\",\n"
          + "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggS"
          + "kAgEAAoIBAQC+K2hSuFpAdrJI\\nnCgcDz2M7t7bjdlsadsasad+fvRSW6TjNQZ3p5LLQY1kSZRqBqylRkzteMOyHg"
          + "aR\\n0Pmxh3ILCND5men43j3h4eDbrhQBuxfEMalkG92sL+PNQSETY2tnvXryOvmBRwa/\\nQP/9dJfIkIDJ9Fw9N4"
          + "Bhhhp6mCcRpdQjV38H7JsyJ7lih/oNjECgYAt\\nknddadwkwewcVxHFhcZJO+XWf6ofLUXpRwiTZakGMn8EE1uVa2"
          + "LgczOjwWHGi99MFjxSer5m9\\n1tCa3/KEGKiS/YL71JvjwX3mb+cewlkcmweBKZHM2JPTk0ZednFSpVZMtycjkbLa"
          + "\\ndYOS8V85AgMBewECggEBAKksaldajfDZDV6nGqbFjMiizAKJolr/M3OQw16K6o3/\\n0S31xIe3sSlgW0+UbYlF"
          + "4U8KifhManD1apVSC3csafaspP4RZUHFhtBywLO9pR5c\\nr6S5aLp+gPWFyIp1pfXbWGvc5VY/v9x7ya1VEa6rXvL"
          + "sKupSeWAW4tMj3eo/64ge\\nsdaceaLYw52KeBYiT6+vpsnYrEkAHO1fF/LavbLLOFJmFTMxmsNaG0tuiJHgjshB\\"
          + "n82DpMCbXG9YcCgI/DbzuIjsdj2JC1cascSP//3PmefWysucBQe7Jryb6NQtASmnv\\nCdDw/0jmZTEjpe4S1lxfHp"
          + "lAhHFtdgYTvyYtaLZiVVkCgYEA8eVpof2rceecw/I6\\n5ng1q3Hl2usdWV/4mZMvR0fOemacLLfocX6IYxT1zA1FF"
          + "JlbXSRsJMf/Qq39mOR2\\nSpW+hr4jCoHeRVYLgsbggtrevGmILAlNoqCMpGZ6vDmJpq6ECV9olliDvpPgWOP+\\nm"
          + "YPDreFBGxWvQrADNbRt2dmGsrsCgYEAyUHqB2wvJHFqdmeBsaacewzV8x9WgmeX\\ngUIi9REwXlGDW0Mz50dxpxcK"
          + "CAYn65+7TCnY5O/jmL0VRxU1J2mSWyWTo1C+17L0\\n3fUqjxL1pkefwecxwecvC+gFFYdJ4CQ/MHHXU81Lwl1iWdF"
          + "Cd2UoGddYaOF+KNeM\\nHC7cmqra+JsCgYEAlUNywzq8nUg7282E+uICfCB0LfwejuymR93CtsFgb7cRd6ak\\nECR"
          + "8FGfCpH8ruWJINllbQfcHVCX47ndLZwqv3oVFKh6pAS/vVI4dpOepP8++7y1u\\ncoOvtreXCX6XqfrWDtKIvv0vjl"
          + "HBhhhp6mCcRpdQjV38H7JsyJ7lih/oNjECgYAt\\nkndj5uNl5SiuVxHFhcZJO+XWf6ofLUregtevZakGMn8EE1uVa"
          + "2AY7eafmoU/nZPT\\n00YB0TBATdCbn/nBSuKDESkhSg9s2GEKQZG5hBmL5uCMfo09z3SfxZIhJdlerreP\\nJ7gSi"
          + "dI12N+EZxYd4xIJh/HFDgp7RRO87f+WJkofMQKBgGTnClK1VMaCRbJZPriw\\nEfeFCoOX75MxKwXs6xgrw4W//AYG"
          + "GUjDt83lD6AZP6tws7gJ2IwY/qP7+lyhjEqN\\nHtfPZRGFkGZsdaksdlaksd323423d+15/UvrlRSFPNj1tWQmNKk"
          + "XyRDW4IG1Oa2p\\nrALStNBx5Y9t0/LQnFI4w3aG\\n-----END PRIVATE KEY-----\\n\",\n"
          + "  \"client_email\": \"someclientid@developer.gserviceaccount.com\",\n"
          + "  \"client_id\": \"someclientid.apps.googleusercontent.com\",\n"
          + "  \"type\": \"service_account\"\n"
          + "}";
  private static final InputStream JSON_KEY_STREAM = new ByteArrayInputStream(JSON_KEY.getBytes());
  private static final StorageException RETRYABLE_EXCEPTION = new StorageException(409, "");
  private static final StorageException FATAL_EXCEPTION = new StorageException(500, "");
  private static final String BLOB_NAME2 = "n2";
  private static final BlobId BLOB_ID1 = BlobId.of(BUCKET_NAME, "n1");
  private static final BlobId BLOB_ID2 = BlobId.of(BUCKET_NAME, BLOB_NAME2);

  private Blob blob1;
  private Blob blob2;
  private List<Blob> blobList;
  private Page<Blob> blobPage;

  @Before
  public void setUp() {
    blob1 = EasyMock.createMock(Blob.class);
    blob2 = EasyMock.createMock(Blob.class);
    blobList = ImmutableList.of(blob1, blob2);
    blobPage =
        new Page<Blob>() {

          @Override
          public boolean hasNextPage() {
            return true;
          }

          @Override
          public String getNextPageToken() {
            return "nextPageCursor";
          }

          @Override
          public Page<Blob> getNextPage() {
            return null;
          }

          @Override
          public Iterable<Blob> getValues() {
            return blobList;
          }

          @Override
          public Iterable<Blob> iterateAll() {
            return blobList;
          }
        };
  }

  @Test
  public void testForceDelete() throws InterruptedException, ExecutionException {
    Storage storageMock = EasyMock.createMock(Storage.class);
    EasyMock.expect(blob1.getBlobId()).andReturn(BLOB_ID1);
    EasyMock.expect(blob2.getBlobId()).andReturn(BLOB_ID2);

    ArrayList<BlobId> ids = new ArrayList<>();
    ids.add(BLOB_ID1);
    ids.add(BLOB_ID2);
    EasyMock.expect(storageMock.delete(ids)).andReturn(Collections.nCopies(2, true));
    EasyMock.expect(storageMock.list(BUCKET_NAME, BlobListOption.versions(true)))
        .andReturn(blobPage);
    EasyMock.expect(storageMock.delete(BUCKET_NAME)).andReturn(true);
    EasyMock.replay(storageMock, blob1, blob2);
    assertTrue(RemoteStorageHelper.forceDelete(storageMock, BUCKET_NAME, 5, TimeUnit.SECONDS));
    EasyMock.verify(storageMock, blob1, blob2);
  }

  @Test
  public void testForceDeleteTimeout() throws InterruptedException, ExecutionException {
    Storage storageMock = EasyMock.createMock(Storage.class);
    EasyMock.expect(blob1.getBlobId()).andReturn(BLOB_ID1).anyTimes();
    EasyMock.expect(blob2.getBlobId()).andReturn(BLOB_ID2).anyTimes();

    ArrayList<BlobId> ids = new ArrayList<>();
    ids.add(BLOB_ID1);
    ids.add(BLOB_ID2);
    EasyMock.expect(storageMock.delete(ids)).andReturn(Collections.nCopies(2, true)).anyTimes();

    EasyMock.expect(storageMock.list(BUCKET_NAME, BlobListOption.versions(true)))
        .andReturn(blobPage)
        .anyTimes();
    EasyMock.expect(storageMock.delete(BUCKET_NAME)).andThrow(RETRYABLE_EXCEPTION).anyTimes();
    EasyMock.replay(storageMock, blob1, blob2);
    assertFalse(
        RemoteStorageHelper.forceDelete(storageMock, BUCKET_NAME, 50, TimeUnit.MICROSECONDS));
    EasyMock.verify(storageMock);
  }

  @Test
  public void testForceDeleteFail() throws InterruptedException, ExecutionException {
    Storage storageMock = EasyMock.createMock(Storage.class);
    EasyMock.expect(blob1.getBlobId()).andReturn(BLOB_ID1);
    EasyMock.expect(blob2.getBlobId()).andReturn(BLOB_ID2);
    ArrayList<BlobId> ids = new ArrayList<>();
    ids.add(BLOB_ID1);
    ids.add(BLOB_ID2);
    EasyMock.expect(storageMock.delete(ids)).andReturn(Collections.nCopies(2, true)).anyTimes();
    EasyMock.expect(storageMock.list(BUCKET_NAME, BlobListOption.versions(true)))
        .andReturn(blobPage);
    EasyMock.expect(storageMock.delete(BUCKET_NAME)).andThrow(FATAL_EXCEPTION);
    EasyMock.replay(storageMock, blob1, blob2);
    try {
      RemoteStorageHelper.forceDelete(storageMock, BUCKET_NAME, 5, TimeUnit.SECONDS);
      Assert.fail();
    } catch (ExecutionException ex) {
      assertNotNull(ex.getMessage());
    } finally {
      EasyMock.verify(storageMock);
    }
  }

  @Test
  public void testForceDeleteNoTimeout() {
    Storage storageMock = EasyMock.createMock(Storage.class);
    EasyMock.expect(blob1.getBlobId()).andReturn(BLOB_ID1);
    EasyMock.expect(blob2.getBlobId()).andReturn(BLOB_ID2);
    ArrayList<BlobId> ids = new ArrayList<>();
    ids.add(BLOB_ID1);
    ids.add(BLOB_ID2);
    EasyMock.expect(storageMock.delete(ids)).andReturn(Collections.nCopies(2, true)).anyTimes();
    EasyMock.expect(storageMock.list(BUCKET_NAME, BlobListOption.versions(true)))
        .andReturn(blobPage);
    EasyMock.expect(storageMock.delete(BUCKET_NAME)).andReturn(true);
    EasyMock.replay(storageMock, blob1, blob2);
    RemoteStorageHelper.forceDelete(storageMock, BUCKET_NAME);
    EasyMock.verify(storageMock);
  }

  @Test
  public void testForceDeleteNoTimeoutFail() {
    Storage storageMock = EasyMock.createMock(Storage.class);
    EasyMock.expect(blob1.getBlobId()).andReturn(BLOB_ID1);
    EasyMock.expect(blob2.getBlobId()).andReturn(BLOB_ID2);
    ArrayList<BlobId> ids = new ArrayList<>();
    ids.add(BLOB_ID1);
    ids.add(BLOB_ID2);
    EasyMock.expect(storageMock.delete(ids)).andReturn(Collections.nCopies(2, true)).anyTimes();
    EasyMock.expect(storageMock.list(BUCKET_NAME, BlobListOption.versions(true)))
        .andReturn(blobPage);
    EasyMock.expect(storageMock.delete(BUCKET_NAME)).andThrow(FATAL_EXCEPTION);
    EasyMock.replay(storageMock, blob1, blob2);
    try {
      RemoteStorageHelper.forceDelete(storageMock, BUCKET_NAME);
      Assert.fail();
    } catch (StorageException ex) {
      assertNotNull(ex.getMessage());
    } finally {
      EasyMock.verify(storageMock);
    }
  }

  @Test
  public void testForceDeleteRetriesWithUserProject() throws Exception {
    final String USER_PROJECT = "user-project";
    Storage storageMock = EasyMock.createMock(Storage.class);
    EasyMock.expect(blob1.getBlobId()).andReturn(BLOB_ID1);
    EasyMock.expect(blob2.getBlobId()).andReturn(BLOB_ID2);
    EasyMock.expect(blob2.getName()).andReturn(BLOB_NAME2);
    ArrayList<BlobId> ids = new ArrayList<>();
    ids.add(BLOB_ID1);
    ids.add(BLOB_ID2);
    EasyMock.expect(storageMock.delete(ids))
        .andReturn(ImmutableList.of(Boolean.TRUE, Boolean.FALSE))
        .anyTimes();
    EasyMock.expect(
            storageMock.delete(
                BUCKET_NAME, BLOB_NAME2, Storage.BlobSourceOption.userProject(USER_PROJECT)))
        .andReturn(true)
        .anyTimes();
    EasyMock.expect(
            storageMock.list(
                BUCKET_NAME,
                BlobListOption.versions(true),
                BlobListOption.userProject(USER_PROJECT)))
        .andReturn(blobPage);
    EasyMock.expect(
            storageMock.delete(BUCKET_NAME, Storage.BucketSourceOption.userProject(USER_PROJECT)))
        .andReturn(true);
    EasyMock.replay(storageMock, blob1, blob2);
    try {
      RemoteStorageHelper.forceDelete(storageMock, BUCKET_NAME, 5, TimeUnit.SECONDS, USER_PROJECT);
    } finally {
      EasyMock.verify(storageMock);
    }
  }

  @Test
  public void testCreateFromStream() {
    RemoteStorageHelper helper = RemoteStorageHelper.create(PROJECT_ID, JSON_KEY_STREAM);
    StorageOptions options = helper.getOptions();
    assertEquals(PROJECT_ID, options.getProjectId());
    assertEquals(60000, ((HttpTransportOptions) options.getTransportOptions()).getConnectTimeout());
    assertEquals(60000, ((HttpTransportOptions) options.getTransportOptions()).getReadTimeout());
    assertEquals(10, options.getRetrySettings().getMaxAttempts());
    assertEquals(Duration.ofMillis(30000), options.getRetrySettings().getMaxRetryDelay());
    assertEquals(Duration.ofMillis(120000), options.getRetrySettings().getTotalTimeout());
    assertEquals(Duration.ofMillis(250), options.getRetrySettings().getInitialRetryDelay());
  }
}
