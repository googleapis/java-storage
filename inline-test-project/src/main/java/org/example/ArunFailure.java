/*
 * Copyright 2024 Google LLC
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

package org.example;
import com.google.auth.Credentials;
import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.CredentialAccessBoundary;
import com.google.auth.oauth2.DownscopedCredentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.NoCredentials;
import com.google.cloud.storage.*;
import com.google.common.collect.ImmutableList;
import com.google.storage.v2.BucketName;
import com.google.storage.v2.GetObjectRequest;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

import java.io.IOException;

// -Djava.util.logging.config.file=/home/franknatividad/Documents/TestJavaStorageProject/logging.properties
//
public class ArunFailure {
    private static AccessToken getTokenFromBroker(String bucketName, String objectPrefix)
            throws IOException {
        // Retrieve the source credentials from ADC.
        GoogleCredentials sourceCredentials =
                GoogleCredentials.getApplicationDefault()
                        .createScoped("https://www.googleapis.com/auth/cloud-platform");
        // Initialize the Credential Access Boundary rules.
        String availableResource = "//storage.googleapis.com/projects/_/buckets/" + bucketName;
        // Downscoped credentials will have readonly access to the resource.
        String availablePermission = "inRole:roles/storage.objectViewer";
        // Only objects starting with the specified prefix string in the object name will be allowed
        // read access.
        String expression =
                "resource.name.startsWith('projects/_/buckets/"
                        + bucketName
                        + "/objects/"
                        + objectPrefix
                        + "')";
        // Build the AvailabilityCondition.
        CredentialAccessBoundary.AccessBoundaryRule.AvailabilityCondition availabilityCondition =
                CredentialAccessBoundary.AccessBoundaryRule.AvailabilityCondition.newBuilder()
                        .setExpression(expression)
                        .build();
        // Define the single access boundary rule using the above properties.
        CredentialAccessBoundary.AccessBoundaryRule rule =
                CredentialAccessBoundary.AccessBoundaryRule.newBuilder()
                        .setAvailableResource(availableResource)
                        .addAvailablePermission(availablePermission)
                        .setAvailabilityCondition(availabilityCondition)
                        .build();
        // Define the Credential Access Boundary with all the relevant rules.
        CredentialAccessBoundary credentialAccessBoundary =
                CredentialAccessBoundary.newBuilder().addRule(rule).build();
        // Create the downscoped credentials.
        DownscopedCredentials downscopedCredentials =
                DownscopedCredentials.newBuilder()
                        .setSourceCredential(sourceCredentials)
                        .setCredentialAccessBoundary(credentialAccessBoundary)
                        .build();
        // Retrieve the token.
        // This will need to be passed to the Token Consumer.
        AccessToken accessToken = downscopedCredentials.refreshAccessToken();
        return accessToken;
    }
    private static class DownscopedTokenByRequestInterceptor implements ClientInterceptor {
        public final Metadata.Key<String> AUTH_KEY =
                Metadata.Key.of(AUTHORIZATION, Metadata.ASCII_STRING_MARSHALLER);
        @Override
        public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
            if (!method.getFullMethodName().equals("google.storage.v2.Storage/GetObject")) {
                // Only support PCU based operations
                return next.newCall(method, callOptions);
            }
            return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
                Listener responseListener;
                Metadata headers;
                int flowControlRequests;
                String methodName;
                @Override
                public void start(Listener<RespT> responseListener, Metadata headers) {
                    this.responseListener = responseListener;
                    this.headers = headers;
                    this.methodName = method.getFullMethodName();
                }
                @Override
                public void sendMessage(ReqT message) {
                    if (headers != null) { // start() is required before sendMessage()
                        try {
                            GetObjectRequest req = (GetObjectRequest)message;
                            String bucketName = BucketName.parse(req.getBucket()).getBucket();
                            String token = getTokenFromBroker(bucketName, req.getObject()).getTokenValue();
                            headers.put(AUTH_KEY, "Bearer " + token);
                        } catch (Exception e) {
                            halfClose();
                        }
                        delegate().start(responseListener, headers);
                        if (flowControlRequests != 0) {
                            super.request(flowControlRequests);
                        }
                        headers = null;
                    }
                    super.sendMessage(message);
                }
                @Override
                public void request(int numMessages) {
                    if (headers != null) {
                        this.flowControlRequests += numMessages;
                    } else {
                        super.request(numMessages);
                    }
                }
            };
        }
    }

    private static Credentials getNoCredentialsWorkaround() {
        GoogleCredentials theCred = GoogleCredentials.create(new AccessToken("", null));
        return theCred;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("start");
        Storage storage = StorageOptions.grpc()
                .setAttemptDirectPath(true)
                .setGrpcInterceptorProvider(() -> ImmutableList.of(new DownscopedTokenByRequestInterceptor()))
                .setCredentials(NoCredentials.getInstance())
                .build().getService();
        Blob blob = storage.get("anima-frank-us-central1-gcs-grpc-team-test-bucket", "pom.xml");
        System.out.println("Downloaded blob?: " + (blob != null));
    }
}