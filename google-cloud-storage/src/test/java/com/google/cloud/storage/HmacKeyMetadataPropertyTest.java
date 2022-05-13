package com.google.cloud.storage;

import com.google.storage.v2.HmacKeyMetadata;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class HmacKeyMetadataPropertyTest {
    @Property
    void allKeysDecode(@ForAll HmacKeyMetadata metadata) {
        HmacKey.HmacKeyMetadata decoded = Conversions.grpc().hmacKeyMetadata().decode(metadata);
        assertThat(decoded.getAccessId()).isEqualTo(metadata.getAccessId());
        assertThat(decoded.getProjectId()).isEqualTo(metadata.getProject());
        assertThat(TimeUnit.MILLISECONDS.toSeconds(decoded.getUpdateTime())).isEqualTo(metadata.getUpdateTime().getSeconds());
        assertThat(TimeUnit.MILLISECONDS.toSeconds(decoded.getCreateTime())).isEqualTo(metadata.getCreateTime().getSeconds());
        assertThat(decoded.getId()).isEqualTo(metadata.getId());
        assertThat(decoded.getState().toString()).isEqualTo(metadata.getState());
        assertThat(decoded.getServiceAccount().getEmail()).isEqualTo(metadata.getServiceAccountEmail());
        //TODO etag
    }

}
