// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/storage/v2/storage.proto

package com.google.storage.v2;

public interface CreateHmacKeyRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.CreateHmacKeyRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Required. The project that the HMAC-owning service account lives in.
   * </pre>
   *
   * <code>string project = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
   * @return The project.
   */
  java.lang.String getProject();
  /**
   * <pre>
   * Required. The project that the HMAC-owning service account lives in.
   * </pre>
   *
   * <code>string project = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
   * @return The bytes for project.
   */
  com.google.protobuf.ByteString
      getProjectBytes();

  /**
   * <pre>
   * Required. The service account to create the HMAC for.
   * </pre>
   *
   * <code>string service_account_email = 2 [(.google.api.field_behavior) = REQUIRED];</code>
   * @return The serviceAccountEmail.
   */
  java.lang.String getServiceAccountEmail();
  /**
   * <pre>
   * Required. The service account to create the HMAC for.
   * </pre>
   *
   * <code>string service_account_email = 2 [(.google.api.field_behavior) = REQUIRED];</code>
   * @return The bytes for serviceAccountEmail.
   */
  com.google.protobuf.ByteString
      getServiceAccountEmailBytes();
}
