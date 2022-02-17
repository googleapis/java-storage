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
      <version>24.3.0</version>
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
  <version>2.4.2</version>
</dependency>

```

If you are using Gradle 5.x or later, add this to your dependencies

```Groovy
implementation platform('com.google.cloud:libraries-bom:24.3.0')

implementation 'com.google.cloud:google-cloud-storage'
```
If you are using Gradle without BOM, add this to your dependencies

```Groovy
implementation 'com.google.cloud:google-cloud-storage:2.4.2'
```

If you are using SBT, add this to your dependencies

```Scala
libraryDependencies += "com.google.cloud" % "google-cloud-storage" % "2.4.2"
```

## Authentication

See the [Authentication][authentication] section in the base directory's README.

## Authorization

The client application making API calls must be granted [authorization scopes][auth-scopes] required for the desired Cloud Storage APIs, and the authenticated principal must have the [IAM role(s)][predefined-iam-roles] required to access GCP resources using the Cloud Storage API calls.

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
[CreateBlob.java](https://github.com/googleapis/google-cloud-java/tree/main/google-cloud-examples/src/main/java/com/google/cloud/examples/storage/snippets/CreateBlob.java).

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
[CreateBlob.java](https://github.com/googleapis/google-cloud-java/tree/main/google-cloud-examples/src/main/java/com/google/cloud/examples/storage/snippets/CreateBlob.java).

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
[UpdateBlob.java](https://github.com/googleapis/google-cloud-java/tree/main/google-cloud-examples/src/main/java/com/google/cloud/examples/storage/snippets/UpdateBlob.java).

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
[CreateAndListBucketsAndBlobs.java](https://github.com/googleapis/google-cloud-java/tree/main/google-cloud-examples/src/main/java/com/google/cloud/examples/storage/snippets/CreateAndListBucketsAndBlobs.java)
we put together examples creating and listing buckets and blobs into one program. The program assumes that you are
running on Compute Engine or from your own desktop. To run the example on App Engine, simply move
the code from the main method to your application's servlet class and change the print statements to
display on your webpage.

### Example Applications

- [`StorageExample`](https://github.com/googleapis/google-cloud-java/tree/main/google-cloud-examples/src/main/java/com/google/cloud/examples/storage/StorageExample.java) is a simple command line interface that provides some of Cloud Storage's functionality.  Read more about using the application on the [`StorageExample` docs page](https://github.com/googleapis/google-cloud-java/blob/main/google-cloud-examples/README.md).
- [`Bookshelf`](https://github.com/GoogleCloudPlatform/getting-started-java/tree/main/bookshelf) - An App Engine application that manages a virtual bookshelf.
  - This app uses `google-cloud` to interface with Cloud Datastore and Cloud Storage. It also uses Cloud SQL, another Google Cloud Platform service.
- [`Flexible Environment/Storage example`](https://github.com/GoogleCloudPlatform/java-docs-samples/tree/main/flexible/cloudstorage) - An app that uploads files to a public Cloud Storage bucket on the App Engine Flexible Environment runtime.




## Samples

Samples are in the [`samples/`](https://github.com/googleapis/java-storage/tree/main/samples) directory.

| Sample                      | Source Code                       | Try it |
| --------------------------- | --------------------------------- | ------ |
| Configure Retries | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/ConfigureRetries.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/ConfigureRetries.java) |
| Generate Signed Post Policy V4 | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/GenerateSignedPostPolicyV4.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/GenerateSignedPostPolicyV4.java) |
| Get Service Account | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/GetServiceAccount.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/GetServiceAccount.java) |
| Quickstart Sample | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/QuickstartSample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/QuickstartSample.java) |
| Print Bucket Acl | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/PrintBucketAcl.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/PrintBucketAcl.java) |
| Print Bucket Acl Filter By User | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/bucket/PrintBucketAclFilterByUser.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/bucket/PrintBucketAclFilterByUser.java) |
| Add Bucket Iam Conditional Binding | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/AddBucketIamConditionalBinding.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/AddBucketIamConditionalBinding.java) |
| Add Bucket Iam Member | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/AddBucketIamMember.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/AddBucketIamMember.java) |
| Add Bucket Label | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/AddBucketLabel.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/AddBucketLabel.java) |
| Change Default Storage Class | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/ChangeDefaultStorageClass.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/ChangeDefaultStorageClass.java) |
| Configure Bucket Cors | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/ConfigureBucketCors.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/ConfigureBucketCors.java) |
| Create Bucket | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/CreateBucket.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/CreateBucket.java) |
| Create Bucket With Storage Class And Location | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/CreateBucketWithStorageClassAndLocation.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/CreateBucketWithStorageClassAndLocation.java) |
| Create Bucket With Turbo Replication | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/CreateBucketWithTurboReplication.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/CreateBucketWithTurboReplication.java) |
| Delete Bucket | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/DeleteBucket.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/DeleteBucket.java) |
| Disable Bucket Versioning | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/DisableBucketVersioning.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/DisableBucketVersioning.java) |
| Disable Lifecycle Management | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/DisableLifecycleManagement.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/DisableLifecycleManagement.java) |
| Disable Requester Pays | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/DisableRequesterPays.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/DisableRequesterPays.java) |
| Enable Bucket Versioning | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/EnableBucketVersioning.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/EnableBucketVersioning.java) |
| Enable Lifecycle Management | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/EnableLifecycleManagement.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/EnableLifecycleManagement.java) |
| Enable Requester Pays | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/EnableRequesterPays.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/EnableRequesterPays.java) |
| Get Bucket Metadata | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/GetBucketMetadata.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/GetBucketMetadata.java) |
| Get Bucket Rpo | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/GetBucketRpo.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/GetBucketRpo.java) |
| Get Public Access Prevention | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/GetPublicAccessPrevention.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/GetPublicAccessPrevention.java) |
| List Bucket Iam Members | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/ListBucketIamMembers.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/ListBucketIamMembers.java) |
| List Buckets | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/ListBuckets.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/ListBuckets.java) |
| Make Bucket Public | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/MakeBucketPublic.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/MakeBucketPublic.java) |
| Remove Bucket Cors | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/RemoveBucketCors.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/RemoveBucketCors.java) |
| Remove Bucket Default KMS Key | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/RemoveBucketDefaultKMSKey.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/RemoveBucketDefaultKMSKey.java) |
| Remove Bucket Iam Conditional Binding | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/RemoveBucketIamConditionalBinding.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/RemoveBucketIamConditionalBinding.java) |
| Remove Bucket Iam Member | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/RemoveBucketIamMember.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/RemoveBucketIamMember.java) |
| Remove Bucket Label | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/RemoveBucketLabel.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/RemoveBucketLabel.java) |
| Set Async Turbo Rpo | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/SetAsyncTurboRpo.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/SetAsyncTurboRpo.java) |
| Set Bucket Website Info | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/SetBucketWebsiteInfo.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/SetBucketWebsiteInfo.java) |
| Set Client Endpoint | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/SetClientEndpoint.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/SetClientEndpoint.java) |
| Set Default Rpo | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/SetDefaultRpo.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/SetDefaultRpo.java) |
| Set Public Access Prevention Enforced | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/SetPublicAccessPreventionEnforced.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/SetPublicAccessPreventionEnforced.java) |
| Set Public Access Prevention Inherited | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/buckets/SetPublicAccessPreventionInherited.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/buckets/SetPublicAccessPreventionInherited.java) |
| Change Object CSE Kto KMS | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/ChangeObjectCSEKtoKMS.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/ChangeObjectCSEKtoKMS.java) |
| Change Object Storage Class | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/ChangeObjectStorageClass.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/ChangeObjectStorageClass.java) |
| Compose Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/ComposeObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/ComposeObject.java) |
| Copy Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/CopyObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/CopyObject.java) |
| Copy Old Version Of Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/CopyOldVersionOfObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/CopyOldVersionOfObject.java) |
| Delete Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/DeleteObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/DeleteObject.java) |
| Delete Old Version Of Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/DeleteOldVersionOfObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/DeleteOldVersionOfObject.java) |
| Download Encrypted Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/DownloadEncryptedObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/DownloadEncryptedObject.java) |
| Download Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/DownloadObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/DownloadObject.java) |
| Download Object Into Memory | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/DownloadObjectIntoMemory.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/DownloadObjectIntoMemory.java) |
| Download Public Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/DownloadPublicObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/DownloadPublicObject.java) |
| Download Requester Pays Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/DownloadRequesterPaysObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/DownloadRequesterPaysObject.java) |
| Generate Encryption Key | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/GenerateEncryptionKey.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/GenerateEncryptionKey.java) |
| Generate V4 Get Object Signed Url | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/GenerateV4GetObjectSignedUrl.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/GenerateV4GetObjectSignedUrl.java) |
| Generate V4 Put Object Signed Url | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/GenerateV4PutObjectSignedUrl.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/GenerateV4PutObjectSignedUrl.java) |
| Get Object Metadata | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/GetObjectMetadata.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/GetObjectMetadata.java) |
| List Objects | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/ListObjects.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/ListObjects.java) |
| List Objects With Old Versions | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/ListObjectsWithOldVersions.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/ListObjectsWithOldVersions.java) |
| List Objects With Prefix | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/ListObjectsWithPrefix.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/ListObjectsWithPrefix.java) |
| Make Object Public | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/MakeObjectPublic.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/MakeObjectPublic.java) |
| Move Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/MoveObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/MoveObject.java) |
| Rotate Object Encryption Key | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/RotateObjectEncryptionKey.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/RotateObjectEncryptionKey.java) |
| Set Object Metadata | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/SetObjectMetadata.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/SetObjectMetadata.java) |
| Stream Object Download | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/StreamObjectDownload.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/StreamObjectDownload.java) |
| Stream Object Upload | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/StreamObjectUpload.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/StreamObjectUpload.java) |
| Upload Encrypted Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/UploadEncryptedObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/UploadEncryptedObject.java) |
| Upload KMS Encrypted Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/UploadKMSEncryptedObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/UploadKMSEncryptedObject.java) |
| Upload Object | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/UploadObject.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/UploadObject.java) |
| Upload Object From Memory | [source code](https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/objects/UploadObjectFromMemory.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-storage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/storage/objects/UploadObjectFromMemory.java) |



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
[maven-version-link]: https://search.maven.org/search?q=g:com.google.cloud%20AND%20a:google-cloud-storage&core=gav
[authentication]: https://github.com/googleapis/google-cloud-java#authentication
[auth-scopes]: https://developers.google.com/identity/protocols/oauth2/scopes
[predefined-iam-roles]: https://cloud.google.com/iam/docs/understanding-roles#predefined_roles
[iam-policy]: https://cloud.google.com/iam/docs/overview#cloud-iam-policy
[developer-console]: https://console.developers.google.com/
[create-project]: https://cloud.google.com/resource-manager/docs/creating-managing-projects
[cloud-sdk]: https://cloud.google.com/sdk/
[troubleshooting]: https://github.com/googleapis/google-cloud-common/blob/main/troubleshooting/readme.md#troubleshooting
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
