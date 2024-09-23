package com.google.cloud.storage.transfermanager;

final class UploadSource<T> {
    private final String blobName;
    private final T source;
    private final boolean isParallelCompositeUpload;

    UploadSource(String blobName, T source, boolean isParallelCompositeUpload) {
        this.blobName = blobName;
        this.source = source;
        this.isParallelCompositeUpload = isParallelCompositeUpload;
    }

    public String getBlobName() {
        return blobName;
    }

    public T getSource() {
        return source;
    }

    public boolean isParallelCompositeUpload() {
        return isParallelCompositeUpload;
    }
}