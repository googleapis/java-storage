# Google Cloud Storage Client for Java

Java idiomatic client for [Cloud Storage][product-docs].

[![Maven][maven-version-image]][maven-version-link]
![Stability][stability-image]

- [Product Documentation][product-docs]
- [Client Library Documentation][javadocs]

## Quickstart

If you are using Maven with [BOM][libraries-bom], add this to your pom.xml file
```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.google.cloud</groupId>
      <artifactId>libraries-bom</artifactId>
      <version>5.3.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>google-cloud-storage</artifactId>
  </dependency>

```

If you are using Maven without BOM, add this to your dependencies:

```xml
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>google-cloud-storage</artifactId>
  <version>1.108.0</version>
</dependency>

```

[//]: # ({x-version-update-start:google-cloud-storage:released})

If you are using Gradle, add this to your dependencies
```Groovy
compile 'com.google.cloud:google-cloud-storage:1.108.0'
```
If you are using SBT, add this to your dependencies
```Scala
libraryDependencies += "com.google.cloud" % "google-cloud-storage" % "1.108.0"
```
[//]: # ({x-version-update-end})

## Authentication

See the [Authentication][authentication] section in the base directory's README.

## Getting Started

### Prerequisites

You will need a [Google Cloud Platform Console][developer-console] project with the Cloud Storage [API enabled][enable-api].
You will need to [enable billing][enable-billing] to use Google Cloud Storage.
[Follow these instructions][create-project] to get your project set up. You will also need to set up the local development environment by
[installing the Google Cloud SDK][cloud-sdk] and running the following commands in command line:
`gcloud auth login` and `gcloud config set project [YOUR PROJECT ID]`.

### Installation and setup

You'll need to obtain the `google-cloud-storage` library.  See the [Quickstart](#quickstart) section
to add `google-cloud-storage` as a dependency in your code.

## About Cloud Storage


[Cloud Storage][product-docs] is a durable and highly available object storage service. Google Cloud Storage is almost infinitely scalable and guarantees consistency: when a write succeeds, the latest copy of the object will be returned to any GET, globally.

See the [Cloud Storage client library docs][javadocs] to learn how to
use this Cloud Storage Client Library.


#### Creating an authorized service object

To make authenticated requests to Google Cloud Storage, you must create a service object with credentials. You can
then make API calls by calling methods on the Storage service object. The simplest way to authenticate is to use
[Application Default Credentials](https://developers.google.com/identity/protocols/application-default-credentials).
These credentials are automatically inferred from your environment, so you only need the following code to create your
service object:

```java
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

Storage storage = StorageOptions.getDefaultInstance().getService();
```

For other authentication options, see the [Authentication](https://github.com/googleapis/google-cloud-java#authentication) page.

#### Storing data
Stored objects are called "blobs" in `google-cloud` and are organized into containers called "buckets".  `Blob`, a
subclass of `BlobInfo`, adds a layer of service-related functionality over `BlobInfo`.  Similarly, `Bucket` adds a
layer of service-related functionality over `BucketInfo`.  In this code snippet, we will create a new bucket and
upload a blob to that bucket.

Add the following imports at the top of your file:

```java
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
```

Then add the following code to create a bucket and upload a simple blob.

*Important: Bucket names have to be globally unique (among all users of Cloud Storage). If you choose a bucket name
that already exists, you'll get a helpful error message telling you to choose another name. In the code below, replace
"my_unique_bucket" with a unique bucket name. See more about naming rules
[here](https://cloud.google.com/storage/docs/bucket-naming?hl=en#requirements).*

```java
// Create a bucket
String bucketName = "my_unique_bucket"; // Change this to something unique
Bucket bucket = storage.create(BucketInfo.of(bucketName));

// Upload a blob to the newly created bucket
BlobId blobId = BlobId.of(bucketName, "my_blob_name");
BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
Blob blob = storage.create(blobInfo, "a simple blob".getBytes(UTF_8));
```

A complete example for creating a blob can be found at
[CreateBlob.java](https://github.com/googleapis/google-cloud-java/tree/master/google-cloud-examples/src/main/java/com/google/cloud/examples/storage/snippets/CreateBlob.java).

At this point, you will be able to see your newly created bucket and blob on the Google Developers Console.

#### Retrieving data
Now that we have content uploaded to the server, we can see how to read data from the server.  Add the following line
to your program to get back the blob we uploaded.

```java
BlobId blobId = BlobId.of(bucketName, "my_blob_name");
byte[] content = storage.readAllBytes(blobId);
String contentString = new String(content, UTF_8);
```

A complete example for accessing blobs can be found at
[CreateBlob.java](https://github.com/googleapis/google-cloud-java/tree/master/google-cloud-examples/src/main/java/com/google/cloud/examples/storage/snippets/CreateBlob.java).

#### Updating data
Another thing we may want to do is update a blob. The following snippet shows how to update a Storage blob if it exists.

``` java
BlobId blobId = BlobId.of(bucketName, "my_blob_name");
Blob blob = storage.get(blobId);
if (blob != null) {
  byte[] prevContent = blob.getContent();
  System.out.println(new String(prevContent, UTF_8));
  WritableByteChannel channel = blob.writer();
  channel.write(ByteBuffer.wrap("Updated content".getBytes(UTF_8)));
  channel.close();
}
```

The complete source code can be found at
[UpdateBlob.java](https://github.com/googleapis/google-cloud-java/tree/master/google-cloud-examples/src/main/java/com/google/cloud/examples/storage/snippets/UpdateBlob.java).

#### Listing buckets and contents of buckets
Suppose that you've added more buckets and blobs, and now you want to see the names of your buckets and the contents
of each one. Add the following code to list all your buckets and all the blobs inside each bucket.

```java
// List all your buckets
System.out.println("My buckets:");
for (Bucket bucket : storage.list().iterateAll()) {
  System.out.println(bucket);

  // List all blobs in the bucket
  System.out.println("Blobs in the bucket:");
  for (Blob blob : bucket.list().iterateAll()) {
    System.out.println(blob);
  }
}
```

#### Complete source code

In
[CreateAndListBucketsAndBlobs.java](https://github.com/googleapis/google-cloud-java/tree/master/google-cloud-examples/src/main/java/com/google/cloud/examples/storage/snippets/CreateAndListBucketsAndBlobs.java)
we put together examples creating and listing buckets and blobs into one program. The program assumes that you are
running on Compute Engine or from your own desktop. To run the example on App Engine, simply move
the code from the main method to your application's servlet class and change the print statements to
display on your webpage.

### Example Applications

- [`StorageExample`](https://github.com/googleapis/google-cloud-java/tree/master/google-cloud-examples/src/main/java/com/google/cloud/examples/storage/StorageExample.java) is a simple command line interface that provides some of Cloud Storage's functionality.  Read more about using the application on the [`StorageExample` docs page](https://github.com/googleapis/google-cloud-java/blob/master/google-cloud-examples/README.md).
- [`Bookshelf`](https://github.com/GoogleCloudPlatform/getting-started-java/tree/master/bookshelf) - An App Engine application that manages a virtual bookshelf.
  - This app uses `google-cloud` to interface with Cloud Datastore and Cloud Storage. It also uses Cloud SQL, another Google Cloud Platform service.
- [`Flexible Environment/Storage example`](https://github.com/GoogleCloudPlatform/java-docs-samples/tree/master/flexible/cloudstorage) - An app that uploads files to a public Cloud Storage bucket on the App Engine Flexible Environment runtime.





## Troubleshooting

To get help, follow the instructions in the [shared Troubleshooting document][troubleshooting].

## Java Versions

Java 7 or above is required for using this client.

## Versioning

This library follows [Semantic Versioning](http://semver.org/), but does update [Storage interface](src/main/java/com.google.cloud.storage/Storage.java)
to introduce new methods which can break your implementations if you implement this interface for testing purposes.


## Contributing


Contributions to this library are always welcome and highly encouraged.

See [CONTRIBUTING][contributing] for more information how to get started.

Please note that this project is released with a Contributor Code of Conduct. By participating in
this project you agree to abide by its terms. See [Code of Conduct][code-of-conduct] for more
information.

## License

Apache 2.0 - See [LICENSE][license] for more information.

## CI Status

Java Version | Status
------------ | ------
Java 7 | [![Kokoro CI][kokoro-badge-image-1]][kokoro-badge-link-1]
Java 8 | [![Kokoro CI][kokoro-badge-image-2]][kokoro-badge-link-2]
Java 8 OSX | [![Kokoro CI][kokoro-badge-image-3]][kokoro-badge-link-3]
Java 8 Windows | [![Kokoro CI][kokoro-badge-image-4]][kokoro-badge-link-4]
Java 11 | [![Kokoro CI][kokoro-badge-image-5]][kokoro-badge-link-5]

[product-docs]: https://cloud.google.com/storage
[javadocs]: https://googleapis.dev/java/java-storage/latest/
[kokoro-badge-image-1]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-storage/java7.svg
[kokoro-badge-link-1]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-storage/java7.html
[kokoro-badge-image-2]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-storage/java8.svg
[kokoro-badge-link-2]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-storage/java8.html
[kokoro-badge-image-3]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-storage/java8-osx.svg
[kokoro-badge-link-3]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-storage/java8-osx.html
[kokoro-badge-image-4]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-storage/java8-win.svg
[kokoro-badge-link-4]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-storage/java8-win.html
[kokoro-badge-image-5]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-storage/java11.svg
[kokoro-badge-link-5]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-storage/java11.html
[stability-image]: https://img.shields.io/badge/stability-ga-green
[maven-version-image]: https://img.shields.io/maven-central/v/com.google.cloud/google-cloud-storage.svg
[maven-version-link]: https://search.maven.org/search?q=g:com.google.cloud%20AND%20a:google-cloud-storage&core=gav
[authentication]: https://github.com/googleapis/google-cloud-java#authentication
[developer-console]: https://console.developers.google.com/
[create-project]: https://cloud.google.com/resource-manager/docs/creating-managing-projects
[cloud-sdk]: https://cloud.google.com/sdk/
[troubleshooting]: https://github.com/googleapis/google-cloud-common/blob/master/troubleshooting/readme.md#troubleshooting
[contributing]: https://github.com/googleapis/java-storage/blob/master/CONTRIBUTING.md
[code-of-conduct]: https://github.com/googleapis/java-storage/blob/master/CODE_OF_CONDUCT.md#contributor-code-of-conduct
[license]: https://github.com/googleapis/java-storage/blob/master/LICENSE
[enable-billing]: https://cloud.google.com/apis/docs/getting-started#enabling_billing
[enable-api]: https://console.cloud.google.com/flows/enableapi?apiid=storage.googleapis.com
[libraries-bom]: https://github.com/GoogleCloudPlatform/cloud-opensource-java/wiki/The-Google-Cloud-Platform-Libraries-BOM
[shell_img]: https://gstatic.com/cloudssh/images/open-btn.png
