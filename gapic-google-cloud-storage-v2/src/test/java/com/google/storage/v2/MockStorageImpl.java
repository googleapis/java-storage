/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.storage.v2;

import com.google.api.core.BetaApi;
import com.google.protobuf.AbstractMessage;
import com.google.storage.v2.StorageGrpc.StorageImplBase;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.annotation.Generated;

@BetaApi
@Generated("by gapic-generator-java")
public class MockStorageImpl extends StorageImplBase {
  private List<AbstractMessage> requests;
  private Queue<java.lang.Object> responses;

  public MockStorageImpl() {
    requests = new ArrayList<>();
    responses = new LinkedList<>();
  }

  public List<AbstractMessage> getRequests() {
    return requests;
  }

  public void addResponse(AbstractMessage response) {
    responses.add(response);
  }

  public void setResponses(List<AbstractMessage> responses) {
    this.responses = new LinkedList<java.lang.Object>(responses);
  }

  public void addException(Exception exception) {
    responses.add(exception);
  }

  public void reset() {
    requests = new ArrayList<>();
    responses = new LinkedList<>();
  }

  @Override
  public void readObject(
      ReadObjectRequest request, StreamObserver<ReadObjectResponse> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof ReadObjectResponse) {
      requests.add(request);
      responseObserver.onNext(((ReadObjectResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ReadObject, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ReadObjectResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public StreamObserver<WriteObjectRequest> writeObject(
      final StreamObserver<WriteObjectResponse> responseObserver) {
    StreamObserver<WriteObjectRequest> requestObserver =
        new StreamObserver<WriteObjectRequest>() {
          @Override
          public void onNext(WriteObjectRequest value) {
            requests.add(value);
            final java.lang.Object response = responses.remove();
            if (response instanceof WriteObjectResponse) {
              responseObserver.onNext(((WriteObjectResponse) response));
            } else if (response instanceof Exception) {
              responseObserver.onError(((Exception) response));
            } else {
              responseObserver.onError(
                  new IllegalArgumentException(
                      String.format(
                          "Unrecognized response type %s for method WriteObject, expected %s or %s",
                          response == null ? "null" : response.getClass().getName(),
                          WriteObjectResponse.class.getName(),
                          Exception.class.getName())));
            }
          }

          @Override
          public void onError(Throwable t) {
            responseObserver.onError(t);
          }

          @Override
          public void onCompleted() {
            responseObserver.onCompleted();
          }
        };
    return requestObserver;
  }

  @Override
  public void startResumableWrite(
      StartResumableWriteRequest request,
      StreamObserver<StartResumableWriteResponse> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof StartResumableWriteResponse) {
      requests.add(request);
      responseObserver.onNext(((StartResumableWriteResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method StartResumableWrite, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  StartResumableWriteResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void queryWriteStatus(
      QueryWriteStatusRequest request, StreamObserver<QueryWriteStatusResponse> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof QueryWriteStatusResponse) {
      requests.add(request);
      responseObserver.onNext(((QueryWriteStatusResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method QueryWriteStatus, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  QueryWriteStatusResponse.class.getName(),
                  Exception.class.getName())));
    }
  }
}
