# Google Cloud Storage Client for Java

Java idiomatic client for [Cloud Storage][product-docs].

[![Maven][maven-version-image]][maven-version-link]
![Stability][stability-image]

- [Product Documentation][product-docs]
- [Client Library Documentation][javadocs]


## Quickstart

If you are using Maven with [BOM][libraries-bom], add this to your pom.xml file:

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.google.cloud</groupId>
      <artifactId>libraries-bom</artifactId>
      <version>26.39.0</version>
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
  <dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>google-cloud-storage-control</artifactId>
  </dependency>

```

If you are using Maven without the BOM, add this to your dependencies:

<!-- {x-version-update-start:google-cloud-storage:released} -->

```xml
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>google-cloud-storage</artifactId>
  <version>2.38.0</version>
</dependency>
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>google-cloud-storage-control</artifactId>
  <version>2.39.1-SNAPSHOT</version><!-- {x-version-update:google-cloud-storage:current} -->
</dependency>

```

If you are using Gradle 5.x or later, add this to your dependencies:

```Groovy
implementation platform('com.google.cloud:libraries-bom:26.39.0')

implementation 'com.google.cloud:google-cloud-storage'
```
If you are using Gradle without BOM, add this to your dependencies:

```Groovy
implementation 'com.google.cloud:google-cloud-storage:2.38.0'
```

If you are using SBT, add this to your dependencies:

```Scala
libraryDependencies += "com.google.cloud" % "google-cloud-storage" % "2.38.0"
```
<!-- {x-version-update-end} -->

## Authentication

See the [Authentication][authentication] section in the base directory's README.

## Authorization

The client application making API calls must be granted [authorization scopes][auth-scopes] required for the desired Cloud Storage APIs, and the authenticated principal must have the [IAM role(s)][predefined-iam-roles] required to access GCP resources using the Cloud Storage API calls.

## Getting Started

### Prerequisites

You will need a [Google Cloud Platform Console][developer-console] project with the Cloud Storage [API enabled][enable-api].
You will need to [enable billing][enable-billing] to use Google Cloud Storage.
[Follow these instructions][create-project] to get your project set up. You will also need to set up the local development environment by
[installing the Google Cloud Command Line Interface][cloud-cli] and running the following commands in command line:
`gcloud auth login` and `gcloud config set project [YOUR PROJECT ID]`.

### Installation and setup

You'll need to obtain the `google-cloud-storage` library.  See the [Quickstart](#quickstart) section
to add `google-cloud-storage` as a dependency in your code.

## About Cloud Storage


[Cloud Storage][product-docs] is a durable and highly available object storage service. Google Cloud Storage is almost infinitely scalable and guarantees consistency: when a write succeeds, the latest copy of the object will be returned to any GET, globally.

See the [Cloud Storage client library docs][javadocs] to learn how to
use this Cloud Storage Client Library.


## About Storage Control

The [Storage Control API](https://cloud.google.com/storage/docs/reference/rpc/) lets you perform metadata-specific, control plane, and long-running operations.

The Storage Control API creates one space to perform metadata-specific, control plane, and long-running operations apart from the Storage API. Separating these operations from the Storage API improves API standardization and lets you run faster releases.

If you are using Maven with [BOM][libraries-bom], add this to your pom.xml file:
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.google.cloud</groupId>
            <artifactId>libraries-bom</artifactId>
            <version>26.37.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>google-cloud-storage-control</artifactId>
</dependency>
```

If you are using Maven without the BOM, add this to your dependencies:
<!-- {x-version-update-start:google-cloud-storage:released} -->
```xml
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>google-cloud-storage-control</artifactId>
    <version>2.39.1-beta-SNAPSHOT</version><!-- {x-version-update:google-cloud-storage-control:current} -->
</dependency>
```

If you are using Gradle 5.x or later, add this to your dependencies:
```Groovy
implementation platform('com.google.cloud:libraries-bom:2.39.0')
implementation 'com.google.cloud:google-cloud-storage-control'
```
If you are using Gradle without BOM, add this to your dependencies:

```Groovy
implementation 'com.google.cloud:google-cloud-storage-control:2.39.1-beta-SNAPSHOT' <!-- {x-version-update:google-cloud-storage-control:current} -->
```

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

For other authentication options, see the [Authentication](https://github.com/googleapis/google-cloud-java#authentication) page in Google Cloud Java.

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
[UploadObject.java](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/UploadObject.java).

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
[DownloadObject.java](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/DownloadObject.java).

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

See [ListObjects.java](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/ListObjects.java) for a complete example.

### Example Applications

- [`Bookshelf`](https://github.com/GoogleCloudPlatform/getting-started-java/tree/main/bookshelf) - An App Engine application that manages a virtual bookshelf.
  - This app uses `google-cloud` to interface with Cloud Datastore and Cloud Storage. It also uses Cloud SQL, another Google Cloud Platform service.
- [`Flexible Environment/Storage example`](https://github.com/GoogleCloudPlatform/java-docs-samples/tree/main/flexible/cloudstorage) - An app that uploads files to a public Cloud Storage bucket on the App Engine Flexible Environment runtime.




## Samples

Samples are in the [`samples/`](https://github.com/googleapis/java-storage/tree/main/samples) directory.

| Sample                      | Source Code                       | Try it |
| --------------------------- | --------------------------------- | ------ |
| Native Image Storage Sample | [source code](https://github.com/googleapis/java-storage/blob/main/samples/native-image-sample/src/main/java/com/example/storage/NativeImageStorageSample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/native-image-sample/src/main/java/com/example/storage/NativeImageStorageSample.java) |
| Configure Retries | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/ConfigureRetries.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/ConfigureRetries.java) |
| Generate Signed Post Policy V4 | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/GenerateSignedPostPolicyV4.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/GenerateSignedPostPolicyV4.java) |
| Get Service Account | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/GetServiceAccount.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/GetServiceAccount.java) |
| Quickstart Grpc Dp Sample | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/QuickstartGrpcDpSample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/QuickstartGrpcDpSample.java) |
| Quickstart Grpc Sample | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/QuickstartGrpcSample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/QuickstartGrpcSample.java) |
| Quickstart Sample | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/QuickstartSample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/QuickstartSample.java) |
| Quickstart Storage Control Sample | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/QuickstartStorageControlSample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/QuickstartStorageControlSample.java) |
| Add Bucket Default Owner | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/AddBucketDefaultOwner.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/AddBucketDefaultOwner.java) |
| Add Bucket Iam Conditional Binding | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/AddBucketIamConditionalBinding.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/AddBucketIamConditionalBinding.java) |
| Add Bucket Iam Member | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/AddBucketIamMember.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/AddBucketIamMember.java) |
| Add Bucket Label | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/AddBucketLabel.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/AddBucketLabel.java) |
| Add Bucket Owner | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/AddBucketOwner.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/AddBucketOwner.java) |
| Change Default Storage Class | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/ChangeDefaultStorageClass.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/ChangeDefaultStorageClass.java) |
| Configure Bucket Cors | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/ConfigureBucketCors.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/ConfigureBucketCors.java) |
| Create Bucket | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/CreateBucket.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/CreateBucket.java) |
| Create Bucket Dual Region | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/CreateBucketDualRegion.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/CreateBucketDualRegion.java) |
| Create Bucket Pub Sub Notification | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/CreateBucketPubSubNotification.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/CreateBucketPubSubNotification.java) |
| Create Bucket With Object Retention | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/CreateBucketWithObjectRetention.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/CreateBucketWithObjectRetention.java) |
| Create Bucket With Storage Class And Location | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/CreateBucketWithStorageClassAndLocation.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/CreateBucketWithStorageClassAndLocation.java) |
| Create Bucket With Turbo Replication | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/CreateBucketWithTurboReplication.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/CreateBucketWithTurboReplication.java) |
| Delete Bucket | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/DeleteBucket.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/DeleteBucket.java) |
| Delete Bucket Pub Sub Notification | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/DeleteBucketPubSubNotification.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/DeleteBucketPubSubNotification.java) |
| Disable Bucket Versioning | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/DisableBucketVersioning.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/DisableBucketVersioning.java) |
| Disable Default Event Based Hold | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/DisableDefaultEventBasedHold.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/DisableDefaultEventBasedHold.java) |
| Disable Lifecycle Management | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/DisableLifecycleManagement.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/DisableLifecycleManagement.java) |
| Disable Requester Pays | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/DisableRequesterPays.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/DisableRequesterPays.java) |
| Disable Uniform Bucket Level Access | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/DisableUniformBucketLevelAccess.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/DisableUniformBucketLevelAccess.java) |
| Enable Bucket Versioning | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/EnableBucketVersioning.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/EnableBucketVersioning.java) |
| Enable Default Event Based Hold | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/EnableDefaultEventBasedHold.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/EnableDefaultEventBasedHold.java) |
| Enable Lifecycle Management | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/EnableLifecycleManagement.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/EnableLifecycleManagement.java) |
| Enable Requester Pays | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/EnableRequesterPays.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/EnableRequesterPays.java) |
| Enable Uniform Bucket Level Access | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/EnableUniformBucketLevelAccess.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/EnableUniformBucketLevelAccess.java) |
| Get Bucket Autoclass | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/GetBucketAutoclass.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/GetBucketAutoclass.java) |
| Get Bucket Metadata | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/GetBucketMetadata.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/GetBucketMetadata.java) |
| Get Bucket Rpo | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/GetBucketRpo.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/GetBucketRpo.java) |
| Get Default Event Based Hold | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/GetDefaultEventBasedHold.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/GetDefaultEventBasedHold.java) |
| Get Public Access Prevention | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/GetPublicAccessPrevention.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/GetPublicAccessPrevention.java) |
| Get Requester Pays Status | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/GetRequesterPaysStatus.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/GetRequesterPaysStatus.java) |
| Get Retention Policy | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/GetRetentionPolicy.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/GetRetentionPolicy.java) |
| Get Uniform Bucket Level Access | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/GetUniformBucketLevelAccess.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/GetUniformBucketLevelAccess.java) |
| List Bucket Iam Members | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/ListBucketIamMembers.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/ListBucketIamMembers.java) |
| List Buckets | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/ListBuckets.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/ListBuckets.java) |
| List Pub Sub Notifications | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/ListPubSubNotifications.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/ListPubSubNotifications.java) |
| Lock Retention Policy | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/LockRetentionPolicy.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/LockRetentionPolicy.java) |
| Make Bucket Public | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/MakeBucketPublic.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/MakeBucketPublic.java) |
| Print Bucket Acl | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/PrintBucketAcl.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/PrintBucketAcl.java) |
| Print Bucket Acl Filter By User | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/PrintBucketAclFilterByUser.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/PrintBucketAclFilterByUser.java) |
| Print Pub Sub Notification | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/PrintPubSubNotification.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/PrintPubSubNotification.java) |
| Remove Bucket Cors | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/RemoveBucketCors.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/RemoveBucketCors.java) |
| Remove Bucket Default Kms Key | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/RemoveBucketDefaultKmsKey.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/RemoveBucketDefaultKmsKey.java) |
| Remove Bucket Default Owner | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/RemoveBucketDefaultOwner.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/RemoveBucketDefaultOwner.java) |
| Remove Bucket Iam Conditional Binding | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/RemoveBucketIamConditionalBinding.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/RemoveBucketIamConditionalBinding.java) |
| Remove Bucket Iam Member | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/RemoveBucketIamMember.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/RemoveBucketIamMember.java) |
| Remove Bucket Label | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/RemoveBucketLabel.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/RemoveBucketLabel.java) |
| Remove Bucket Owner | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/RemoveBucketOwner.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/RemoveBucketOwner.java) |
| Remove Retention Policy | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/RemoveRetentionPolicy.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/RemoveRetentionPolicy.java) |
| Set Async Turbo Rpo | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/SetAsyncTurboRpo.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/SetAsyncTurboRpo.java) |
| Set Bucket Autoclass | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/SetBucketAutoclass.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/SetBucketAutoclass.java) |
| Set Bucket Default Kms Key | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/SetBucketDefaultKmsKey.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/SetBucketDefaultKmsKey.java) |
| Set Bucket Website Info | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/SetBucketWebsiteInfo.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/SetBucketWebsiteInfo.java) |
| Set Client Endpoint | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/SetClientEndpoint.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/SetClientEndpoint.java) |
| Set Default Rpo | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/SetDefaultRpo.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/SetDefaultRpo.java) |
| Set Public Access Prevention Enforced | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/SetPublicAccessPreventionEnforced.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/SetPublicAccessPreventionEnforced.java) |
| Set Public Access Prevention Inherited | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/SetPublicAccessPreventionInherited.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/SetPublicAccessPreventionInherited.java) |
| Set Retention Policy | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/SetRetentionPolicy.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/SetRetentionPolicy.java) |
| Activate Hmac Key | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/hmac/ActivateHmacKey.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/hmac/ActivateHmacKey.java) |
| Create Hmac Key | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/hmac/CreateHmacKey.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/hmac/CreateHmacKey.java) |
| Deactivate Hmac Key | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/hmac/DeactivateHmacKey.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/hmac/DeactivateHmacKey.java) |
| Delete Hmac Key | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/hmac/DeleteHmacKey.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/hmac/DeleteHmacKey.java) |
| Get Hmac Key | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/hmac/GetHmacKey.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/hmac/GetHmacKey.java) |
| List Hmac Keys | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/hmac/ListHmacKeys.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/hmac/ListHmacKeys.java) |
| Add File Owner | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/AddFileOwner.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/AddFileOwner.java) |
| Batch Set Object Metadata | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/BatchSetObjectMetadata.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/BatchSetObjectMetadata.java) |
| Change Object Csek To Kms | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/ChangeObjectCsekToKms.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/ChangeObjectCsekToKms.java) |
| Change Object Storage Class | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/ChangeObjectStorageClass.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/ChangeObjectStorageClass.java) |
| Compose Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/ComposeObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/ComposeObject.java) |
| Copy Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/CopyObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/CopyObject.java) |
| Copy Old Version Of Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/CopyOldVersionOfObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/CopyOldVersionOfObject.java) |
| Delete Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/DeleteObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/DeleteObject.java) |
| Delete Old Version Of Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/DeleteOldVersionOfObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/DeleteOldVersionOfObject.java) |
| Download Byte Range | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/DownloadByteRange.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/DownloadByteRange.java) |
| Download Encrypted Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/DownloadEncryptedObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/DownloadEncryptedObject.java) |
| Download Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/DownloadObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/DownloadObject.java) |
| Download Object Into Memory | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/DownloadObjectIntoMemory.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/DownloadObjectIntoMemory.java) |
| Download Public Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/DownloadPublicObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/DownloadPublicObject.java) |
| Download Requester Pays Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/DownloadRequesterPaysObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/DownloadRequesterPaysObject.java) |
| Generate Encryption Key | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/GenerateEncryptionKey.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/GenerateEncryptionKey.java) |
| Generate V4 Get Object Signed Url | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/GenerateV4GetObjectSignedUrl.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/GenerateV4GetObjectSignedUrl.java) |
| Generate V4 Put Object Signed Url | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/GenerateV4PutObjectSignedUrl.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/GenerateV4PutObjectSignedUrl.java) |
| Get Object Metadata | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/GetObjectMetadata.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/GetObjectMetadata.java) |
| List Objects | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/ListObjects.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/ListObjects.java) |
| List Objects With Old Versions | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/ListObjectsWithOldVersions.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/ListObjectsWithOldVersions.java) |
| List Objects With Prefix | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/ListObjectsWithPrefix.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/ListObjectsWithPrefix.java) |
| Make Object Public | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/MakeObjectPublic.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/MakeObjectPublic.java) |
| Move Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/MoveObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/MoveObject.java) |
| Print File Acl | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/PrintFileAcl.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/PrintFileAcl.java) |
| Print File Acl For User | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/PrintFileAclForUser.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/PrintFileAclForUser.java) |
| Release Event Based Hold | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/ReleaseEventBasedHold.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/ReleaseEventBasedHold.java) |
| Release Temporary Hold | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/ReleaseTemporaryHold.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/ReleaseTemporaryHold.java) |
| Remove File Owner | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/RemoveFileOwner.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/RemoveFileOwner.java) |
| Rotate Object Encryption Key | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/RotateObjectEncryptionKey.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/RotateObjectEncryptionKey.java) |
| Set Event Based Hold | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/SetEventBasedHold.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/SetEventBasedHold.java) |
| Set Object Metadata | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/SetObjectMetadata.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/SetObjectMetadata.java) |
| Set Object Retention Policy | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/SetObjectRetentionPolicy.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/SetObjectRetentionPolicy.java) |
| Set Temporary Hold | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/SetTemporaryHold.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/SetTemporaryHold.java) |
| Stream Object Download | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/StreamObjectDownload.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/StreamObjectDownload.java) |
| Stream Object Upload | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/StreamObjectUpload.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/StreamObjectUpload.java) |
| Upload Encrypted Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/UploadEncryptedObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/UploadEncryptedObject.java) |
| Upload Kms Encrypted Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/UploadKmsEncryptedObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/UploadKmsEncryptedObject.java) |
| Upload Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/UploadObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/UploadObject.java) |
| Upload Object From Memory | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/UploadObjectFromMemory.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/object/UploadObjectFromMemory.java) |
| Allow Divide And Conquer Download | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/transfermanager/AllowDivideAndConquerDownload.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/transfermanager/AllowDivideAndConquerDownload.java) |
| Allow Parallel Composite Upload | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/transfermanager/AllowParallelCompositeUpload.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/transfermanager/AllowParallelCompositeUpload.java) |
| Download Bucket | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/transfermanager/DownloadBucket.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/transfermanager/DownloadBucket.java) |
| Download Many | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/transfermanager/DownloadMany.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/transfermanager/DownloadMany.java) |
| Upload Directory | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/transfermanager/UploadDirectory.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/transfermanager/UploadDirectory.java) |
| Upload Many | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/transfermanager/UploadMany.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/transfermanager/UploadMany.java) |



## Troubleshooting

To get help, follow the instructions in the [shared Troubleshooting document][troubleshooting].

## Supported Java Versions

Java 8 or above is required for using this client.

Google's Java client libraries,
[Google Cloud Client Libraries][cloudlibs]
and
[Google Cloud API Libraries][apilibs],
follow the
[Oracle Java SE support roadmap][oracle]
(see the Oracle Java SE Product Releases section).

### For new development

In general, new feature development occurs with support for the lowest Java
LTS version covered by  Oracle's Premier Support (which typically lasts 5 years
from initial General Availability). If the minimum required JVM for a given
library is changed, it is accompanied by a [semver][semver] major release.

Java 11 and (in September 2021) Java 17 are the best choices for new
development.

### Keeping production systems current

Google tests its client libraries with all current LTS versions covered by
Oracle's Extended Support (which typically lasts 8 years from initial
General Availability).

#### Legacy support

Google's client libraries support legacy versions of Java runtimes with long
term stable libraries that don't receive feature updates on a best efforts basis
as it may not be possible to backport all patches.

Google provides updates on a best efforts basis to apps that continue to use
Java 7, though apps might need to upgrade to current versions of the library
that supports their JVM.

#### Where to find specific information

The latest versions and the supported Java versions are identified on
the individual GitHub repository `github.com/GoogleAPIs/java-SERVICENAME`
and on [google-cloud-java][g-c-j].

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
Java 8 | [![Kokoro CI][kokoro-badge-image-2]][kokoro-badge-link-2]
Java 8 OSX | [![Kokoro CI][kokoro-badge-image-3]][kokoro-badge-link-3]
Java 8 Windows | [![Kokoro CI][kokoro-badge-image-4]][kokoro-badge-link-4]
Java 11 | [![Kokoro CI][kokoro-badge-image-5]][kokoro-badge-link-5]

Java is a registered trademark of Oracle and/or its affiliates.

[product-docs]: https://cloud.google.com/storage
[javadocs]: https://cloud.google.com/java/docs/reference/google-cloud-storage/latest/history
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
[stability-image]: https://img.shields.io/badge/stability-stable-green
[maven-version-image]: https://img.shields.io/maven-central/v/com.google.cloud/google-cloud-storage.svg
[maven-version-link]: https://central.sonatype.com/artifact/com.google.cloud/google-cloud-storage/2.38.0
[authentication]: https://github.com/googleapis/google-cloud-java#authentication
[auth-scopes]: https://developers.google.com/identity/protocols/oauth2/scopes
[predefined-iam-roles]: https://cloud.google.com/iam/docs/understanding-roles#predefined_roles
[iam-policy]: https://cloud.google.com/iam/docs/overview#cloud-iam-policy
[developer-console]: https://console.developers.google.com/
[create-project]: https://cloud.google.com/resource-manager/docs/creating-managing-projects
[cloud-cli]: https://cloud.google.com/cli
[troubleshooting]: https://github.com/googleapis/google-cloud-java/blob/main/TROUBLESHOOTING.md
[contributing]: https://github.com/googleapis/java-storage/blob/main/CONTRIBUTING.md
[code-of-conduct]: https://github.com/googleapis/java-storage/blob/main/CODE_OF_CONDUCT.md#contributor-code-of-conduct
[license]: https://github.com/googleapis/java-storage/blob/main/LICENSE
[enable-billing]: https://cloud.google.com/apis/docs/getting-started#enabling_billing
[enable-api]: https://console.cloud.google.com/flows/enableapi?apiid=storage.googleapis.com
[libraries-bom]: https://github.com/GoogleCloudPlatform/cloud-opensource-java/wiki/The-Google-Cloud-Platform-Libraries-BOM
[shell_img]: https://gstatic.com/cloudssh/images/open-btn.png

[semver]: https://semver.org/
[cloudlibs]: https://cloud.google.com/apis/docs/client-libraries-explained
[apilibs]: https://cloud.google.com/apis/docs/client-libraries-explained#google_api_client_libraries
[oracle]: https://www.oracle.com/java/technologies/java-se-support-roadmap.html
[g-c-j]: http://github.com/googleapis/google-cloud-java
