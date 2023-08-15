## Testing code that uses Google Cloud Storage

`RemoteStorageHelper` contains convenience methods to make setting up and cleaning up the test buckets easier. To use this class:

1. Create a test Google Cloud project.

2. Download a JSON service account credentials file from the Google Developer's Console.  See more about this on the [Google Cloud Platform Storage Authentication page][cloud-platform-storage-authentication].

3. Create a `RemoteStorageHelper` object using your project ID and JSON key.
   Here is an example that uses the `RemoteStorageHelper` to create a bucket.
   ```java
   RemoteStorageHelper helper =
     RemoteStorageHelper.create(PROJECT_ID, new FileInputStream("/path/to/my/JSON/key.json"));
   Storage storage = helper.getOptions().getService();
   String bucket = RemoteStorageHelper.generateBucketName();
   storage.create(BucketInfo.of(bucket));
   ```

4. Run your tests.

5. Clean up the test project by using `forceDelete` to clear any buckets used.
   Here is an example that clears the bucket created in Step 3 with a timeout of 5 seconds.
   ```java
   RemoteStorageHelper.forceDelete(storage, bucket, 5, TimeUnit.SECONDS);
   ```
