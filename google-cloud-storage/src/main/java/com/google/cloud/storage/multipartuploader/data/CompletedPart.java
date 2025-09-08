package com.google.cloud.storage.multipartuploader.data;

public class CompletedPart {

  private final int partNumber;

  private final String etag;

  private CompletedPart(int partNumber, String etag) {
    this.partNumber = partNumber;
    this.etag = etag;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public int getPartNumber() {
    return partNumber;
  }

  public String getEtag() {
    return etag;
  }

  public static class Builder {
    private int partNumber;
    private String etag;

    public Builder setPartNumber(int partNumber) {
      this.partNumber = partNumber;
      return this;
    }

    public Builder setEtag(String etag) {
      this.etag = etag;
      return this;
    }

    public CompletedPart build() {
      return new CompletedPart(partNumber, etag);
    }
  }
}
