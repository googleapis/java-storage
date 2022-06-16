package com.google.storage.v2;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * ## API Overview and Naming Syntax
 * The Cloud Storage gRPC API allows applications to read and write data through
 * the abstractions of buckets and objects. For a description of these
 * abstractions please see https://cloud.google.com/storage/docs.
 * Resources are named as follows:
 *   - Projects are referred to as they are defined by the Resource Manager API,
 *     using strings like `projects/123456` or `projects/my-string-id`.
 *   - Buckets are named using string names of the form:
 *     `projects/{project}/buckets/{bucket}`
 *     For globally unique buckets, `_` may be substituted for the project.
 *   - Objects are uniquely identified by their name along with the name of the
 *     bucket they belong to, as separate strings in this API. For example:
 *       ReadObjectRequest {
 *         bucket: 'projects/_/buckets/my-bucket'
 *         object: 'my-object'
 *       }
 *     Note that object names can contain `/` characters, which are treated as
 *     any other character (no special directory semantics).
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler",
    comments = "Source: google/storage/v2/storage.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class StorageGrpc {

  private StorageGrpc() {}

  public static final String SERVICE_NAME = "google.storage.v2.Storage";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.DeleteBucketRequest,
      com.google.protobuf.Empty> getDeleteBucketMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteBucket",
      requestType = com.google.storage.v2.DeleteBucketRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.DeleteBucketRequest,
      com.google.protobuf.Empty> getDeleteBucketMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.DeleteBucketRequest, com.google.protobuf.Empty> getDeleteBucketMethod;
    if ((getDeleteBucketMethod = StorageGrpc.getDeleteBucketMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getDeleteBucketMethod = StorageGrpc.getDeleteBucketMethod) == null) {
          StorageGrpc.getDeleteBucketMethod = getDeleteBucketMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.DeleteBucketRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteBucket"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.DeleteBucketRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("DeleteBucket"))
              .build();
        }
      }
    }
    return getDeleteBucketMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.GetBucketRequest,
      com.google.storage.v2.Bucket> getGetBucketMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetBucket",
      requestType = com.google.storage.v2.GetBucketRequest.class,
      responseType = com.google.storage.v2.Bucket.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.GetBucketRequest,
      com.google.storage.v2.Bucket> getGetBucketMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.GetBucketRequest, com.google.storage.v2.Bucket> getGetBucketMethod;
    if ((getGetBucketMethod = StorageGrpc.getGetBucketMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getGetBucketMethod = StorageGrpc.getGetBucketMethod) == null) {
          StorageGrpc.getGetBucketMethod = getGetBucketMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.GetBucketRequest, com.google.storage.v2.Bucket>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetBucket"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.GetBucketRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.Bucket.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("GetBucket"))
              .build();
        }
      }
    }
    return getGetBucketMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.CreateBucketRequest,
      com.google.storage.v2.Bucket> getCreateBucketMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateBucket",
      requestType = com.google.storage.v2.CreateBucketRequest.class,
      responseType = com.google.storage.v2.Bucket.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.CreateBucketRequest,
      com.google.storage.v2.Bucket> getCreateBucketMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.CreateBucketRequest, com.google.storage.v2.Bucket> getCreateBucketMethod;
    if ((getCreateBucketMethod = StorageGrpc.getCreateBucketMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getCreateBucketMethod = StorageGrpc.getCreateBucketMethod) == null) {
          StorageGrpc.getCreateBucketMethod = getCreateBucketMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.CreateBucketRequest, com.google.storage.v2.Bucket>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateBucket"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.CreateBucketRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.Bucket.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("CreateBucket"))
              .build();
        }
      }
    }
    return getCreateBucketMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.ListBucketsRequest,
      com.google.storage.v2.ListBucketsResponse> getListBucketsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListBuckets",
      requestType = com.google.storage.v2.ListBucketsRequest.class,
      responseType = com.google.storage.v2.ListBucketsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.ListBucketsRequest,
      com.google.storage.v2.ListBucketsResponse> getListBucketsMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.ListBucketsRequest, com.google.storage.v2.ListBucketsResponse> getListBucketsMethod;
    if ((getListBucketsMethod = StorageGrpc.getListBucketsMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getListBucketsMethod = StorageGrpc.getListBucketsMethod) == null) {
          StorageGrpc.getListBucketsMethod = getListBucketsMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.ListBucketsRequest, com.google.storage.v2.ListBucketsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListBuckets"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.ListBucketsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.ListBucketsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("ListBuckets"))
              .build();
        }
      }
    }
    return getListBucketsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.LockBucketRetentionPolicyRequest,
      com.google.storage.v2.Bucket> getLockBucketRetentionPolicyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "LockBucketRetentionPolicy",
      requestType = com.google.storage.v2.LockBucketRetentionPolicyRequest.class,
      responseType = com.google.storage.v2.Bucket.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.LockBucketRetentionPolicyRequest,
      com.google.storage.v2.Bucket> getLockBucketRetentionPolicyMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.LockBucketRetentionPolicyRequest, com.google.storage.v2.Bucket> getLockBucketRetentionPolicyMethod;
    if ((getLockBucketRetentionPolicyMethod = StorageGrpc.getLockBucketRetentionPolicyMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getLockBucketRetentionPolicyMethod = StorageGrpc.getLockBucketRetentionPolicyMethod) == null) {
          StorageGrpc.getLockBucketRetentionPolicyMethod = getLockBucketRetentionPolicyMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.LockBucketRetentionPolicyRequest, com.google.storage.v2.Bucket>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "LockBucketRetentionPolicy"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.LockBucketRetentionPolicyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.Bucket.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("LockBucketRetentionPolicy"))
              .build();
        }
      }
    }
    return getLockBucketRetentionPolicyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.iam.v1.GetIamPolicyRequest,
      com.google.iam.v1.Policy> getGetIamPolicyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetIamPolicy",
      requestType = com.google.iam.v1.GetIamPolicyRequest.class,
      responseType = com.google.iam.v1.Policy.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.iam.v1.GetIamPolicyRequest,
      com.google.iam.v1.Policy> getGetIamPolicyMethod() {
    io.grpc.MethodDescriptor<com.google.iam.v1.GetIamPolicyRequest, com.google.iam.v1.Policy> getGetIamPolicyMethod;
    if ((getGetIamPolicyMethod = StorageGrpc.getGetIamPolicyMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getGetIamPolicyMethod = StorageGrpc.getGetIamPolicyMethod) == null) {
          StorageGrpc.getGetIamPolicyMethod = getGetIamPolicyMethod =
              io.grpc.MethodDescriptor.<com.google.iam.v1.GetIamPolicyRequest, com.google.iam.v1.Policy>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetIamPolicy"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.iam.v1.GetIamPolicyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.iam.v1.Policy.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("GetIamPolicy"))
              .build();
        }
      }
    }
    return getGetIamPolicyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.iam.v1.SetIamPolicyRequest,
      com.google.iam.v1.Policy> getSetIamPolicyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SetIamPolicy",
      requestType = com.google.iam.v1.SetIamPolicyRequest.class,
      responseType = com.google.iam.v1.Policy.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.iam.v1.SetIamPolicyRequest,
      com.google.iam.v1.Policy> getSetIamPolicyMethod() {
    io.grpc.MethodDescriptor<com.google.iam.v1.SetIamPolicyRequest, com.google.iam.v1.Policy> getSetIamPolicyMethod;
    if ((getSetIamPolicyMethod = StorageGrpc.getSetIamPolicyMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getSetIamPolicyMethod = StorageGrpc.getSetIamPolicyMethod) == null) {
          StorageGrpc.getSetIamPolicyMethod = getSetIamPolicyMethod =
              io.grpc.MethodDescriptor.<com.google.iam.v1.SetIamPolicyRequest, com.google.iam.v1.Policy>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SetIamPolicy"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.iam.v1.SetIamPolicyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.iam.v1.Policy.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("SetIamPolicy"))
              .build();
        }
      }
    }
    return getSetIamPolicyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.iam.v1.TestIamPermissionsRequest,
      com.google.iam.v1.TestIamPermissionsResponse> getTestIamPermissionsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TestIamPermissions",
      requestType = com.google.iam.v1.TestIamPermissionsRequest.class,
      responseType = com.google.iam.v1.TestIamPermissionsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.iam.v1.TestIamPermissionsRequest,
      com.google.iam.v1.TestIamPermissionsResponse> getTestIamPermissionsMethod() {
    io.grpc.MethodDescriptor<com.google.iam.v1.TestIamPermissionsRequest, com.google.iam.v1.TestIamPermissionsResponse> getTestIamPermissionsMethod;
    if ((getTestIamPermissionsMethod = StorageGrpc.getTestIamPermissionsMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getTestIamPermissionsMethod = StorageGrpc.getTestIamPermissionsMethod) == null) {
          StorageGrpc.getTestIamPermissionsMethod = getTestIamPermissionsMethod =
              io.grpc.MethodDescriptor.<com.google.iam.v1.TestIamPermissionsRequest, com.google.iam.v1.TestIamPermissionsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "TestIamPermissions"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.iam.v1.TestIamPermissionsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.iam.v1.TestIamPermissionsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("TestIamPermissions"))
              .build();
        }
      }
    }
    return getTestIamPermissionsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.UpdateBucketRequest,
      com.google.storage.v2.Bucket> getUpdateBucketMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateBucket",
      requestType = com.google.storage.v2.UpdateBucketRequest.class,
      responseType = com.google.storage.v2.Bucket.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.UpdateBucketRequest,
      com.google.storage.v2.Bucket> getUpdateBucketMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.UpdateBucketRequest, com.google.storage.v2.Bucket> getUpdateBucketMethod;
    if ((getUpdateBucketMethod = StorageGrpc.getUpdateBucketMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getUpdateBucketMethod = StorageGrpc.getUpdateBucketMethod) == null) {
          StorageGrpc.getUpdateBucketMethod = getUpdateBucketMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.UpdateBucketRequest, com.google.storage.v2.Bucket>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateBucket"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.UpdateBucketRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.Bucket.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("UpdateBucket"))
              .build();
        }
      }
    }
    return getUpdateBucketMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.DeleteNotificationRequest,
      com.google.protobuf.Empty> getDeleteNotificationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteNotification",
      requestType = com.google.storage.v2.DeleteNotificationRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.DeleteNotificationRequest,
      com.google.protobuf.Empty> getDeleteNotificationMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.DeleteNotificationRequest, com.google.protobuf.Empty> getDeleteNotificationMethod;
    if ((getDeleteNotificationMethod = StorageGrpc.getDeleteNotificationMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getDeleteNotificationMethod = StorageGrpc.getDeleteNotificationMethod) == null) {
          StorageGrpc.getDeleteNotificationMethod = getDeleteNotificationMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.DeleteNotificationRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteNotification"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.DeleteNotificationRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("DeleteNotification"))
              .build();
        }
      }
    }
    return getDeleteNotificationMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.GetNotificationRequest,
      com.google.storage.v2.Notification> getGetNotificationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetNotification",
      requestType = com.google.storage.v2.GetNotificationRequest.class,
      responseType = com.google.storage.v2.Notification.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.GetNotificationRequest,
      com.google.storage.v2.Notification> getGetNotificationMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.GetNotificationRequest, com.google.storage.v2.Notification> getGetNotificationMethod;
    if ((getGetNotificationMethod = StorageGrpc.getGetNotificationMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getGetNotificationMethod = StorageGrpc.getGetNotificationMethod) == null) {
          StorageGrpc.getGetNotificationMethod = getGetNotificationMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.GetNotificationRequest, com.google.storage.v2.Notification>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetNotification"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.GetNotificationRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.Notification.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("GetNotification"))
              .build();
        }
      }
    }
    return getGetNotificationMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.CreateNotificationRequest,
      com.google.storage.v2.Notification> getCreateNotificationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateNotification",
      requestType = com.google.storage.v2.CreateNotificationRequest.class,
      responseType = com.google.storage.v2.Notification.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.CreateNotificationRequest,
      com.google.storage.v2.Notification> getCreateNotificationMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.CreateNotificationRequest, com.google.storage.v2.Notification> getCreateNotificationMethod;
    if ((getCreateNotificationMethod = StorageGrpc.getCreateNotificationMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getCreateNotificationMethod = StorageGrpc.getCreateNotificationMethod) == null) {
          StorageGrpc.getCreateNotificationMethod = getCreateNotificationMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.CreateNotificationRequest, com.google.storage.v2.Notification>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateNotification"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.CreateNotificationRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.Notification.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("CreateNotification"))
              .build();
        }
      }
    }
    return getCreateNotificationMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.ListNotificationsRequest,
      com.google.storage.v2.ListNotificationsResponse> getListNotificationsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListNotifications",
      requestType = com.google.storage.v2.ListNotificationsRequest.class,
      responseType = com.google.storage.v2.ListNotificationsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.ListNotificationsRequest,
      com.google.storage.v2.ListNotificationsResponse> getListNotificationsMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.ListNotificationsRequest, com.google.storage.v2.ListNotificationsResponse> getListNotificationsMethod;
    if ((getListNotificationsMethod = StorageGrpc.getListNotificationsMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getListNotificationsMethod = StorageGrpc.getListNotificationsMethod) == null) {
          StorageGrpc.getListNotificationsMethod = getListNotificationsMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.ListNotificationsRequest, com.google.storage.v2.ListNotificationsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListNotifications"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.ListNotificationsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.ListNotificationsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("ListNotifications"))
              .build();
        }
      }
    }
    return getListNotificationsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.ComposeObjectRequest,
      com.google.storage.v2.Object> getComposeObjectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ComposeObject",
      requestType = com.google.storage.v2.ComposeObjectRequest.class,
      responseType = com.google.storage.v2.Object.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.ComposeObjectRequest,
      com.google.storage.v2.Object> getComposeObjectMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.ComposeObjectRequest, com.google.storage.v2.Object> getComposeObjectMethod;
    if ((getComposeObjectMethod = StorageGrpc.getComposeObjectMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getComposeObjectMethod = StorageGrpc.getComposeObjectMethod) == null) {
          StorageGrpc.getComposeObjectMethod = getComposeObjectMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.ComposeObjectRequest, com.google.storage.v2.Object>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ComposeObject"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.ComposeObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.Object.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("ComposeObject"))
              .build();
        }
      }
    }
    return getComposeObjectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.DeleteObjectRequest,
      com.google.protobuf.Empty> getDeleteObjectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteObject",
      requestType = com.google.storage.v2.DeleteObjectRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.DeleteObjectRequest,
      com.google.protobuf.Empty> getDeleteObjectMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.DeleteObjectRequest, com.google.protobuf.Empty> getDeleteObjectMethod;
    if ((getDeleteObjectMethod = StorageGrpc.getDeleteObjectMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getDeleteObjectMethod = StorageGrpc.getDeleteObjectMethod) == null) {
          StorageGrpc.getDeleteObjectMethod = getDeleteObjectMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.DeleteObjectRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteObject"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.DeleteObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("DeleteObject"))
              .build();
        }
      }
    }
    return getDeleteObjectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.GetObjectRequest,
      com.google.storage.v2.Object> getGetObjectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetObject",
      requestType = com.google.storage.v2.GetObjectRequest.class,
      responseType = com.google.storage.v2.Object.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.GetObjectRequest,
      com.google.storage.v2.Object> getGetObjectMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.GetObjectRequest, com.google.storage.v2.Object> getGetObjectMethod;
    if ((getGetObjectMethod = StorageGrpc.getGetObjectMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getGetObjectMethod = StorageGrpc.getGetObjectMethod) == null) {
          StorageGrpc.getGetObjectMethod = getGetObjectMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.GetObjectRequest, com.google.storage.v2.Object>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetObject"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.GetObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.Object.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("GetObject"))
              .build();
        }
      }
    }
    return getGetObjectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.ReadObjectRequest,
      com.google.storage.v2.ReadObjectResponse> getReadObjectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReadObject",
      requestType = com.google.storage.v2.ReadObjectRequest.class,
      responseType = com.google.storage.v2.ReadObjectResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.ReadObjectRequest,
      com.google.storage.v2.ReadObjectResponse> getReadObjectMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.ReadObjectRequest, com.google.storage.v2.ReadObjectResponse> getReadObjectMethod;
    if ((getReadObjectMethod = StorageGrpc.getReadObjectMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getReadObjectMethod = StorageGrpc.getReadObjectMethod) == null) {
          StorageGrpc.getReadObjectMethod = getReadObjectMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.ReadObjectRequest, com.google.storage.v2.ReadObjectResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ReadObject"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.ReadObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.ReadObjectResponse.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("ReadObject"))
              .build();
        }
      }
    }
    return getReadObjectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.UpdateObjectRequest,
      com.google.storage.v2.Object> getUpdateObjectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateObject",
      requestType = com.google.storage.v2.UpdateObjectRequest.class,
      responseType = com.google.storage.v2.Object.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.UpdateObjectRequest,
      com.google.storage.v2.Object> getUpdateObjectMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.UpdateObjectRequest, com.google.storage.v2.Object> getUpdateObjectMethod;
    if ((getUpdateObjectMethod = StorageGrpc.getUpdateObjectMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getUpdateObjectMethod = StorageGrpc.getUpdateObjectMethod) == null) {
          StorageGrpc.getUpdateObjectMethod = getUpdateObjectMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.UpdateObjectRequest, com.google.storage.v2.Object>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateObject"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.UpdateObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.Object.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("UpdateObject"))
              .build();
        }
      }
    }
    return getUpdateObjectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.WriteObjectRequest,
      com.google.storage.v2.WriteObjectResponse> getWriteObjectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "WriteObject",
      requestType = com.google.storage.v2.WriteObjectRequest.class,
      responseType = com.google.storage.v2.WriteObjectResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.WriteObjectRequest,
      com.google.storage.v2.WriteObjectResponse> getWriteObjectMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.WriteObjectRequest, com.google.storage.v2.WriteObjectResponse> getWriteObjectMethod;
    if ((getWriteObjectMethod = StorageGrpc.getWriteObjectMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getWriteObjectMethod = StorageGrpc.getWriteObjectMethod) == null) {
          StorageGrpc.getWriteObjectMethod = getWriteObjectMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.WriteObjectRequest, com.google.storage.v2.WriteObjectResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "WriteObject"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.WriteObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.WriteObjectResponse.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("WriteObject"))
              .build();
        }
      }
    }
    return getWriteObjectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.ListObjectsRequest,
      com.google.storage.v2.ListObjectsResponse> getListObjectsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListObjects",
      requestType = com.google.storage.v2.ListObjectsRequest.class,
      responseType = com.google.storage.v2.ListObjectsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.ListObjectsRequest,
      com.google.storage.v2.ListObjectsResponse> getListObjectsMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.ListObjectsRequest, com.google.storage.v2.ListObjectsResponse> getListObjectsMethod;
    if ((getListObjectsMethod = StorageGrpc.getListObjectsMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getListObjectsMethod = StorageGrpc.getListObjectsMethod) == null) {
          StorageGrpc.getListObjectsMethod = getListObjectsMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.ListObjectsRequest, com.google.storage.v2.ListObjectsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListObjects"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.ListObjectsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.ListObjectsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("ListObjects"))
              .build();
        }
      }
    }
    return getListObjectsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.RewriteObjectRequest,
      com.google.storage.v2.RewriteResponse> getRewriteObjectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RewriteObject",
      requestType = com.google.storage.v2.RewriteObjectRequest.class,
      responseType = com.google.storage.v2.RewriteResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.RewriteObjectRequest,
      com.google.storage.v2.RewriteResponse> getRewriteObjectMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.RewriteObjectRequest, com.google.storage.v2.RewriteResponse> getRewriteObjectMethod;
    if ((getRewriteObjectMethod = StorageGrpc.getRewriteObjectMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getRewriteObjectMethod = StorageGrpc.getRewriteObjectMethod) == null) {
          StorageGrpc.getRewriteObjectMethod = getRewriteObjectMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.RewriteObjectRequest, com.google.storage.v2.RewriteResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RewriteObject"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.RewriteObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.RewriteResponse.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("RewriteObject"))
              .build();
        }
      }
    }
    return getRewriteObjectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.StartResumableWriteRequest,
      com.google.storage.v2.StartResumableWriteResponse> getStartResumableWriteMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StartResumableWrite",
      requestType = com.google.storage.v2.StartResumableWriteRequest.class,
      responseType = com.google.storage.v2.StartResumableWriteResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.StartResumableWriteRequest,
      com.google.storage.v2.StartResumableWriteResponse> getStartResumableWriteMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.StartResumableWriteRequest, com.google.storage.v2.StartResumableWriteResponse> getStartResumableWriteMethod;
    if ((getStartResumableWriteMethod = StorageGrpc.getStartResumableWriteMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getStartResumableWriteMethod = StorageGrpc.getStartResumableWriteMethod) == null) {
          StorageGrpc.getStartResumableWriteMethod = getStartResumableWriteMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.StartResumableWriteRequest, com.google.storage.v2.StartResumableWriteResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "StartResumableWrite"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.StartResumableWriteRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.StartResumableWriteResponse.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("StartResumableWrite"))
              .build();
        }
      }
    }
    return getStartResumableWriteMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.QueryWriteStatusRequest,
      com.google.storage.v2.QueryWriteStatusResponse> getQueryWriteStatusMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "QueryWriteStatus",
      requestType = com.google.storage.v2.QueryWriteStatusRequest.class,
      responseType = com.google.storage.v2.QueryWriteStatusResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.QueryWriteStatusRequest,
      com.google.storage.v2.QueryWriteStatusResponse> getQueryWriteStatusMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.QueryWriteStatusRequest, com.google.storage.v2.QueryWriteStatusResponse> getQueryWriteStatusMethod;
    if ((getQueryWriteStatusMethod = StorageGrpc.getQueryWriteStatusMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getQueryWriteStatusMethod = StorageGrpc.getQueryWriteStatusMethod) == null) {
          StorageGrpc.getQueryWriteStatusMethod = getQueryWriteStatusMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.QueryWriteStatusRequest, com.google.storage.v2.QueryWriteStatusResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QueryWriteStatus"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.QueryWriteStatusRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.QueryWriteStatusResponse.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("QueryWriteStatus"))
              .build();
        }
      }
    }
    return getQueryWriteStatusMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.GetServiceAccountRequest,
      com.google.storage.v2.ServiceAccount> getGetServiceAccountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetServiceAccount",
      requestType = com.google.storage.v2.GetServiceAccountRequest.class,
      responseType = com.google.storage.v2.ServiceAccount.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.GetServiceAccountRequest,
      com.google.storage.v2.ServiceAccount> getGetServiceAccountMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.GetServiceAccountRequest, com.google.storage.v2.ServiceAccount> getGetServiceAccountMethod;
    if ((getGetServiceAccountMethod = StorageGrpc.getGetServiceAccountMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getGetServiceAccountMethod = StorageGrpc.getGetServiceAccountMethod) == null) {
          StorageGrpc.getGetServiceAccountMethod = getGetServiceAccountMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.GetServiceAccountRequest, com.google.storage.v2.ServiceAccount>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetServiceAccount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.GetServiceAccountRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.ServiceAccount.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("GetServiceAccount"))
              .build();
        }
      }
    }
    return getGetServiceAccountMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.CreateHmacKeyRequest,
      com.google.storage.v2.CreateHmacKeyResponse> getCreateHmacKeyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateHmacKey",
      requestType = com.google.storage.v2.CreateHmacKeyRequest.class,
      responseType = com.google.storage.v2.CreateHmacKeyResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.CreateHmacKeyRequest,
      com.google.storage.v2.CreateHmacKeyResponse> getCreateHmacKeyMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.CreateHmacKeyRequest, com.google.storage.v2.CreateHmacKeyResponse> getCreateHmacKeyMethod;
    if ((getCreateHmacKeyMethod = StorageGrpc.getCreateHmacKeyMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getCreateHmacKeyMethod = StorageGrpc.getCreateHmacKeyMethod) == null) {
          StorageGrpc.getCreateHmacKeyMethod = getCreateHmacKeyMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.CreateHmacKeyRequest, com.google.storage.v2.CreateHmacKeyResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateHmacKey"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.CreateHmacKeyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.CreateHmacKeyResponse.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("CreateHmacKey"))
              .build();
        }
      }
    }
    return getCreateHmacKeyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.DeleteHmacKeyRequest,
      com.google.protobuf.Empty> getDeleteHmacKeyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteHmacKey",
      requestType = com.google.storage.v2.DeleteHmacKeyRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.DeleteHmacKeyRequest,
      com.google.protobuf.Empty> getDeleteHmacKeyMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.DeleteHmacKeyRequest, com.google.protobuf.Empty> getDeleteHmacKeyMethod;
    if ((getDeleteHmacKeyMethod = StorageGrpc.getDeleteHmacKeyMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getDeleteHmacKeyMethod = StorageGrpc.getDeleteHmacKeyMethod) == null) {
          StorageGrpc.getDeleteHmacKeyMethod = getDeleteHmacKeyMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.DeleteHmacKeyRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteHmacKey"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.DeleteHmacKeyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("DeleteHmacKey"))
              .build();
        }
      }
    }
    return getDeleteHmacKeyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.GetHmacKeyRequest,
      com.google.storage.v2.HmacKeyMetadata> getGetHmacKeyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetHmacKey",
      requestType = com.google.storage.v2.GetHmacKeyRequest.class,
      responseType = com.google.storage.v2.HmacKeyMetadata.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.GetHmacKeyRequest,
      com.google.storage.v2.HmacKeyMetadata> getGetHmacKeyMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.GetHmacKeyRequest, com.google.storage.v2.HmacKeyMetadata> getGetHmacKeyMethod;
    if ((getGetHmacKeyMethod = StorageGrpc.getGetHmacKeyMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getGetHmacKeyMethod = StorageGrpc.getGetHmacKeyMethod) == null) {
          StorageGrpc.getGetHmacKeyMethod = getGetHmacKeyMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.GetHmacKeyRequest, com.google.storage.v2.HmacKeyMetadata>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetHmacKey"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.GetHmacKeyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.HmacKeyMetadata.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("GetHmacKey"))
              .build();
        }
      }
    }
    return getGetHmacKeyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.ListHmacKeysRequest,
      com.google.storage.v2.ListHmacKeysResponse> getListHmacKeysMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListHmacKeys",
      requestType = com.google.storage.v2.ListHmacKeysRequest.class,
      responseType = com.google.storage.v2.ListHmacKeysResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.ListHmacKeysRequest,
      com.google.storage.v2.ListHmacKeysResponse> getListHmacKeysMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.ListHmacKeysRequest, com.google.storage.v2.ListHmacKeysResponse> getListHmacKeysMethod;
    if ((getListHmacKeysMethod = StorageGrpc.getListHmacKeysMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getListHmacKeysMethod = StorageGrpc.getListHmacKeysMethod) == null) {
          StorageGrpc.getListHmacKeysMethod = getListHmacKeysMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.ListHmacKeysRequest, com.google.storage.v2.ListHmacKeysResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListHmacKeys"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.ListHmacKeysRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.ListHmacKeysResponse.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("ListHmacKeys"))
              .build();
        }
      }
    }
    return getListHmacKeysMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.storage.v2.UpdateHmacKeyRequest,
      com.google.storage.v2.HmacKeyMetadata> getUpdateHmacKeyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateHmacKey",
      requestType = com.google.storage.v2.UpdateHmacKeyRequest.class,
      responseType = com.google.storage.v2.HmacKeyMetadata.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.storage.v2.UpdateHmacKeyRequest,
      com.google.storage.v2.HmacKeyMetadata> getUpdateHmacKeyMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v2.UpdateHmacKeyRequest, com.google.storage.v2.HmacKeyMetadata> getUpdateHmacKeyMethod;
    if ((getUpdateHmacKeyMethod = StorageGrpc.getUpdateHmacKeyMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getUpdateHmacKeyMethod = StorageGrpc.getUpdateHmacKeyMethod) == null) {
          StorageGrpc.getUpdateHmacKeyMethod = getUpdateHmacKeyMethod =
              io.grpc.MethodDescriptor.<com.google.storage.v2.UpdateHmacKeyRequest, com.google.storage.v2.HmacKeyMetadata>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateHmacKey"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.UpdateHmacKeyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.storage.v2.HmacKeyMetadata.getDefaultInstance()))
              .setSchemaDescriptor(new StorageMethodDescriptorSupplier("UpdateHmacKey"))
              .build();
        }
      }
    }
    return getUpdateHmacKeyMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static StorageStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StorageStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<StorageStub>() {
        @java.lang.Override
        public StorageStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new StorageStub(channel, callOptions);
        }
      };
    return StorageStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static StorageBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StorageBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<StorageBlockingStub>() {
        @java.lang.Override
        public StorageBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new StorageBlockingStub(channel, callOptions);
        }
      };
    return StorageBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static StorageFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StorageFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<StorageFutureStub>() {
        @java.lang.Override
        public StorageFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new StorageFutureStub(channel, callOptions);
        }
      };
    return StorageFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * ## API Overview and Naming Syntax
   * The Cloud Storage gRPC API allows applications to read and write data through
   * the abstractions of buckets and objects. For a description of these
   * abstractions please see https://cloud.google.com/storage/docs.
   * Resources are named as follows:
   *   - Projects are referred to as they are defined by the Resource Manager API,
   *     using strings like `projects/123456` or `projects/my-string-id`.
   *   - Buckets are named using string names of the form:
   *     `projects/{project}/buckets/{bucket}`
   *     For globally unique buckets, `_` may be substituted for the project.
   *   - Objects are uniquely identified by their name along with the name of the
   *     bucket they belong to, as separate strings in this API. For example:
   *       ReadObjectRequest {
   *         bucket: 'projects/_/buckets/my-bucket'
   *         object: 'my-object'
   *       }
   *     Note that object names can contain `/` characters, which are treated as
   *     any other character (no special directory semantics).
   * </pre>
   */
  public static abstract class StorageImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Permanently deletes an empty bucket.
     * </pre>
     */
    public void deleteBucket(com.google.storage.v2.DeleteBucketRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteBucketMethod(), responseObserver);
    }

    /**
     * <pre>
     * Returns metadata for the specified bucket.
     * </pre>
     */
    public void getBucket(com.google.storage.v2.GetBucketRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.Bucket> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetBucketMethod(), responseObserver);
    }

    /**
     * <pre>
     * Creates a new bucket.
     * </pre>
     */
    public void createBucket(com.google.storage.v2.CreateBucketRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.Bucket> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateBucketMethod(), responseObserver);
    }

    /**
     * <pre>
     * Retrieves a list of buckets for a given project.
     * </pre>
     */
    public void listBuckets(com.google.storage.v2.ListBucketsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.ListBucketsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getListBucketsMethod(), responseObserver);
    }

    /**
     * <pre>
     * Locks retention policy on a bucket.
     * </pre>
     */
    public void lockBucketRetentionPolicy(com.google.storage.v2.LockBucketRetentionPolicyRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.Bucket> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getLockBucketRetentionPolicyMethod(), responseObserver);
    }

    /**
     * <pre>
     * Gets the IAM policy for a specified bucket.
     * </pre>
     */
    public void getIamPolicy(com.google.iam.v1.GetIamPolicyRequest request,
        io.grpc.stub.StreamObserver<com.google.iam.v1.Policy> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetIamPolicyMethod(), responseObserver);
    }

    /**
     * <pre>
     * Updates an IAM policy for the specified bucket.
     * </pre>
     */
    public void setIamPolicy(com.google.iam.v1.SetIamPolicyRequest request,
        io.grpc.stub.StreamObserver<com.google.iam.v1.Policy> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSetIamPolicyMethod(), responseObserver);
    }

    /**
     * <pre>
     * Tests a set of permissions on the given bucket to see which, if
     * any, are held by the caller.
     * </pre>
     */
    public void testIamPermissions(com.google.iam.v1.TestIamPermissionsRequest request,
        io.grpc.stub.StreamObserver<com.google.iam.v1.TestIamPermissionsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getTestIamPermissionsMethod(), responseObserver);
    }

    /**
     * <pre>
     * Updates a bucket. Equivalent to JSON API's storage.buckets.patch method.
     * </pre>
     */
    public void updateBucket(com.google.storage.v2.UpdateBucketRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.Bucket> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateBucketMethod(), responseObserver);
    }

    /**
     * <pre>
     * Permanently deletes a notification subscription.
     * </pre>
     */
    public void deleteNotification(com.google.storage.v2.DeleteNotificationRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteNotificationMethod(), responseObserver);
    }

    /**
     * <pre>
     * View a notification config.
     * </pre>
     */
    public void getNotification(com.google.storage.v2.GetNotificationRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.Notification> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetNotificationMethod(), responseObserver);
    }

    /**
     * <pre>
     * Creates a notification subscription for a given bucket.
     * These notifications, when triggered, publish messages to the specified
     * Pub/Sub topics.
     * See https://cloud.google.com/storage/docs/pubsub-notifications.
     * </pre>
     */
    public void createNotification(com.google.storage.v2.CreateNotificationRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.Notification> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateNotificationMethod(), responseObserver);
    }

    /**
     * <pre>
     * Retrieves a list of notification subscriptions for a given bucket.
     * </pre>
     */
    public void listNotifications(com.google.storage.v2.ListNotificationsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.ListNotificationsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getListNotificationsMethod(), responseObserver);
    }

    /**
     * <pre>
     * Concatenates a list of existing objects into a new object in the same
     * bucket.
     * </pre>
     */
    public void composeObject(com.google.storage.v2.ComposeObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.Object> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getComposeObjectMethod(), responseObserver);
    }

    /**
     * <pre>
     * Deletes an object and its metadata. Deletions are permanent if versioning
     * is not enabled for the bucket, or if the `generation` parameter
     * is used.
     * </pre>
     */
    public void deleteObject(com.google.storage.v2.DeleteObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteObjectMethod(), responseObserver);
    }

    /**
     * <pre>
     * Retrieves an object's metadata.
     * </pre>
     */
    public void getObject(com.google.storage.v2.GetObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.Object> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetObjectMethod(), responseObserver);
    }

    /**
     * <pre>
     * Reads an object's data.
     * </pre>
     */
    public void readObject(com.google.storage.v2.ReadObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.ReadObjectResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReadObjectMethod(), responseObserver);
    }

    /**
     * <pre>
     * Updates an object's metadata.
     * Equivalent to JSON API's storage.objects.patch.
     * </pre>
     */
    public void updateObject(com.google.storage.v2.UpdateObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.Object> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateObjectMethod(), responseObserver);
    }

    /**
     * <pre>
     * Stores a new object and metadata.
     * An object can be written either in a single message stream or in a
     * resumable sequence of message streams. To write using a single stream,
     * the client should include in the first message of the stream an
     * `WriteObjectSpec` describing the destination bucket, object, and any
     * preconditions. Additionally, the final message must set 'finish_write' to
     * true, or else it is an error.
     * For a resumable write, the client should instead call
     * `StartResumableWrite()`, populating a `WriteObjectSpec` into that request.
     * They should then attach the returned `upload_id` to the first message of
     * each following call to `WriteObject`. If the stream is closed before
     * finishing the upload (either explicitly by the client or due to a network
     * error or an error response from the server), the client should do as
     * follows:
     *   - Check the result Status of the stream, to determine if writing can be
     *     resumed on this stream or must be restarted from scratch (by calling
     *     `StartResumableWrite()`). The resumable errors are DEADLINE_EXCEEDED,
     *     INTERNAL, and UNAVAILABLE. For each case, the client should use binary
     *     exponential backoff before retrying.  Additionally, writes can be
     *     resumed after RESOURCE_EXHAUSTED errors, but only after taking
     *     appropriate measures, which may include reducing aggregate send rate
     *     across clients and/or requesting a quota increase for your project.
     *   - If the call to `WriteObject` returns `ABORTED`, that indicates
     *     concurrent attempts to update the resumable write, caused either by
     *     multiple racing clients or by a single client where the previous
     *     request was timed out on the client side but nonetheless reached the
     *     server. In this case the client should take steps to prevent further
     *     concurrent writes (e.g., increase the timeouts, stop using more than
     *     one process to perform the upload, etc.), and then should follow the
     *     steps below for resuming the upload.
     *   - For resumable errors, the client should call `QueryWriteStatus()` and
     *     then continue writing from the returned `persisted_size`. This may be
     *     less than the amount of data the client previously sent. Note also that
     *     it is acceptable to send data starting at an offset earlier than the
     *     returned `persisted_size`; in this case, the service will skip data at
     *     offsets that were already persisted (without checking that it matches
     *     the previously written data), and write only the data starting from the
     *     persisted offset. This behavior can make client-side handling simpler
     *     in some cases.
     * The service will not view the object as complete until the client has
     * sent a `WriteObjectRequest` with `finish_write` set to `true`. Sending any
     * requests on a stream after sending a request with `finish_write` set to
     * `true` will cause an error. The client **should** check the response it
     * receives to determine how much data the service was able to commit and
     * whether the service views the object as complete.
     * Attempting to resume an already finalized object will result in an OK
     * status, with a WriteObjectResponse containing the finalized object's
     * metadata.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.google.storage.v2.WriteObjectRequest> writeObject(
        io.grpc.stub.StreamObserver<com.google.storage.v2.WriteObjectResponse> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getWriteObjectMethod(), responseObserver);
    }

    /**
     * <pre>
     * Retrieves a list of objects matching the criteria.
     * </pre>
     */
    public void listObjects(com.google.storage.v2.ListObjectsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.ListObjectsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getListObjectsMethod(), responseObserver);
    }

    /**
     * <pre>
     * Rewrites a source object to a destination object. Optionally overrides
     * metadata.
     * </pre>
     */
    public void rewriteObject(com.google.storage.v2.RewriteObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.RewriteResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRewriteObjectMethod(), responseObserver);
    }

    /**
     * <pre>
     * Starts a resumable write. How long the write operation remains valid, and
     * what happens when the write operation becomes invalid, are
     * service-dependent.
     * </pre>
     */
    public void startResumableWrite(com.google.storage.v2.StartResumableWriteRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.StartResumableWriteResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getStartResumableWriteMethod(), responseObserver);
    }

    /**
     * <pre>
     * Determines the `persisted_size` for an object that is being written, which
     * can then be used as the `write_offset` for the next `Write()` call.
     * If the object does not exist (i.e., the object has been deleted, or the
     * first `Write()` has not yet reached the service), this method returns the
     * error `NOT_FOUND`.
     * The client **may** call `QueryWriteStatus()` at any time to determine how
     * much data has been processed for this object. This is useful if the
     * client is buffering data and needs to know which data can be safely
     * evicted. For any sequence of `QueryWriteStatus()` calls for a given
     * object name, the sequence of returned `persisted_size` values will be
     * non-decreasing.
     * </pre>
     */
    public void queryWriteStatus(com.google.storage.v2.QueryWriteStatusRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.QueryWriteStatusResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryWriteStatusMethod(), responseObserver);
    }

    /**
     * <pre>
     * Retrieves the name of a project's Google Cloud Storage service account.
     * </pre>
     */
    public void getServiceAccount(com.google.storage.v2.GetServiceAccountRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.ServiceAccount> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetServiceAccountMethod(), responseObserver);
    }

    /**
     * <pre>
     * Creates a new HMAC key for the given service account.
     * </pre>
     */
    public void createHmacKey(com.google.storage.v2.CreateHmacKeyRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.CreateHmacKeyResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateHmacKeyMethod(), responseObserver);
    }

    /**
     * <pre>
     * Deletes a given HMAC key.  Key must be in an INACTIVE state.
     * </pre>
     */
    public void deleteHmacKey(com.google.storage.v2.DeleteHmacKeyRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteHmacKeyMethod(), responseObserver);
    }

    /**
     * <pre>
     * Gets an existing HMAC key metadata for the given id.
     * </pre>
     */
    public void getHmacKey(com.google.storage.v2.GetHmacKeyRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.HmacKeyMetadata> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetHmacKeyMethod(), responseObserver);
    }

    /**
     * <pre>
     * Lists HMAC keys under a given project with the additional filters provided.
     * </pre>
     */
    public void listHmacKeys(com.google.storage.v2.ListHmacKeysRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.ListHmacKeysResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getListHmacKeysMethod(), responseObserver);
    }

    /**
     * <pre>
     * Updates a given HMAC key state between ACTIVE and INACTIVE.
     * </pre>
     */
    public void updateHmacKey(com.google.storage.v2.UpdateHmacKeyRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.HmacKeyMetadata> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateHmacKeyMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getDeleteBucketMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.DeleteBucketRequest,
                com.google.protobuf.Empty>(
                  this, METHODID_DELETE_BUCKET)))
          .addMethod(
            getGetBucketMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.GetBucketRequest,
                com.google.storage.v2.Bucket>(
                  this, METHODID_GET_BUCKET)))
          .addMethod(
            getCreateBucketMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.CreateBucketRequest,
                com.google.storage.v2.Bucket>(
                  this, METHODID_CREATE_BUCKET)))
          .addMethod(
            getListBucketsMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.ListBucketsRequest,
                com.google.storage.v2.ListBucketsResponse>(
                  this, METHODID_LIST_BUCKETS)))
          .addMethod(
            getLockBucketRetentionPolicyMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.LockBucketRetentionPolicyRequest,
                com.google.storage.v2.Bucket>(
                  this, METHODID_LOCK_BUCKET_RETENTION_POLICY)))
          .addMethod(
            getGetIamPolicyMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.iam.v1.GetIamPolicyRequest,
                com.google.iam.v1.Policy>(
                  this, METHODID_GET_IAM_POLICY)))
          .addMethod(
            getSetIamPolicyMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.iam.v1.SetIamPolicyRequest,
                com.google.iam.v1.Policy>(
                  this, METHODID_SET_IAM_POLICY)))
          .addMethod(
            getTestIamPermissionsMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.iam.v1.TestIamPermissionsRequest,
                com.google.iam.v1.TestIamPermissionsResponse>(
                  this, METHODID_TEST_IAM_PERMISSIONS)))
          .addMethod(
            getUpdateBucketMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.UpdateBucketRequest,
                com.google.storage.v2.Bucket>(
                  this, METHODID_UPDATE_BUCKET)))
          .addMethod(
            getDeleteNotificationMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.DeleteNotificationRequest,
                com.google.protobuf.Empty>(
                  this, METHODID_DELETE_NOTIFICATION)))
          .addMethod(
            getGetNotificationMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.GetNotificationRequest,
                com.google.storage.v2.Notification>(
                  this, METHODID_GET_NOTIFICATION)))
          .addMethod(
            getCreateNotificationMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.CreateNotificationRequest,
                com.google.storage.v2.Notification>(
                  this, METHODID_CREATE_NOTIFICATION)))
          .addMethod(
            getListNotificationsMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.ListNotificationsRequest,
                com.google.storage.v2.ListNotificationsResponse>(
                  this, METHODID_LIST_NOTIFICATIONS)))
          .addMethod(
            getComposeObjectMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.ComposeObjectRequest,
                com.google.storage.v2.Object>(
                  this, METHODID_COMPOSE_OBJECT)))
          .addMethod(
            getDeleteObjectMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.DeleteObjectRequest,
                com.google.protobuf.Empty>(
                  this, METHODID_DELETE_OBJECT)))
          .addMethod(
            getGetObjectMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.GetObjectRequest,
                com.google.storage.v2.Object>(
                  this, METHODID_GET_OBJECT)))
          .addMethod(
            getReadObjectMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                com.google.storage.v2.ReadObjectRequest,
                com.google.storage.v2.ReadObjectResponse>(
                  this, METHODID_READ_OBJECT)))
          .addMethod(
            getUpdateObjectMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.UpdateObjectRequest,
                com.google.storage.v2.Object>(
                  this, METHODID_UPDATE_OBJECT)))
          .addMethod(
            getWriteObjectMethod(),
            io.grpc.stub.ServerCalls.asyncClientStreamingCall(
              new MethodHandlers<
                com.google.storage.v2.WriteObjectRequest,
                com.google.storage.v2.WriteObjectResponse>(
                  this, METHODID_WRITE_OBJECT)))
          .addMethod(
            getListObjectsMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.ListObjectsRequest,
                com.google.storage.v2.ListObjectsResponse>(
                  this, METHODID_LIST_OBJECTS)))
          .addMethod(
            getRewriteObjectMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.RewriteObjectRequest,
                com.google.storage.v2.RewriteResponse>(
                  this, METHODID_REWRITE_OBJECT)))
          .addMethod(
            getStartResumableWriteMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.StartResumableWriteRequest,
                com.google.storage.v2.StartResumableWriteResponse>(
                  this, METHODID_START_RESUMABLE_WRITE)))
          .addMethod(
            getQueryWriteStatusMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.QueryWriteStatusRequest,
                com.google.storage.v2.QueryWriteStatusResponse>(
                  this, METHODID_QUERY_WRITE_STATUS)))
          .addMethod(
            getGetServiceAccountMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.GetServiceAccountRequest,
                com.google.storage.v2.ServiceAccount>(
                  this, METHODID_GET_SERVICE_ACCOUNT)))
          .addMethod(
            getCreateHmacKeyMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.CreateHmacKeyRequest,
                com.google.storage.v2.CreateHmacKeyResponse>(
                  this, METHODID_CREATE_HMAC_KEY)))
          .addMethod(
            getDeleteHmacKeyMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.DeleteHmacKeyRequest,
                com.google.protobuf.Empty>(
                  this, METHODID_DELETE_HMAC_KEY)))
          .addMethod(
            getGetHmacKeyMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.GetHmacKeyRequest,
                com.google.storage.v2.HmacKeyMetadata>(
                  this, METHODID_GET_HMAC_KEY)))
          .addMethod(
            getListHmacKeysMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.ListHmacKeysRequest,
                com.google.storage.v2.ListHmacKeysResponse>(
                  this, METHODID_LIST_HMAC_KEYS)))
          .addMethod(
            getUpdateHmacKeyMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.storage.v2.UpdateHmacKeyRequest,
                com.google.storage.v2.HmacKeyMetadata>(
                  this, METHODID_UPDATE_HMAC_KEY)))
          .build();
    }
  }

  /**
   * <pre>
   * ## API Overview and Naming Syntax
   * The Cloud Storage gRPC API allows applications to read and write data through
   * the abstractions of buckets and objects. For a description of these
   * abstractions please see https://cloud.google.com/storage/docs.
   * Resources are named as follows:
   *   - Projects are referred to as they are defined by the Resource Manager API,
   *     using strings like `projects/123456` or `projects/my-string-id`.
   *   - Buckets are named using string names of the form:
   *     `projects/{project}/buckets/{bucket}`
   *     For globally unique buckets, `_` may be substituted for the project.
   *   - Objects are uniquely identified by their name along with the name of the
   *     bucket they belong to, as separate strings in this API. For example:
   *       ReadObjectRequest {
   *         bucket: 'projects/_/buckets/my-bucket'
   *         object: 'my-object'
   *       }
   *     Note that object names can contain `/` characters, which are treated as
   *     any other character (no special directory semantics).
   * </pre>
   */
  public static final class StorageStub extends io.grpc.stub.AbstractAsyncStub<StorageStub> {
    private StorageStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StorageStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StorageStub(channel, callOptions);
    }

    /**
     * <pre>
     * Permanently deletes an empty bucket.
     * </pre>
     */
    public void deleteBucket(com.google.storage.v2.DeleteBucketRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteBucketMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Returns metadata for the specified bucket.
     * </pre>
     */
    public void getBucket(com.google.storage.v2.GetBucketRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.Bucket> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetBucketMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Creates a new bucket.
     * </pre>
     */
    public void createBucket(com.google.storage.v2.CreateBucketRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.Bucket> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateBucketMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Retrieves a list of buckets for a given project.
     * </pre>
     */
    public void listBuckets(com.google.storage.v2.ListBucketsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.ListBucketsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getListBucketsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Locks retention policy on a bucket.
     * </pre>
     */
    public void lockBucketRetentionPolicy(com.google.storage.v2.LockBucketRetentionPolicyRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.Bucket> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getLockBucketRetentionPolicyMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Gets the IAM policy for a specified bucket.
     * </pre>
     */
    public void getIamPolicy(com.google.iam.v1.GetIamPolicyRequest request,
        io.grpc.stub.StreamObserver<com.google.iam.v1.Policy> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetIamPolicyMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Updates an IAM policy for the specified bucket.
     * </pre>
     */
    public void setIamPolicy(com.google.iam.v1.SetIamPolicyRequest request,
        io.grpc.stub.StreamObserver<com.google.iam.v1.Policy> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSetIamPolicyMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Tests a set of permissions on the given bucket to see which, if
     * any, are held by the caller.
     * </pre>
     */
    public void testIamPermissions(com.google.iam.v1.TestIamPermissionsRequest request,
        io.grpc.stub.StreamObserver<com.google.iam.v1.TestIamPermissionsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getTestIamPermissionsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Updates a bucket. Equivalent to JSON API's storage.buckets.patch method.
     * </pre>
     */
    public void updateBucket(com.google.storage.v2.UpdateBucketRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.Bucket> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateBucketMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Permanently deletes a notification subscription.
     * </pre>
     */
    public void deleteNotification(com.google.storage.v2.DeleteNotificationRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteNotificationMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * View a notification config.
     * </pre>
     */
    public void getNotification(com.google.storage.v2.GetNotificationRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.Notification> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetNotificationMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Creates a notification subscription for a given bucket.
     * These notifications, when triggered, publish messages to the specified
     * Pub/Sub topics.
     * See https://cloud.google.com/storage/docs/pubsub-notifications.
     * </pre>
     */
    public void createNotification(com.google.storage.v2.CreateNotificationRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.Notification> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateNotificationMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Retrieves a list of notification subscriptions for a given bucket.
     * </pre>
     */
    public void listNotifications(com.google.storage.v2.ListNotificationsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.ListNotificationsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getListNotificationsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Concatenates a list of existing objects into a new object in the same
     * bucket.
     * </pre>
     */
    public void composeObject(com.google.storage.v2.ComposeObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.Object> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getComposeObjectMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Deletes an object and its metadata. Deletions are permanent if versioning
     * is not enabled for the bucket, or if the `generation` parameter
     * is used.
     * </pre>
     */
    public void deleteObject(com.google.storage.v2.DeleteObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteObjectMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Retrieves an object's metadata.
     * </pre>
     */
    public void getObject(com.google.storage.v2.GetObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.Object> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetObjectMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Reads an object's data.
     * </pre>
     */
    public void readObject(com.google.storage.v2.ReadObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.ReadObjectResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getReadObjectMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Updates an object's metadata.
     * Equivalent to JSON API's storage.objects.patch.
     * </pre>
     */
    public void updateObject(com.google.storage.v2.UpdateObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.Object> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateObjectMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Stores a new object and metadata.
     * An object can be written either in a single message stream or in a
     * resumable sequence of message streams. To write using a single stream,
     * the client should include in the first message of the stream an
     * `WriteObjectSpec` describing the destination bucket, object, and any
     * preconditions. Additionally, the final message must set 'finish_write' to
     * true, or else it is an error.
     * For a resumable write, the client should instead call
     * `StartResumableWrite()`, populating a `WriteObjectSpec` into that request.
     * They should then attach the returned `upload_id` to the first message of
     * each following call to `WriteObject`. If the stream is closed before
     * finishing the upload (either explicitly by the client or due to a network
     * error or an error response from the server), the client should do as
     * follows:
     *   - Check the result Status of the stream, to determine if writing can be
     *     resumed on this stream or must be restarted from scratch (by calling
     *     `StartResumableWrite()`). The resumable errors are DEADLINE_EXCEEDED,
     *     INTERNAL, and UNAVAILABLE. For each case, the client should use binary
     *     exponential backoff before retrying.  Additionally, writes can be
     *     resumed after RESOURCE_EXHAUSTED errors, but only after taking
     *     appropriate measures, which may include reducing aggregate send rate
     *     across clients and/or requesting a quota increase for your project.
     *   - If the call to `WriteObject` returns `ABORTED`, that indicates
     *     concurrent attempts to update the resumable write, caused either by
     *     multiple racing clients or by a single client where the previous
     *     request was timed out on the client side but nonetheless reached the
     *     server. In this case the client should take steps to prevent further
     *     concurrent writes (e.g., increase the timeouts, stop using more than
     *     one process to perform the upload, etc.), and then should follow the
     *     steps below for resuming the upload.
     *   - For resumable errors, the client should call `QueryWriteStatus()` and
     *     then continue writing from the returned `persisted_size`. This may be
     *     less than the amount of data the client previously sent. Note also that
     *     it is acceptable to send data starting at an offset earlier than the
     *     returned `persisted_size`; in this case, the service will skip data at
     *     offsets that were already persisted (without checking that it matches
     *     the previously written data), and write only the data starting from the
     *     persisted offset. This behavior can make client-side handling simpler
     *     in some cases.
     * The service will not view the object as complete until the client has
     * sent a `WriteObjectRequest` with `finish_write` set to `true`. Sending any
     * requests on a stream after sending a request with `finish_write` set to
     * `true` will cause an error. The client **should** check the response it
     * receives to determine how much data the service was able to commit and
     * whether the service views the object as complete.
     * Attempting to resume an already finalized object will result in an OK
     * status, with a WriteObjectResponse containing the finalized object's
     * metadata.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.google.storage.v2.WriteObjectRequest> writeObject(
        io.grpc.stub.StreamObserver<com.google.storage.v2.WriteObjectResponse> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncClientStreamingCall(
          getChannel().newCall(getWriteObjectMethod(), getCallOptions()), responseObserver);
    }

    /**
     * <pre>
     * Retrieves a list of objects matching the criteria.
     * </pre>
     */
    public void listObjects(com.google.storage.v2.ListObjectsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.ListObjectsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getListObjectsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Rewrites a source object to a destination object. Optionally overrides
     * metadata.
     * </pre>
     */
    public void rewriteObject(com.google.storage.v2.RewriteObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.RewriteResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRewriteObjectMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Starts a resumable write. How long the write operation remains valid, and
     * what happens when the write operation becomes invalid, are
     * service-dependent.
     * </pre>
     */
    public void startResumableWrite(com.google.storage.v2.StartResumableWriteRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.StartResumableWriteResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getStartResumableWriteMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Determines the `persisted_size` for an object that is being written, which
     * can then be used as the `write_offset` for the next `Write()` call.
     * If the object does not exist (i.e., the object has been deleted, or the
     * first `Write()` has not yet reached the service), this method returns the
     * error `NOT_FOUND`.
     * The client **may** call `QueryWriteStatus()` at any time to determine how
     * much data has been processed for this object. This is useful if the
     * client is buffering data and needs to know which data can be safely
     * evicted. For any sequence of `QueryWriteStatus()` calls for a given
     * object name, the sequence of returned `persisted_size` values will be
     * non-decreasing.
     * </pre>
     */
    public void queryWriteStatus(com.google.storage.v2.QueryWriteStatusRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.QueryWriteStatusResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryWriteStatusMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Retrieves the name of a project's Google Cloud Storage service account.
     * </pre>
     */
    public void getServiceAccount(com.google.storage.v2.GetServiceAccountRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.ServiceAccount> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetServiceAccountMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Creates a new HMAC key for the given service account.
     * </pre>
     */
    public void createHmacKey(com.google.storage.v2.CreateHmacKeyRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.CreateHmacKeyResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateHmacKeyMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Deletes a given HMAC key.  Key must be in an INACTIVE state.
     * </pre>
     */
    public void deleteHmacKey(com.google.storage.v2.DeleteHmacKeyRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteHmacKeyMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Gets an existing HMAC key metadata for the given id.
     * </pre>
     */
    public void getHmacKey(com.google.storage.v2.GetHmacKeyRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.HmacKeyMetadata> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetHmacKeyMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Lists HMAC keys under a given project with the additional filters provided.
     * </pre>
     */
    public void listHmacKeys(com.google.storage.v2.ListHmacKeysRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.ListHmacKeysResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getListHmacKeysMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Updates a given HMAC key state between ACTIVE and INACTIVE.
     * </pre>
     */
    public void updateHmacKey(com.google.storage.v2.UpdateHmacKeyRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.HmacKeyMetadata> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateHmacKeyMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * ## API Overview and Naming Syntax
   * The Cloud Storage gRPC API allows applications to read and write data through
   * the abstractions of buckets and objects. For a description of these
   * abstractions please see https://cloud.google.com/storage/docs.
   * Resources are named as follows:
   *   - Projects are referred to as they are defined by the Resource Manager API,
   *     using strings like `projects/123456` or `projects/my-string-id`.
   *   - Buckets are named using string names of the form:
   *     `projects/{project}/buckets/{bucket}`
   *     For globally unique buckets, `_` may be substituted for the project.
   *   - Objects are uniquely identified by their name along with the name of the
   *     bucket they belong to, as separate strings in this API. For example:
   *       ReadObjectRequest {
   *         bucket: 'projects/_/buckets/my-bucket'
   *         object: 'my-object'
   *       }
   *     Note that object names can contain `/` characters, which are treated as
   *     any other character (no special directory semantics).
   * </pre>
   */
  public static final class StorageBlockingStub extends io.grpc.stub.AbstractBlockingStub<StorageBlockingStub> {
    private StorageBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StorageBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StorageBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Permanently deletes an empty bucket.
     * </pre>
     */
    public com.google.protobuf.Empty deleteBucket(com.google.storage.v2.DeleteBucketRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteBucketMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Returns metadata for the specified bucket.
     * </pre>
     */
    public com.google.storage.v2.Bucket getBucket(com.google.storage.v2.GetBucketRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetBucketMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Creates a new bucket.
     * </pre>
     */
    public com.google.storage.v2.Bucket createBucket(com.google.storage.v2.CreateBucketRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateBucketMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Retrieves a list of buckets for a given project.
     * </pre>
     */
    public com.google.storage.v2.ListBucketsResponse listBuckets(com.google.storage.v2.ListBucketsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getListBucketsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Locks retention policy on a bucket.
     * </pre>
     */
    public com.google.storage.v2.Bucket lockBucketRetentionPolicy(com.google.storage.v2.LockBucketRetentionPolicyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getLockBucketRetentionPolicyMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Gets the IAM policy for a specified bucket.
     * </pre>
     */
    public com.google.iam.v1.Policy getIamPolicy(com.google.iam.v1.GetIamPolicyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetIamPolicyMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Updates an IAM policy for the specified bucket.
     * </pre>
     */
    public com.google.iam.v1.Policy setIamPolicy(com.google.iam.v1.SetIamPolicyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSetIamPolicyMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Tests a set of permissions on the given bucket to see which, if
     * any, are held by the caller.
     * </pre>
     */
    public com.google.iam.v1.TestIamPermissionsResponse testIamPermissions(com.google.iam.v1.TestIamPermissionsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getTestIamPermissionsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Updates a bucket. Equivalent to JSON API's storage.buckets.patch method.
     * </pre>
     */
    public com.google.storage.v2.Bucket updateBucket(com.google.storage.v2.UpdateBucketRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateBucketMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Permanently deletes a notification subscription.
     * </pre>
     */
    public com.google.protobuf.Empty deleteNotification(com.google.storage.v2.DeleteNotificationRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteNotificationMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * View a notification config.
     * </pre>
     */
    public com.google.storage.v2.Notification getNotification(com.google.storage.v2.GetNotificationRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetNotificationMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Creates a notification subscription for a given bucket.
     * These notifications, when triggered, publish messages to the specified
     * Pub/Sub topics.
     * See https://cloud.google.com/storage/docs/pubsub-notifications.
     * </pre>
     */
    public com.google.storage.v2.Notification createNotification(com.google.storage.v2.CreateNotificationRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateNotificationMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Retrieves a list of notification subscriptions for a given bucket.
     * </pre>
     */
    public com.google.storage.v2.ListNotificationsResponse listNotifications(com.google.storage.v2.ListNotificationsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getListNotificationsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Concatenates a list of existing objects into a new object in the same
     * bucket.
     * </pre>
     */
    public com.google.storage.v2.Object composeObject(com.google.storage.v2.ComposeObjectRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getComposeObjectMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Deletes an object and its metadata. Deletions are permanent if versioning
     * is not enabled for the bucket, or if the `generation` parameter
     * is used.
     * </pre>
     */
    public com.google.protobuf.Empty deleteObject(com.google.storage.v2.DeleteObjectRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteObjectMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Retrieves an object's metadata.
     * </pre>
     */
    public com.google.storage.v2.Object getObject(com.google.storage.v2.GetObjectRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetObjectMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Reads an object's data.
     * </pre>
     */
    public java.util.Iterator<com.google.storage.v2.ReadObjectResponse> readObject(
        com.google.storage.v2.ReadObjectRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getReadObjectMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Updates an object's metadata.
     * Equivalent to JSON API's storage.objects.patch.
     * </pre>
     */
    public com.google.storage.v2.Object updateObject(com.google.storage.v2.UpdateObjectRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateObjectMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Retrieves a list of objects matching the criteria.
     * </pre>
     */
    public com.google.storage.v2.ListObjectsResponse listObjects(com.google.storage.v2.ListObjectsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getListObjectsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Rewrites a source object to a destination object. Optionally overrides
     * metadata.
     * </pre>
     */
    public com.google.storage.v2.RewriteResponse rewriteObject(com.google.storage.v2.RewriteObjectRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRewriteObjectMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Starts a resumable write. How long the write operation remains valid, and
     * what happens when the write operation becomes invalid, are
     * service-dependent.
     * </pre>
     */
    public com.google.storage.v2.StartResumableWriteResponse startResumableWrite(com.google.storage.v2.StartResumableWriteRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getStartResumableWriteMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Determines the `persisted_size` for an object that is being written, which
     * can then be used as the `write_offset` for the next `Write()` call.
     * If the object does not exist (i.e., the object has been deleted, or the
     * first `Write()` has not yet reached the service), this method returns the
     * error `NOT_FOUND`.
     * The client **may** call `QueryWriteStatus()` at any time to determine how
     * much data has been processed for this object. This is useful if the
     * client is buffering data and needs to know which data can be safely
     * evicted. For any sequence of `QueryWriteStatus()` calls for a given
     * object name, the sequence of returned `persisted_size` values will be
     * non-decreasing.
     * </pre>
     */
    public com.google.storage.v2.QueryWriteStatusResponse queryWriteStatus(com.google.storage.v2.QueryWriteStatusRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getQueryWriteStatusMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Retrieves the name of a project's Google Cloud Storage service account.
     * </pre>
     */
    public com.google.storage.v2.ServiceAccount getServiceAccount(com.google.storage.v2.GetServiceAccountRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetServiceAccountMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Creates a new HMAC key for the given service account.
     * </pre>
     */
    public com.google.storage.v2.CreateHmacKeyResponse createHmacKey(com.google.storage.v2.CreateHmacKeyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateHmacKeyMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Deletes a given HMAC key.  Key must be in an INACTIVE state.
     * </pre>
     */
    public com.google.protobuf.Empty deleteHmacKey(com.google.storage.v2.DeleteHmacKeyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteHmacKeyMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Gets an existing HMAC key metadata for the given id.
     * </pre>
     */
    public com.google.storage.v2.HmacKeyMetadata getHmacKey(com.google.storage.v2.GetHmacKeyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetHmacKeyMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Lists HMAC keys under a given project with the additional filters provided.
     * </pre>
     */
    public com.google.storage.v2.ListHmacKeysResponse listHmacKeys(com.google.storage.v2.ListHmacKeysRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getListHmacKeysMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Updates a given HMAC key state between ACTIVE and INACTIVE.
     * </pre>
     */
    public com.google.storage.v2.HmacKeyMetadata updateHmacKey(com.google.storage.v2.UpdateHmacKeyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateHmacKeyMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * ## API Overview and Naming Syntax
   * The Cloud Storage gRPC API allows applications to read and write data through
   * the abstractions of buckets and objects. For a description of these
   * abstractions please see https://cloud.google.com/storage/docs.
   * Resources are named as follows:
   *   - Projects are referred to as they are defined by the Resource Manager API,
   *     using strings like `projects/123456` or `projects/my-string-id`.
   *   - Buckets are named using string names of the form:
   *     `projects/{project}/buckets/{bucket}`
   *     For globally unique buckets, `_` may be substituted for the project.
   *   - Objects are uniquely identified by their name along with the name of the
   *     bucket they belong to, as separate strings in this API. For example:
   *       ReadObjectRequest {
   *         bucket: 'projects/_/buckets/my-bucket'
   *         object: 'my-object'
   *       }
   *     Note that object names can contain `/` characters, which are treated as
   *     any other character (no special directory semantics).
   * </pre>
   */
  public static final class StorageFutureStub extends io.grpc.stub.AbstractFutureStub<StorageFutureStub> {
    private StorageFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StorageFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StorageFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Permanently deletes an empty bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteBucket(
        com.google.storage.v2.DeleteBucketRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteBucketMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Returns metadata for the specified bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.Bucket> getBucket(
        com.google.storage.v2.GetBucketRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetBucketMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Creates a new bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.Bucket> createBucket(
        com.google.storage.v2.CreateBucketRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateBucketMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Retrieves a list of buckets for a given project.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.ListBucketsResponse> listBuckets(
        com.google.storage.v2.ListBucketsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getListBucketsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Locks retention policy on a bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.Bucket> lockBucketRetentionPolicy(
        com.google.storage.v2.LockBucketRetentionPolicyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getLockBucketRetentionPolicyMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Gets the IAM policy for a specified bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.iam.v1.Policy> getIamPolicy(
        com.google.iam.v1.GetIamPolicyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetIamPolicyMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Updates an IAM policy for the specified bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.iam.v1.Policy> setIamPolicy(
        com.google.iam.v1.SetIamPolicyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSetIamPolicyMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Tests a set of permissions on the given bucket to see which, if
     * any, are held by the caller.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.iam.v1.TestIamPermissionsResponse> testIamPermissions(
        com.google.iam.v1.TestIamPermissionsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getTestIamPermissionsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Updates a bucket. Equivalent to JSON API's storage.buckets.patch method.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.Bucket> updateBucket(
        com.google.storage.v2.UpdateBucketRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateBucketMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Permanently deletes a notification subscription.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteNotification(
        com.google.storage.v2.DeleteNotificationRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteNotificationMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * View a notification config.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.Notification> getNotification(
        com.google.storage.v2.GetNotificationRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetNotificationMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Creates a notification subscription for a given bucket.
     * These notifications, when triggered, publish messages to the specified
     * Pub/Sub topics.
     * See https://cloud.google.com/storage/docs/pubsub-notifications.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.Notification> createNotification(
        com.google.storage.v2.CreateNotificationRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateNotificationMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Retrieves a list of notification subscriptions for a given bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.ListNotificationsResponse> listNotifications(
        com.google.storage.v2.ListNotificationsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getListNotificationsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Concatenates a list of existing objects into a new object in the same
     * bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.Object> composeObject(
        com.google.storage.v2.ComposeObjectRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getComposeObjectMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Deletes an object and its metadata. Deletions are permanent if versioning
     * is not enabled for the bucket, or if the `generation` parameter
     * is used.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteObject(
        com.google.storage.v2.DeleteObjectRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteObjectMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Retrieves an object's metadata.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.Object> getObject(
        com.google.storage.v2.GetObjectRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetObjectMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Updates an object's metadata.
     * Equivalent to JSON API's storage.objects.patch.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.Object> updateObject(
        com.google.storage.v2.UpdateObjectRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateObjectMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Retrieves a list of objects matching the criteria.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.ListObjectsResponse> listObjects(
        com.google.storage.v2.ListObjectsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getListObjectsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Rewrites a source object to a destination object. Optionally overrides
     * metadata.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.RewriteResponse> rewriteObject(
        com.google.storage.v2.RewriteObjectRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRewriteObjectMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Starts a resumable write. How long the write operation remains valid, and
     * what happens when the write operation becomes invalid, are
     * service-dependent.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.StartResumableWriteResponse> startResumableWrite(
        com.google.storage.v2.StartResumableWriteRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getStartResumableWriteMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Determines the `persisted_size` for an object that is being written, which
     * can then be used as the `write_offset` for the next `Write()` call.
     * If the object does not exist (i.e., the object has been deleted, or the
     * first `Write()` has not yet reached the service), this method returns the
     * error `NOT_FOUND`.
     * The client **may** call `QueryWriteStatus()` at any time to determine how
     * much data has been processed for this object. This is useful if the
     * client is buffering data and needs to know which data can be safely
     * evicted. For any sequence of `QueryWriteStatus()` calls for a given
     * object name, the sequence of returned `persisted_size` values will be
     * non-decreasing.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.QueryWriteStatusResponse> queryWriteStatus(
        com.google.storage.v2.QueryWriteStatusRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryWriteStatusMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Retrieves the name of a project's Google Cloud Storage service account.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.ServiceAccount> getServiceAccount(
        com.google.storage.v2.GetServiceAccountRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetServiceAccountMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Creates a new HMAC key for the given service account.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.CreateHmacKeyResponse> createHmacKey(
        com.google.storage.v2.CreateHmacKeyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateHmacKeyMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Deletes a given HMAC key.  Key must be in an INACTIVE state.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteHmacKey(
        com.google.storage.v2.DeleteHmacKeyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteHmacKeyMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Gets an existing HMAC key metadata for the given id.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.HmacKeyMetadata> getHmacKey(
        com.google.storage.v2.GetHmacKeyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetHmacKeyMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Lists HMAC keys under a given project with the additional filters provided.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.ListHmacKeysResponse> listHmacKeys(
        com.google.storage.v2.ListHmacKeysRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getListHmacKeysMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Updates a given HMAC key state between ACTIVE and INACTIVE.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v2.HmacKeyMetadata> updateHmacKey(
        com.google.storage.v2.UpdateHmacKeyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateHmacKeyMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_DELETE_BUCKET = 0;
  private static final int METHODID_GET_BUCKET = 1;
  private static final int METHODID_CREATE_BUCKET = 2;
  private static final int METHODID_LIST_BUCKETS = 3;
  private static final int METHODID_LOCK_BUCKET_RETENTION_POLICY = 4;
  private static final int METHODID_GET_IAM_POLICY = 5;
  private static final int METHODID_SET_IAM_POLICY = 6;
  private static final int METHODID_TEST_IAM_PERMISSIONS = 7;
  private static final int METHODID_UPDATE_BUCKET = 8;
  private static final int METHODID_DELETE_NOTIFICATION = 9;
  private static final int METHODID_GET_NOTIFICATION = 10;
  private static final int METHODID_CREATE_NOTIFICATION = 11;
  private static final int METHODID_LIST_NOTIFICATIONS = 12;
  private static final int METHODID_COMPOSE_OBJECT = 13;
  private static final int METHODID_DELETE_OBJECT = 14;
  private static final int METHODID_GET_OBJECT = 15;
  private static final int METHODID_READ_OBJECT = 16;
  private static final int METHODID_UPDATE_OBJECT = 17;
  private static final int METHODID_LIST_OBJECTS = 18;
  private static final int METHODID_REWRITE_OBJECT = 19;
  private static final int METHODID_START_RESUMABLE_WRITE = 20;
  private static final int METHODID_QUERY_WRITE_STATUS = 21;
  private static final int METHODID_GET_SERVICE_ACCOUNT = 22;
  private static final int METHODID_CREATE_HMAC_KEY = 23;
  private static final int METHODID_DELETE_HMAC_KEY = 24;
  private static final int METHODID_GET_HMAC_KEY = 25;
  private static final int METHODID_LIST_HMAC_KEYS = 26;
  private static final int METHODID_UPDATE_HMAC_KEY = 27;
  private static final int METHODID_WRITE_OBJECT = 28;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final StorageImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(StorageImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_DELETE_BUCKET:
          serviceImpl.deleteBucket((com.google.storage.v2.DeleteBucketRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_GET_BUCKET:
          serviceImpl.getBucket((com.google.storage.v2.GetBucketRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.Bucket>) responseObserver);
          break;
        case METHODID_CREATE_BUCKET:
          serviceImpl.createBucket((com.google.storage.v2.CreateBucketRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.Bucket>) responseObserver);
          break;
        case METHODID_LIST_BUCKETS:
          serviceImpl.listBuckets((com.google.storage.v2.ListBucketsRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.ListBucketsResponse>) responseObserver);
          break;
        case METHODID_LOCK_BUCKET_RETENTION_POLICY:
          serviceImpl.lockBucketRetentionPolicy((com.google.storage.v2.LockBucketRetentionPolicyRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.Bucket>) responseObserver);
          break;
        case METHODID_GET_IAM_POLICY:
          serviceImpl.getIamPolicy((com.google.iam.v1.GetIamPolicyRequest) request,
              (io.grpc.stub.StreamObserver<com.google.iam.v1.Policy>) responseObserver);
          break;
        case METHODID_SET_IAM_POLICY:
          serviceImpl.setIamPolicy((com.google.iam.v1.SetIamPolicyRequest) request,
              (io.grpc.stub.StreamObserver<com.google.iam.v1.Policy>) responseObserver);
          break;
        case METHODID_TEST_IAM_PERMISSIONS:
          serviceImpl.testIamPermissions((com.google.iam.v1.TestIamPermissionsRequest) request,
              (io.grpc.stub.StreamObserver<com.google.iam.v1.TestIamPermissionsResponse>) responseObserver);
          break;
        case METHODID_UPDATE_BUCKET:
          serviceImpl.updateBucket((com.google.storage.v2.UpdateBucketRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.Bucket>) responseObserver);
          break;
        case METHODID_DELETE_NOTIFICATION:
          serviceImpl.deleteNotification((com.google.storage.v2.DeleteNotificationRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_GET_NOTIFICATION:
          serviceImpl.getNotification((com.google.storage.v2.GetNotificationRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.Notification>) responseObserver);
          break;
        case METHODID_CREATE_NOTIFICATION:
          serviceImpl.createNotification((com.google.storage.v2.CreateNotificationRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.Notification>) responseObserver);
          break;
        case METHODID_LIST_NOTIFICATIONS:
          serviceImpl.listNotifications((com.google.storage.v2.ListNotificationsRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.ListNotificationsResponse>) responseObserver);
          break;
        case METHODID_COMPOSE_OBJECT:
          serviceImpl.composeObject((com.google.storage.v2.ComposeObjectRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.Object>) responseObserver);
          break;
        case METHODID_DELETE_OBJECT:
          serviceImpl.deleteObject((com.google.storage.v2.DeleteObjectRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_GET_OBJECT:
          serviceImpl.getObject((com.google.storage.v2.GetObjectRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.Object>) responseObserver);
          break;
        case METHODID_READ_OBJECT:
          serviceImpl.readObject((com.google.storage.v2.ReadObjectRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.ReadObjectResponse>) responseObserver);
          break;
        case METHODID_UPDATE_OBJECT:
          serviceImpl.updateObject((com.google.storage.v2.UpdateObjectRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.Object>) responseObserver);
          break;
        case METHODID_LIST_OBJECTS:
          serviceImpl.listObjects((com.google.storage.v2.ListObjectsRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.ListObjectsResponse>) responseObserver);
          break;
        case METHODID_REWRITE_OBJECT:
          serviceImpl.rewriteObject((com.google.storage.v2.RewriteObjectRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.RewriteResponse>) responseObserver);
          break;
        case METHODID_START_RESUMABLE_WRITE:
          serviceImpl.startResumableWrite((com.google.storage.v2.StartResumableWriteRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.StartResumableWriteResponse>) responseObserver);
          break;
        case METHODID_QUERY_WRITE_STATUS:
          serviceImpl.queryWriteStatus((com.google.storage.v2.QueryWriteStatusRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.QueryWriteStatusResponse>) responseObserver);
          break;
        case METHODID_GET_SERVICE_ACCOUNT:
          serviceImpl.getServiceAccount((com.google.storage.v2.GetServiceAccountRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.ServiceAccount>) responseObserver);
          break;
        case METHODID_CREATE_HMAC_KEY:
          serviceImpl.createHmacKey((com.google.storage.v2.CreateHmacKeyRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.CreateHmacKeyResponse>) responseObserver);
          break;
        case METHODID_DELETE_HMAC_KEY:
          serviceImpl.deleteHmacKey((com.google.storage.v2.DeleteHmacKeyRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_GET_HMAC_KEY:
          serviceImpl.getHmacKey((com.google.storage.v2.GetHmacKeyRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.HmacKeyMetadata>) responseObserver);
          break;
        case METHODID_LIST_HMAC_KEYS:
          serviceImpl.listHmacKeys((com.google.storage.v2.ListHmacKeysRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.ListHmacKeysResponse>) responseObserver);
          break;
        case METHODID_UPDATE_HMAC_KEY:
          serviceImpl.updateHmacKey((com.google.storage.v2.UpdateHmacKeyRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.HmacKeyMetadata>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_WRITE_OBJECT:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.writeObject(
              (io.grpc.stub.StreamObserver<com.google.storage.v2.WriteObjectResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class StorageBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    StorageBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.google.storage.v2.StorageProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Storage");
    }
  }

  private static final class StorageFileDescriptorSupplier
      extends StorageBaseDescriptorSupplier {
    StorageFileDescriptorSupplier() {}
  }

  private static final class StorageMethodDescriptorSupplier
      extends StorageBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    StorageMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (StorageGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new StorageFileDescriptorSupplier())
              .addMethod(getDeleteBucketMethod())
              .addMethod(getGetBucketMethod())
              .addMethod(getCreateBucketMethod())
              .addMethod(getListBucketsMethod())
              .addMethod(getLockBucketRetentionPolicyMethod())
              .addMethod(getGetIamPolicyMethod())
              .addMethod(getSetIamPolicyMethod())
              .addMethod(getTestIamPermissionsMethod())
              .addMethod(getUpdateBucketMethod())
              .addMethod(getDeleteNotificationMethod())
              .addMethod(getGetNotificationMethod())
              .addMethod(getCreateNotificationMethod())
              .addMethod(getListNotificationsMethod())
              .addMethod(getComposeObjectMethod())
              .addMethod(getDeleteObjectMethod())
              .addMethod(getGetObjectMethod())
              .addMethod(getReadObjectMethod())
              .addMethod(getUpdateObjectMethod())
              .addMethod(getWriteObjectMethod())
              .addMethod(getListObjectsMethod())
              .addMethod(getRewriteObjectMethod())
              .addMethod(getStartResumableWriteMethod())
              .addMethod(getQueryWriteStatusMethod())
              .addMethod(getGetServiceAccountMethod())
              .addMethod(getCreateHmacKeyMethod())
              .addMethod(getDeleteHmacKeyMethod())
              .addMethod(getGetHmacKeyMethod())
              .addMethod(getListHmacKeysMethod())
              .addMethod(getUpdateHmacKeyMethod())
              .build();
        }
      }
    }
    return result;
  }
}
