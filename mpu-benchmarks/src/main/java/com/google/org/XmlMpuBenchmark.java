package com.google.org;

import com.google.cloud.storage.HttpStorageOptions;
import com.google.cloud.storage.MultipartUploadClient;
import com.google.cloud.storage.MultipartUploadSettings;
import com.google.cloud.storage.RequestBody;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CompleteMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CompleteMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.CompletedMultipartUpload;
import com.google.cloud.storage.multipartupload.model.CompletedPart;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.ListPartsRequest;
import com.google.cloud.storage.multipartupload.model.ListPartsResponse;
import com.google.cloud.storage.multipartupload.model.UploadPartRequest;
import com.google.cloud.storage.multipartupload.model.UploadPartResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class XmlMpuBenchmark {

    private static final long MEGABYTE = 1024L * 1024L;

    // mvn compile exec:java -Dexec.mainClass="org.example.XmlMpuBenchmark" -Dexec.args="gcs-hyd-connector-benchmarks shreyassinha 5GB /usr/local/google/home/shreyassinha/5GB_file 32 8 true"
    // java -Xmx5g -jar gcs-xml-mpu-benchmark-1.0-SNAPSHOT.jar gcs-hyd-connector-benchmarks shreyassinha 100GB7 large_file.bin 64 64
    public static void main(String[] args) throws Exception {
        BenchmarkConfig config = BenchmarkConfig.fromArgs(args);
        if (config == null || !config.validate()) {
            return;
        }

        HttpStorageOptions options =
                HttpStorageOptions.newBuilder().setProjectId(config.projectId).build();
        MultipartUploadSettings settings = MultipartUploadSettings.of(options);
        MultipartUploadClient multipartUploadClient = MultipartUploadClient.create(settings);

        runBenchmark(multipartUploadClient, config);
    }

    private static void runBenchmark(MultipartUploadClient multipartUploadClient, BenchmarkConfig config)
            throws Exception {
        long totalStartTime = System.nanoTime();

        long createStartTime = System.nanoTime();
        CreateMultipartUploadResponse initiatedUpload = initiateUpload(multipartUploadClient, config);
        long createEndTime = System.nanoTime();
        String uploadId = initiatedUpload.uploadId();
        if (!config.quietMode) {
            System.out.println("Initiated multipart upload with ID: " + uploadId);
        }

        ExecutorService executor = Executors.newFixedThreadPool(config.numberOfThreads);
        List<Long> latencies = Collections.synchronizedList(new ArrayList<>());

        try {
            List<CompletedPart> completedParts =
                    uploadParts(multipartUploadClient, config, uploadId, executor, latencies);

            if (!config.quietMode) {
                listParts(config.bucketName, config.objectName, uploadId, multipartUploadClient);
            }

            long completeStartTime = System.nanoTime();
            CompleteMultipartUploadResponse completeResponse =
                    completeUpload(multipartUploadClient, config, uploadId, completedParts);
            long completeEndTime = System.nanoTime();

            if (!config.quietMode) {
                System.out.println(completeResponse);
                System.out.println("✅ Successfully completed multipart upload for " + config.objectName);
            }

            long totalEndTime = System.nanoTime();
            logMetrics(
                    totalStartTime,
                    totalEndTime,
                    config.fileSize,
                    latencies,
                    createStartTime,
                    createEndTime,
                    completeStartTime,
                    completeEndTime,
                    config.quietMode);

        } catch (Exception e) {
            System.err.println("❌ An error occurred during upload: " + e.getMessage());
            abortUpload(multipartUploadClient, config, uploadId);
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }

    private static CreateMultipartUploadResponse initiateUpload(
            MultipartUploadClient client, BenchmarkConfig config) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("mpu-key1", "mpu-value1");
        metadata.put("mpu-key2", "mpu-value2");
        metadata.put("mpu-key3", "mpu-value3");
        CreateMultipartUploadRequest createMultipartUploadRequest =
                CreateMultipartUploadRequest.builder()
                        .bucket(config.bucketName)
                        .key(config.objectName)
                        .metadata(metadata)
                        .storageClass(StorageClass.COLDLINE)
                        .contentType("text/plain")
                        .build();

        return client.createMultipartUpload(createMultipartUploadRequest);
    }

    private static List<CompletedPart> uploadParts(
            MultipartUploadClient client,
            BenchmarkConfig config,
            String uploadId,
            ExecutorService executor,
            List<Long> latencies)
            throws InterruptedException, ExecutionException {
        List<Callable<CompletedPart>> uploadTasks = new ArrayList<>();
        long filePosition = 0;
        int partNumber = 1;
        while (filePosition < config.fileSize) {
            long partSizeForUpload = Math.min(config.partSize, (config.fileSize - filePosition));
            long finalFilePosition = filePosition;
            int finalPartNumber = partNumber;

            Callable<CompletedPart> uploadTask =
                    () -> {
                        try (RandomAccessFile randomAccessFile = new RandomAccessFile(config.file, "r")) {
                            byte[] partBuffer = new byte[(int) partSizeForUpload];
                            randomAccessFile.seek(finalFilePosition);
                            randomAccessFile.readFully(partBuffer);

                            UploadPartRequest uploadPartRequest =
                                    UploadPartRequest.builder()
                                            .bucket(config.bucketName)
                                            .key(config.objectName)
                                            .uploadId(uploadId)
                                            .partNumber(finalPartNumber)
                                            .build();

                            long uploadPartStartTime = System.nanoTime();
                            UploadPartResponse uploadPartResponse =
                                    client.uploadPart(uploadPartRequest, RequestBody.of(ByteBuffer.wrap(partBuffer)));
                            long uploadPartEndTime = System.nanoTime();
                            long latency = uploadPartEndTime - uploadPartStartTime;
                            latencies.add(latency);
                            // System.out.printf(
                            //     "Uploaded part %d with ETag: %s, md5=%s. Latency: %.3f ms%n",
                            //     finalPartNumber,
                            //     uploadPartResponse.eTag(),
                            //     uploadPartResponse.md5(),
                            //     latency / 1e6);

                            return CompletedPart.builder()
                                    .partNumber(finalPartNumber)
                                    .eTag(uploadPartResponse.eTag())
                                    .build();
                        }
                    };
            uploadTasks.add(uploadTask);

            filePosition += partSizeForUpload;
            partNumber++;
        }

        List<Future<CompletedPart>> futures = executor.invokeAll(uploadTasks);

        List<CompletedPart> completedParts = new ArrayList<>();
        for (Future<CompletedPart> future : futures) {
            completedParts.add(future.get());
        }
        completedParts.sort(Comparator.comparingInt(CompletedPart::partNumber));
        return completedParts;
    }

    private static CompleteMultipartUploadResponse completeUpload(
            MultipartUploadClient client,
            BenchmarkConfig config,
            String uploadId,
            List<CompletedPart> parts) {
        CompletedMultipartUpload completedMultipartUpload =
                CompletedMultipartUpload.builder().parts(parts).build();

        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                CompleteMultipartUploadRequest.builder()
                        .bucket(config.bucketName)
                        .key(config.objectName)
                        .uploadId(uploadId)
                        .multipartUpload(completedMultipartUpload)
                        .build();

        return client.completeMultipartUpload(completeMultipartUploadRequest);
    }

    private static void abortUpload(
            MultipartUploadClient client, BenchmarkConfig config, String uploadId) {
        AbortMultipartUploadRequest abortRequest =
                AbortMultipartUploadRequest.builder()
                        .bucket(config.bucketName)
                        .key(config.objectName)
                        .uploadId(uploadId)
                        .build();
        client.abortMultipartUpload(abortRequest);
        System.err.println("Aborted multipart upload with ID: " + uploadId);
    }

    private static class BenchmarkConfig {
        final String projectId;
        final String bucketName;
        final String objectName;
        final String filePath;
        final int numberOfThreads;
        final long partSize;
        final File file;
        final long fileSize;
        final boolean quietMode;

        BenchmarkConfig(
                String projectId,
                String bucketName,
                String objectName,
                String filePath,
                int numberOfThreads,
                long partSize,
                boolean quietMode) {
            this.projectId = projectId;
            this.bucketName = bucketName;
            this.objectName = objectName;
            this.filePath = filePath;
            this.numberOfThreads = numberOfThreads;
            this.partSize = partSize;
            this.file = new File(filePath);
            this.fileSize = this.file.length();
            this.quietMode = quietMode;
        }

        static BenchmarkConfig fromArgs(String[] args) {
            if (args.length < 6) {
                System.out.println(
                        "Usage: XmlMpuBenchmark <project_id> <bucket_name> <object_name> <file_path> <number_of_threads> <part_size_mb> [quiet_mode]");
                return null;
            }

            String projectId = args[0];
            String bucketName = args[1];
            String objectName = args[2];
            String filePath = args[3];
            int numberOfThreads = Integer.parseInt(args[4]);
            long partSize = Long.parseLong(args[5]) * MEGABYTE;
            boolean quietMode = args.length > 6 && Boolean.parseBoolean(args[6]);

            return new BenchmarkConfig(
                    projectId, bucketName, objectName, filePath, numberOfThreads, partSize, quietMode);
        }

        boolean validate() {
            if (!file.exists() || !file.isFile()) {
                System.err.println("Error: File not found or is not a regular file: " + filePath);
                return false;
            }
            if (fileSize == 0) {
                System.err.println("Error: Input file is empty, nothing to upload.");
                return false;
            }
            return true;
        }
    }

    private static void logMetrics(
            long totalStartTime,
            long totalEndTime,
            long fileSize,
            List<Long> latencies,
            long createStartTime,
            long createEndTime,
            long completeStartTime,
            long completeEndTime,
            boolean quietMode) {
        long totalTime = totalEndTime - totalStartTime;
        double totalTimeInSeconds = totalTime / 1e9;
        double fileSizeInMB = (double) fileSize / MEGABYTE;
        double throughput = fileSizeInMB / totalTimeInSeconds;

        if (!quietMode) {
            System.out.printf(
                    "CreateMultipartUpload latency: %.3f ms%n", (createEndTime - createStartTime) / 1e6);
            System.out.printf(
                    "CompleteMultipartUpload latency: %.3f ms %n",
                    (completeEndTime - completeStartTime) / 1e6);

            System.out.println(" --- Aggregate Metrics ---");
            System.out.printf("Total time: %.3f s%n", totalTimeInSeconds);
            System.out.printf("File size: %.3f MB%n", fileSizeInMB);
        }
        System.out.printf("Throughput: %.3f MB/s%n", throughput);

        if (!quietMode) {
            double averageLatency =
                    latencies.stream().mapToLong(Long::longValue).average().orElse(0) / 1e6;
            double maxLatency = latencies.stream().mapToLong(Long::longValue).max().orElse(0) / 1e6;
            double minLatency = latencies.stream().mapToLong(Long::longValue).min().orElse(0) / 1e6;
            System.out.println(" --- Per-Part Upload Latency (ms) ---");
            System.out.printf("Average: %.3f%n", averageLatency);
            System.out.printf("Min: %.3f%n", minLatency);
            System.out.printf("Max: %.3f%n", maxLatency);
        }
    }

    private static void listParts(
            String bucketName, String objectName, String uploadId, MultipartUploadClient client) {
        ListPartsRequest listPartsRequest =
                ListPartsRequest.builder().bucket(bucketName).key(objectName).uploadId(uploadId).build();

        ListPartsResponse listPartsResponse = client.listParts(listPartsRequest);
        System.out.printf("List parts response:%s%n", listPartsResponse);
    }
}