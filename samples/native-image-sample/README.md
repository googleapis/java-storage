# Storage Sample Application with Native Image

The Storage sample application demonstrates some common operations with Google Cloud Storage and is compatible with Native Image compilation.

## Setup Instructions

You will need to follow these prerequisite steps in order to run these samples:

1. If you have not already, [create a Google Cloud Platform Project](https://cloud.google.com/resource-manager/docs/creating-managing-projects#creating_a_project).

2. Install the [Google Cloud SDK](https://cloud.google.com/sdk/) which will allow you to run the sample with your project's credentials.

   Once installed, log in with Application Default Credentials using the following command:

    ```
    gcloud auth application-default login
    ```

   **Note:** Authenticating with Application Default Credentials is convenient to use during development, but we recommend [alternate methods of authentication](https://cloud.google.com/docs/authentication/production) during production use.

3. Install the GraalVM compiler.

   You can follow the [official installation instructions](https://www.graalvm.org/docs/getting-started/#install-graalvm) from the GraalVM website.
   After following the instructions, ensure that you install the Native Image extension installed by running:

    ```
    gu install native-image
    ```

   Once you finish following the instructions, verify that the default version of Java is set to the GraalVM version by running `java -version` in a terminal.

   You will see something similar to the below output:

    ```
    $ java -version
   
    openjdk version "11.0.7" 2020-04-14
    OpenJDK Runtime Environment GraalVM CE 20.1.0 (build 11.0.7+10-jvmci-20.1-b02)
    OpenJDK 64-Bit Server VM GraalVM CE 20.1.0 (build 11.0.7+10-jvmci-20.1-b02, mixed mode, sharing)
    ```

4. [Enable the Cloud Storage APIs](https://console.cloud.google.com/apis/api/storage.googleapis.com).

### Run with Native Image Compilation

Navigate to this directory in a new terminal.

1. Compile the application using the Native Image Compiler. This step may take a few minutes.

    ```
    mvn package -P native -DskipTests
    ```

2. Run the application:

    ```
    ./target/native-image-sample
    ```

3. The application will run through basic Cloud Storage operations of creating, reading, and deleting Cloud Storage resources.

   You can manually manage Cloud Storage resources through [Google Cloud Console](https://console.cloud.google.com/storage) to verify that the resources are cleaned up.

    ```
    Creating bucket nativeimage-sample-bucket-7221f161-688c-4a7a-9120-8900d20f0802
    Write file to bucket.
    Reading the file that was written...
    Successfully wrote to file: Hello World!
    Cleaning up resources...
    Deleted file nativeimage-sample-file-5d927aaf-cb03-41de-8383-696733893db5
    Deleted bucket nativeimage-sample-bucket-7221f161-688c-4a7a-9120-8900d20f0802
   ```
   
## Sample Integration test with Native Image Support

In order to run the sample integration test as a native image, call the following command:

   ```
   mvn test -Pnative
   ```