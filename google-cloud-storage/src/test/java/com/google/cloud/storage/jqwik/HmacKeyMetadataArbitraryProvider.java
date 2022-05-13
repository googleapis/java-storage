package com.google.cloud.storage.jqwik;

import com.google.cloud.storage.HmacKey;
import com.google.storage.v2.HmacKeyMetadata;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.constraints.UpperChars;
import net.jqwik.api.providers.ArbitraryProvider;
import net.jqwik.api.providers.TypeUsage;
import net.jqwik.web.api.Web;

import java.util.Collections;
import java.util.Set;

public class HmacKeyMetadataArbitraryProvider implements ArbitraryProvider {
    @Override
    public boolean canProvideFor(TypeUsage targetType) {
        return targetType.isOfType(HmacKeyMetadata.class);
    }

    @Override
    public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {

        @UpperChars Arbitrary<String> accessID = Arbitraries.strings().alpha().numeric().ofLength(61);

        Arbitrary<HmacKeyMetadata> as =
                Combinators.combine(
                        accessID,
                        StorageArbitraries.projectID(),
                        Arbitraries.of(HmacKey.HmacKeyState.class),
                        StorageArbitraries.timestamp(),
                        StorageArbitraries.timestamp(),
                        Web.emails()
                ).as(
                        (accessId, projectID, state, createTime, updateTime, email) ->
                            HmacKeyMetadata.newBuilder()
                                    .setAccessId(accessId)
                                    .setProject(projectID.get())
                                    .setId(projectID.get() + "/" + accessId)
                                    .setState(state.toString())
                                    .setCreateTime(createTime)
                                    .setUpdateTime(updateTime)
                                    .setServiceAccountEmail(email)
                                    .build());
        return Collections.singleton(as);
    }
}
