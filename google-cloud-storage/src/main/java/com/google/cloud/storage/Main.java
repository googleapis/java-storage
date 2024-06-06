package com.google.cloud.storage;

import com.google.api.gax.paging.Page;
import com.google.common.collect.ImmutableList;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Storage storage = StorageOptions.grpc().build().getService();
        System.out.println("created");
        BucketInfo bucketInfo = BucketInfo.of("gcs-grpc-team-jl-"+UUID.randomUUID());
        storage.create(bucketInfo);

        byte[] content = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        String prefix = UUID.randomUUID().toString();
        List<Blob> blobs =
                IntStream.rangeClosed(1, 10)
                        .mapToObj(i -> String.format("%s/%02d", prefix, i))
                        .map(n -> BlobInfo.newBuilder(bucketInfo, n).build())
                        .map(info -> storage.create(info, content, Storage.BlobTargetOption.doesNotExist()))
                        .collect(ImmutableList.toImmutableList());

        while(true) {
            Page<Blob> list = storage.list(bucketInfo.getName(), Storage.BlobListOption.prefix(prefix));
            Thread.sleep(1000);
        }
//      ImmutableList<String> actual =
//              StreamSupport.stream(list.iterateAll().spliterator(), false)
//                      .map(Blob::getName)
//                      .collect(ImmutableList.toImmutableList());
//    assertThat(actual).isEqualTo(expected);
    }
}
