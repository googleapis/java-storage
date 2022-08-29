package com.google.cloud.storage.it;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.auth.http.HttpTransportFactory;
import com.google.cloud.TransportOptions;
import com.google.cloud.WriteChannel;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketFixture;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageFixture;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class ITWriteChannelConnectionPoolTest {
  @ClassRule(order = 1)
  public static final StorageFixture storageFixture =
      StorageFixture.of(() -> RemoteStorageHelper.create().getOptions().getService());

  @ClassRule(order = 2)
  public static final BucketFixture bucketFixture =
      BucketFixture.newBuilder().setHandle(storageFixture::getInstance).build();

  private static final byte[] BLOB_BYTE_CONTENT = {0xD, 0xE, 0xA, 0xD};
  private static final String BLOB_STRING_CONTENT = "Hello Google Cloud Storage!";

  private static String bucketName;
  private static Storage storage;

  @BeforeClass
  public static void beforeClass() throws IOException {
    storage = storageFixture.getInstance();
    bucketName = bucketFixture.getBucketInfo().getName();
  }

  private static class CustomHttpTransportFactory implements HttpTransportFactory {
    @Override
    @SuppressWarnings({"unchecked", "deprecation"})
    public HttpTransport create() {
      PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
      manager.setMaxTotal(1);
      return new ApacheHttpTransport(HttpClients.createMinimal(manager));
    }
  }

  @Test(timeout = 5000)
  public void testWriteChannelWithConnectionPool() throws IOException {
    TransportOptions transportOptions =
        HttpTransportOptions.newBuilder()
            .setHttpTransportFactory(new CustomHttpTransportFactory())
            .build();
    Storage storageWithPool =
        StorageOptions.http().setTransportOptions(transportOptions).build().getService();
    String blobName = "test-custom-pool-management";
    BlobInfo blob = BlobInfo.newBuilder(bucketName, blobName).build();
    byte[] stringBytes;
    try (WriteChannel writer = storageWithPool.writer(blob)) {
      stringBytes = BLOB_STRING_CONTENT.getBytes(UTF_8);
      writer.write(ByteBuffer.wrap(BLOB_BYTE_CONTENT));
      writer.write(ByteBuffer.wrap(stringBytes));
    }
    try (WriteChannel writer = storageWithPool.writer(blob)) {
      stringBytes = BLOB_STRING_CONTENT.getBytes(UTF_8);
      writer.write(ByteBuffer.wrap(BLOB_BYTE_CONTENT));
      writer.write(ByteBuffer.wrap(stringBytes));
    }
  }
}
