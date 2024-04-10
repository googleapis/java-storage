package com.google.cloud.storage;

import com.google.storage.v2.ReadObjectResponse;

interface ResponseContentLifecycleManager {
    ResponseContentLifecycleHandle get(ReadObjectResponse response);

    static ResponseContentLifecycleManager noop() {
        return response ->
                new ResponseContentLifecycleHandle(
                        response,
                        () -> {
                            // no-op
                        });
    }
}
