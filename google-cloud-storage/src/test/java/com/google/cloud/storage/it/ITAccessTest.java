/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.storage.it;

import static com.google.cloud.storage.TestUtils.retry429s;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.cloud.Condition;
import com.google.cloud.Identity;
import com.google.cloud.Policy;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Entity;
import com.google.cloud.storage.Acl.Project.ProjectRole;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.BucketInfo.IamConfiguration;
import com.google.cloud.storage.BucketInfo.PublicAccessPrevention;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.Storage.BucketField;
import com.google.cloud.storage.Storage.BucketGetOption;
import com.google.cloud.storage.Storage.BucketSourceOption;
import com.google.cloud.storage.Storage.BucketTargetOption;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageRoles;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.BucketFixture;
import com.google.cloud.storage.it.runner.annotations.BucketType;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.registry.Generator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@CrossRun(
    transports = {Transport.HTTP, Transport.GRPC},
    backends = {Backend.PROD})
public class ITAccessTest {

  private static final Long RETENTION_PERIOD = 5L;
  private static final Duration RETENTION_PERIOD_DURATION = Duration.ofSeconds(5);

  @Inject public Storage storage;

  @Inject
  @BucketFixture(BucketType.DEFAULT)
  public BucketInfo bucket;

  @Inject
  @BucketFixture(BucketType.REQUESTER_PAYS)
  public BucketInfo requesterPaysBucket;

  @Inject public Generator generator;

  @Test
  @CrossRun.Ignore(transports = Transport.GRPC)
  public void bucketAcl_requesterPays_true() {
    String projectId = storage.getOptions().getProjectId();
    testBucketAclRequesterPays(requesterPaysBucket, BucketSourceOption.userProject(projectId));
  }

  @Test
  @CrossRun.Ignore(transports = Transport.GRPC)
  public void bucketAcl_requesterPays_false() {
    testBucketAclRequesterPays(bucket);
  }

  private void testBucketAclRequesterPays(
      BucketInfo bucket, Storage.BucketSourceOption... bucketOptions) {
    // TODO: break into individual tests
    assertNull(storage.getAcl(bucket.getName(), User.ofAllAuthenticatedUsers(), bucketOptions));
    assertFalse(storage.deleteAcl(bucket.getName(), User.ofAllAuthenticatedUsers(), bucketOptions));
    Acl acl = Acl.of(User.ofAllAuthenticatedUsers(), Role.READER);
    assertNotNull(storage.createAcl(bucket.getName(), acl, bucketOptions));
    Acl updatedAcl =
        storage.updateAcl(
            bucket.getName(), acl.toBuilder().setRole(Role.WRITER).build(), bucketOptions);
    assertEquals(Role.WRITER, updatedAcl.getRole());
    Set<Acl> acls = new HashSet<>();
    acls.addAll(storage.listAcls(bucket.getName(), bucketOptions));
    assertTrue(acls.contains(updatedAcl));
    assertTrue(storage.deleteAcl(bucket.getName(), User.ofAllAuthenticatedUsers(), bucketOptions));
    assertNull(storage.getAcl(bucket.getName(), User.ofAllAuthenticatedUsers(), bucketOptions));
  }

  @Test
  public void bucket_defaultAcl_get() {
    String bucketName = bucket.getName();
    // lookup an entity from the bucket which is known to exist
    Bucket bucketWithAcls =
        storage.get(
            bucketName, BucketGetOption.fields(BucketField.ACL, BucketField.DEFAULT_OBJECT_ACL));

    Acl actual = bucketWithAcls.getDefaultAcl().iterator().next();

    Acl acl = retry429s(() -> storage.getDefaultAcl(bucketName, actual.getEntity()), storage);

    assertThat(acl).isEqualTo(actual);
  }

  /** When a bucket does exist, but an acl for the specified entity is not defined return null */
  @Test
  public void bucket_defaultAcl_get_notFoundReturnsNull() {
    Acl acl = retry429s(() -> storage.getDefaultAcl(bucket.getName(), User.ofAllUsers()), storage);

    assertThat(acl).isNull();
  }

  /** When a bucket doesn't exist, return null for the acl value */
  @Test
  public void bucket_defaultAcl_get_bucket404() {
    Acl acl =
        retry429s(() -> storage.getDefaultAcl(bucket.getName() + "x", User.ofAllUsers()), storage);

    assertThat(acl).isNull();
  }

  @Test
  public void bucket_defaultAcl_list() {
    String bucketName = bucket.getName();
    // lookup an entity from the bucket which is known to exist
    Bucket bucketWithAcls =
        storage.get(
            bucketName, BucketGetOption.fields(BucketField.ACL, BucketField.DEFAULT_OBJECT_ACL));

    Acl actual = bucketWithAcls.getDefaultAcl().iterator().next();

    List<Acl> acls = retry429s(() -> storage.listDefaultAcls(bucketName), storage);

    assertThat(acls).contains(actual);
  }

  @Test
  public void bucket_defaultAcl_list_bucket404() {
    StorageException storageException =
        assertThrows(
            StorageException.class,
            () -> retry429s(() -> storage.listDefaultAcls(bucket.getName() + "x"), storage));

    assertThat(storageException.getCode()).isEqualTo(404);
  }

  @Test
  public void bucket_defaultAcl_create() throws Exception {
    BucketInfo bucketInfo = BucketInfo.newBuilder(generator.randomBucketName()).build();
    try (TemporaryBucket tempB =
        TemporaryBucket.newBuilder().setBucketInfo(bucketInfo).setStorage(storage).build()) {
      BucketInfo bucket = tempB.getBucket();

      Acl readAll = Acl.of(User.ofAllAuthenticatedUsers(), Role.READER);
      Acl actual = retry429s(() -> storage.createDefaultAcl(bucket.getName(), readAll), storage);

      assertThat(actual.getEntity()).isEqualTo(readAll.getEntity());
      assertThat(actual.getRole()).isEqualTo(readAll.getRole());
      assertThat(actual.getEtag()).isNotEmpty();

      Bucket bucketUpdated =
          storage.get(bucket.getName(), BucketGetOption.fields(BucketField.values()));
      assertThat(bucketUpdated.getMetageneration()).isNotEqualTo(bucket.getMetageneration());

      // etags change when updates happen, drop before our comparison
      List<Acl> expectedAcls = dropEtags(bucket.getDefaultAcl());
      List<Acl> actualAcls = dropEtags(bucketUpdated.getDefaultAcl());
      assertThat(actualAcls).containsAtLeastElementsIn(expectedAcls);
      assertThat(actualAcls).contains(readAll);
    }
  }

  @Test
  public void bucket_defaultAcl_create_bucket404() {
    Acl readAll = Acl.of(User.ofAllAuthenticatedUsers(), Role.READER);
    StorageException storageException =
        assertThrows(
            StorageException.class,
            () ->
                retry429s(
                    () -> storage.createDefaultAcl(bucket.getName() + "x", readAll), storage));

    assertThat(storageException.getCode()).isEqualTo(404);
  }

  @Test
  public void bucket_defaultAcl_update() throws Exception {
    BucketInfo bucketInfo = BucketInfo.newBuilder(generator.randomBucketName()).build();
    try (TemporaryBucket tempB =
        TemporaryBucket.newBuilder().setBucketInfo(bucketInfo).setStorage(storage).build()) {
      BucketInfo bucket = tempB.getBucket();

      List<Acl> defaultAcls = bucket.getDefaultAcl();
      assertThat(defaultAcls).isNotEmpty();

      Predicate<Acl> isProjectEditor = hasProjectRole(ProjectRole.EDITORS);

      //noinspection OptionalGetWithoutIsPresent
      Acl projectEditorAsOwner =
          defaultAcls.stream().filter(hasRole(Role.OWNER).and(isProjectEditor)).findFirst().get();

      // lower the privileges of project editors to writer from owner
      Entity entity = projectEditorAsOwner.getEntity();
      Acl projectEditorAsReader = Acl.of(entity, Role.READER);

      Acl actual =
          retry429s(
              () -> storage.updateDefaultAcl(bucket.getName(), projectEditorAsReader), storage);

      assertThat(actual.getEntity()).isEqualTo(projectEditorAsReader.getEntity());
      assertThat(actual.getRole()).isEqualTo(projectEditorAsReader.getRole());
      assertThat(actual.getEtag()).isNotEmpty();

      Bucket bucketUpdated =
          storage.get(bucket.getName(), BucketGetOption.fields(BucketField.values()));
      assertThat(bucketUpdated.getMetageneration()).isNotEqualTo(bucket.getMetageneration());

      // etags change when updates happen, drop before our comparison
      List<Acl> expectedAcls =
          dropEtags(
              bucket.getDefaultAcl().stream()
                  .filter(isProjectEditor.negate())
                  .collect(Collectors.toList()));
      List<Acl> actualAcls = dropEtags(bucketUpdated.getDefaultAcl());
      assertThat(actualAcls).containsAtLeastElementsIn(expectedAcls);
      assertThat(actualAcls).doesNotContain(projectEditorAsOwner);
      assertThat(actualAcls).contains(projectEditorAsReader);
    }
  }

  @Test
  public void bucket_defaultAcl_update_bucket404() {
    Acl readAll = Acl.of(User.ofAllAuthenticatedUsers(), Role.READER);
    StorageException storageException =
        assertThrows(
            StorageException.class,
            () ->
                retry429s(
                    () -> storage.updateDefaultAcl(bucket.getName() + "x", readAll), storage));

    assertThat(storageException.getCode()).isEqualTo(404);
  }

  @Test
  public void bucket_defaultAcl_delete() throws Exception {
    BucketInfo bucketInfo = BucketInfo.newBuilder(generator.randomBucketName()).build();
    try (TemporaryBucket tempB =
        TemporaryBucket.newBuilder().setBucketInfo(bucketInfo).setStorage(storage).build()) {
      BucketInfo bucket = tempB.getBucket();

      List<Acl> defaultAcls = bucket.getDefaultAcl();
      assertThat(defaultAcls).isNotEmpty();

      Predicate<Acl> isProjectEditor = hasProjectRole(ProjectRole.VIEWERS);

      //noinspection OptionalGetWithoutIsPresent
      Acl projectViewerAsReader =
          defaultAcls.stream().filter(hasRole(Role.READER).and(isProjectEditor)).findFirst().get();

      Entity entity = projectViewerAsReader.getEntity();

      boolean actual = retry429s(() -> storage.deleteDefaultAcl(bucket.getName(), entity), storage);

      assertThat(actual).isTrue();

      Bucket bucketUpdated =
          storage.get(bucket.getName(), BucketGetOption.fields(BucketField.values()));
      assertThat(bucketUpdated.getMetageneration()).isNotEqualTo(bucket.getMetageneration());

      // etags change when deletes happen, drop before our comparison
      List<Acl> expectedAcls =
          dropEtags(
              bucket.getDefaultAcl().stream()
                  .filter(isProjectEditor.negate())
                  .collect(Collectors.toList()));
      List<Acl> actualAcls = dropEtags(bucketUpdated.getDefaultAcl());
      assertThat(actualAcls).containsAtLeastElementsIn(expectedAcls);
      Optional<Entity> search =
          actualAcls.stream().map(Acl::getEntity).filter(e -> e.equals(entity)).findAny();
      assertThat(search.isPresent()).isFalse();
    }
  }

  @Test
  public void bucket_defaultAcl_delete_bucket404() {
    boolean actual =
        retry429s(
            () -> storage.deleteDefaultAcl(bucket.getName() + "x", User.ofAllUsers()), storage);

    assertThat(actual).isEqualTo(false);
  }

  @Test
  public void bucket_defaultAcl_delete_noExistingAcl() throws Exception {
    BucketInfo bucketInfo = BucketInfo.newBuilder(generator.randomBucketName()).build();
    try (TemporaryBucket tempB =
        TemporaryBucket.newBuilder().setBucketInfo(bucketInfo).setStorage(storage).build()) {
      BucketInfo bucket = tempB.getBucket();
      boolean actual =
          retry429s(() -> storage.deleteDefaultAcl(bucket.getName(), User.ofAllUsers()), storage);

      assertThat(actual).isEqualTo(false);
    }
  }

  @Test
  @Ignore("Make hermetic, previously dependant on external transitive state")
  public void testBucketPolicyV1RequesterPays() {
    String projectId = storage.getOptions().getProjectId();

    Storage.BucketSourceOption[] bucketOptions =
        new Storage.BucketSourceOption[] {Storage.BucketSourceOption.userProject(projectId)};
    Identity projectOwner = Identity.projectOwner(projectId);
    Identity projectEditor = Identity.projectEditor(projectId);
    Identity projectViewer = Identity.projectViewer(projectId);
    Map<com.google.cloud.Role, Set<Identity>> bindingsWithoutPublicRead =
        ImmutableMap.of(
            StorageRoles.legacyBucketOwner(),
            ImmutableSet.of(projectOwner, projectEditor),
            StorageRoles.legacyBucketReader(),
            ImmutableSet.of(projectViewer));
    Map<com.google.cloud.Role, Set<Identity>> bindingsWithPublicRead =
        ImmutableMap.of(
            StorageRoles.legacyBucketOwner(),
            ImmutableSet.of(projectOwner, projectEditor),
            StorageRoles.legacyBucketReader(),
            ImmutableSet.of(projectViewer),
            StorageRoles.legacyObjectReader(),
            ImmutableSet.of(Identity.allUsers()));

    // Validate getting policy.
    Policy currentPolicy = storage.getIamPolicy(requesterPaysBucket.getName(), bucketOptions);
    assertEquals(bindingsWithoutPublicRead, currentPolicy.getBindings());

    // Validate updating policy.
    Policy updatedPolicy =
        storage.setIamPolicy(
            requesterPaysBucket.getName(),
            currentPolicy
                .toBuilder()
                .addIdentity(StorageRoles.legacyObjectReader(), Identity.allUsers())
                .build(),
            bucketOptions);
    assertEquals(bindingsWithPublicRead, updatedPolicy.getBindings());
    Policy revertedPolicy =
        storage.setIamPolicy(
            requesterPaysBucket.getName(),
            updatedPolicy
                .toBuilder()
                .removeIdentity(StorageRoles.legacyObjectReader(), Identity.allUsers())
                .build(),
            bucketOptions);
    assertEquals(bindingsWithoutPublicRead, revertedPolicy.getBindings());

    // Validate testing permissions.
    List<Boolean> expectedPermissions = ImmutableList.of(true, true);
    assertEquals(
        expectedPermissions,
        storage.testIamPermissions(
            requesterPaysBucket.getName(),
            ImmutableList.of("storage.buckets.getIamPolicy", "storage.buckets.setIamPolicy"),
            bucketOptions));
  }

  @Test
  @Ignore("Make hermetic, previously dependant on external transitive state")
  public void testBucketPolicyV1() {
    String projectId = storage.getOptions().getProjectId();

    Storage.BucketSourceOption[] bucketOptions = new Storage.BucketSourceOption[] {};
    Identity projectOwner = Identity.projectOwner(projectId);
    Identity projectEditor = Identity.projectEditor(projectId);
    Identity projectViewer = Identity.projectViewer(projectId);
    Map<com.google.cloud.Role, Set<Identity>> bindingsWithoutPublicRead =
        ImmutableMap.of(
            StorageRoles.legacyBucketOwner(),
            ImmutableSet.of(projectOwner, projectEditor),
            StorageRoles.legacyBucketReader(),
            ImmutableSet.of(projectViewer));
    Map<com.google.cloud.Role, Set<Identity>> bindingsWithPublicRead =
        ImmutableMap.of(
            StorageRoles.legacyBucketOwner(),
            ImmutableSet.of(projectOwner, projectEditor),
            StorageRoles.legacyBucketReader(),
            ImmutableSet.of(projectViewer),
            StorageRoles.legacyObjectReader(),
            ImmutableSet.of(Identity.allUsers()));

    // Validate getting policy.
    Policy currentPolicy = storage.getIamPolicy(bucket.getName(), bucketOptions);
    assertEquals(bindingsWithoutPublicRead, currentPolicy.getBindings());

    // Validate updating policy.
    Policy updatedPolicy =
        storage.setIamPolicy(
            bucket.getName(),
            currentPolicy
                .toBuilder()
                .addIdentity(StorageRoles.legacyObjectReader(), Identity.allUsers())
                .build(),
            bucketOptions);
    assertEquals(bindingsWithPublicRead, updatedPolicy.getBindings());
    Policy revertedPolicy =
        storage.setIamPolicy(
            bucket.getName(),
            updatedPolicy
                .toBuilder()
                .removeIdentity(StorageRoles.legacyObjectReader(), Identity.allUsers())
                .build(),
            bucketOptions);
    assertEquals(bindingsWithoutPublicRead, revertedPolicy.getBindings());

    // Validate testing permissions.
    List<Boolean> expectedPermissions = ImmutableList.of(true, true);
    assertEquals(
        expectedPermissions,
        storage.testIamPermissions(
            bucket.getName(),
            ImmutableList.of("storage.buckets.getIamPolicy", "storage.buckets.setIamPolicy"),
            bucketOptions));
  }

  @Test
  @Ignore("Make hermetic, previously dependant on external transitive state")
  public void testBucketPolicyV3() throws Exception {
    String projectId = storage.getOptions().getProjectId();
    BucketInfo bucketInfo =
        BucketInfo.newBuilder(generator.randomBucketName())
            .setIamConfiguration(
                BucketInfo.IamConfiguration.newBuilder()
                    .setIsUniformBucketLevelAccessEnabled(true)
                    .build())
            .build();
    Storage.BucketSourceOption[] bucketOptions =
        new Storage.BucketSourceOption[] {Storage.BucketSourceOption.requestedPolicyVersion(3)};
    Identity projectOwner = Identity.projectOwner(projectId);
    Identity projectEditor = Identity.projectEditor(projectId);
    Identity projectViewer = Identity.projectViewer(projectId);
    List<com.google.cloud.Binding> bindingsWithoutPublicRead =
        ImmutableList.of(
            com.google.cloud.Binding.newBuilder()
                .setRole(StorageRoles.legacyBucketOwner().toString())
                .setMembers(ImmutableList.of(projectEditor.strValue(), projectOwner.strValue()))
                .build(),
            com.google.cloud.Binding.newBuilder()
                .setRole(StorageRoles.legacyBucketReader().toString())
                .setMembers(ImmutableList.of(projectViewer.strValue()))
                .build());
    List<com.google.cloud.Binding> bindingsWithPublicRead =
        ImmutableList.of(
            com.google.cloud.Binding.newBuilder()
                .setRole(StorageRoles.legacyBucketReader().toString())
                .setMembers(ImmutableList.of(projectViewer.strValue()))
                .build(),
            com.google.cloud.Binding.newBuilder()
                .setRole(StorageRoles.legacyBucketOwner().toString())
                .setMembers(ImmutableList.of(projectEditor.strValue(), projectOwner.strValue()))
                .build(),
            com.google.cloud.Binding.newBuilder()
                .setRole(StorageRoles.legacyObjectReader().toString())
                .setMembers(ImmutableList.of("allUsers"))
                .build());

    List<com.google.cloud.Binding> bindingsWithConditionalPolicy =
        ImmutableList.of(
            com.google.cloud.Binding.newBuilder()
                .setRole(StorageRoles.legacyBucketReader().toString())
                .setMembers(ImmutableList.of(projectViewer.strValue()))
                .build(),
            com.google.cloud.Binding.newBuilder()
                .setRole(StorageRoles.legacyBucketOwner().toString())
                .setMembers(ImmutableList.of(projectEditor.strValue(), projectOwner.strValue()))
                .build(),
            com.google.cloud.Binding.newBuilder()
                .setRole(StorageRoles.legacyObjectReader().toString())
                .setMembers(
                    ImmutableList.of(
                        "serviceAccount:storage-python@spec-test-ruby-samples.iam.gserviceaccount.com"))
                .setCondition(
                    Condition.newBuilder()
                        .setTitle("Title")
                        .setDescription("Description")
                        .setExpression(
                            "resource.name.startsWith(\"projects/_/buckets/bucket-name/objects/prefix-a-\")")
                        .build())
                .build());

    try (TemporaryBucket tempB =
        TemporaryBucket.newBuilder().setBucketInfo(bucketInfo).setStorage(storage).build()) {
      BucketInfo bucket = tempB.getBucket();

      // Validate getting policy.
      Policy currentPolicy = storage.getIamPolicy(bucket.getName(), bucketOptions);
      Collector<CharSequence, ?, String> joining = Collectors.joining(",\n\t", "[\n\t", "\n]");
      String s = currentPolicy.getBindingsList().stream().map(Object::toString).collect(joining);
      String ss = bindingsWithoutPublicRead.stream().map(Object::toString).collect(joining);
      assertThat(s).isEqualTo(ss);
      // assertEquals(bindingsWithoutPublicRead, currentPolicy.getBindingsList());

      // Validate updating policy.
      List<com.google.cloud.Binding> currentBindings =
          new ArrayList(currentPolicy.getBindingsList());
      currentBindings.add(
          com.google.cloud.Binding.newBuilder()
              .setRole(StorageRoles.legacyObjectReader().getValue())
              .addMembers(Identity.allUsers().strValue())
              .build());
      Policy updatedPolicy =
          storage.setIamPolicy(
              bucket.getName(),
              currentPolicy.toBuilder().setBindings(currentBindings).build(),
              bucketOptions);
      assertTrue(
          bindingsWithPublicRead.size() == updatedPolicy.getBindingsList().size()
              && bindingsWithPublicRead.containsAll(updatedPolicy.getBindingsList()));

      // Remove a member
      List<com.google.cloud.Binding> updatedBindings =
          new ArrayList(updatedPolicy.getBindingsList());
      for (int i = 0; i < updatedBindings.size(); i++) {
        com.google.cloud.Binding binding = updatedBindings.get(i);
        if (binding.getRole().equals(StorageRoles.legacyObjectReader().toString())) {
          List<String> members = new ArrayList(binding.getMembers());
          members.remove(Identity.allUsers().strValue());
          updatedBindings.set(i, binding.toBuilder().setMembers(members).build());
          break;
        }
      }

      Policy revertedPolicy =
          storage.setIamPolicy(
              bucket.getName(),
              updatedPolicy.toBuilder().setBindings(updatedBindings).build(),
              bucketOptions);

      assertEquals(bindingsWithoutPublicRead, revertedPolicy.getBindingsList());
      assertTrue(
          bindingsWithoutPublicRead.size() == revertedPolicy.getBindingsList().size()
              && bindingsWithoutPublicRead.containsAll(revertedPolicy.getBindingsList()));

      // Add Conditional Policy
      List<com.google.cloud.Binding> conditionalBindings =
          new ArrayList(revertedPolicy.getBindingsList());
      conditionalBindings.add(
          com.google.cloud.Binding.newBuilder()
              .setRole(StorageRoles.legacyObjectReader().toString())
              .addMembers(
                  "serviceAccount:storage-python@spec-test-ruby-samples.iam.gserviceaccount.com")
              .setCondition(
                  Condition.newBuilder()
                      .setTitle("Title")
                      .setDescription("Description")
                      .setExpression(
                          "resource.name.startsWith(\"projects/_/buckets/bucket-name/objects/prefix-a-\")")
                      .build())
              .build());
      Policy conditionalPolicy =
          storage.setIamPolicy(
              bucket.getName(),
              revertedPolicy.toBuilder().setBindings(conditionalBindings).setVersion(3).build(),
              bucketOptions);
      assertTrue(
          bindingsWithConditionalPolicy.size() == conditionalPolicy.getBindingsList().size()
              && bindingsWithConditionalPolicy.containsAll(conditionalPolicy.getBindingsList()));

      // Remove Conditional Policy
      conditionalPolicy =
          storage.setIamPolicy(
              bucket.getName(),
              conditionalPolicy.toBuilder().setBindings(updatedBindings).setVersion(3).build(),
              bucketOptions);

      // Validate testing permissions.
      List<Boolean> expectedPermissions = ImmutableList.of(true, true);
      assertEquals(
          expectedPermissions,
          storage.testIamPermissions(
              bucket.getName(),
              ImmutableList.of("storage.buckets.getIamPolicy", "storage.buckets.setIamPolicy"),
              bucketOptions));
    }
  }

  @Test
  @SuppressWarnings({"unchecked", "deprecation"})
  @CrossRun.Ignore(transports = Transport.GRPC)
  public void testBucketWithBucketPolicyOnlyEnabled() throws Exception {
    // TODO: break this test up into each of the respective scenarios
    //   1. Create bucket with BucketPolicyOnly enabled
    //   2. Get bucket with BucketPolicyOnly enabled
    //   3. Expect failure when attempting to list ACLs for BucketPolicyOnly bucket
    //   4. Expect failure when attempting to list default ACLs for BucketPolicyOnly bucket

    // TODO: temp bucket
    String randBucketName = generator.randomBucketName();
    try {
      storage.create(
          Bucket.newBuilder(randBucketName)
              .setIamConfiguration(
                  BucketInfo.IamConfiguration.newBuilder()
                      .setIsBucketPolicyOnlyEnabled(true)
                      .build())
              .build());

      Bucket remoteBucket =
          storage.get(randBucketName, Storage.BucketGetOption.fields(BucketField.IAMCONFIGURATION));

      assertTrue(remoteBucket.getIamConfiguration().isBucketPolicyOnlyEnabled());
      assertNotNull(remoteBucket.getIamConfiguration().getBucketPolicyOnlyLockedTime());

      try {
        remoteBucket.listAcls();
        fail("StorageException was expected.");
      } catch (StorageException e) {
        // Expected: Listing legacy ACLs should fail on a BPO enabled bucket
      }
      try {
        remoteBucket.listDefaultAcls();
        fail("StorageException was expected");
      } catch (StorageException e) {
        // Expected: Listing legacy ACLs should fail on a BPO enabled bucket
      }
    } finally {
      BucketCleaner.doCleanup(randBucketName, storage);
    }
  }

  @Test
  @CrossRun.Ignore(transports = Transport.GRPC)
  public void testBucketWithUniformBucketLevelAccessEnabled() throws Exception {
    // TODO: break this test up into each of the respective scenarios
    //   1. Create bucket with UniformBucketLevelAccess enabled
    //   2. Get bucket with UniformBucketLevelAccess enabled
    //   3. Expect failure when attempting to list ACLs for UniformBucketLevelAccess bucket
    //   4. Expect failure when attempting to list default ACLs for UniformBucketLevelAccess bucket

    // TODO: temp bucket
    String randBucketName = generator.randomBucketName();
    try {
      storage.create(
          Bucket.newBuilder(randBucketName)
              .setIamConfiguration(
                  BucketInfo.IamConfiguration.newBuilder()
                      .setIsUniformBucketLevelAccessEnabled(true)
                      .build())
              .build());

      Bucket remoteBucket =
          storage.get(randBucketName, Storage.BucketGetOption.fields(BucketField.IAMCONFIGURATION));

      assertTrue(remoteBucket.getIamConfiguration().isUniformBucketLevelAccessEnabled());
      assertNotNull(remoteBucket.getIamConfiguration().getUniformBucketLevelAccessLockedTime());
      try {
        remoteBucket.listAcls();
        fail("StorageException was expected.");
      } catch (StorageException e) {
        // Expected: Listing legacy ACLs should fail on a BPO enabled bucket
      }
      try {
        remoteBucket.listDefaultAcls();
        fail("StorageException was expected");
      } catch (StorageException e) {
        // Expected: Listing legacy ACLs should fail on a BPO enabled bucket
      }
    } finally {
      BucketCleaner.doCleanup(randBucketName, storage);
    }
  }

  @Test
  public void testEnableAndDisableUniformBucketLevelAccessOnExistingBucket() throws Exception {
    String bpoBucket = generator.randomBucketName();
    BucketInfo.IamConfiguration ublaDisabledIamConfiguration =
        BucketInfo.IamConfiguration.newBuilder()
            .setIsUniformBucketLevelAccessEnabled(false)
            .build();
    BucketInfo bucketInfo =
        Bucket.newBuilder(bpoBucket)
            .setIamConfiguration(ublaDisabledIamConfiguration)
            .setAcl(ImmutableList.of(Acl.of(User.ofAllAuthenticatedUsers(), Role.READER)))
            .setDefaultAcl(ImmutableList.of(Acl.of(User.ofAllAuthenticatedUsers(), Role.READER)))
            .build();
    try (TemporaryBucket tempB =
        TemporaryBucket.newBuilder().setBucketInfo(bucketInfo).setStorage(storage).build()) {
      // BPO is disabled by default.
      BucketInfo bucket = tempB.getBucket();
      assertThat(bucket.getIamConfiguration().isUniformBucketLevelAccessEnabled()).isFalse();

      storage.update(
          bucket
              .toBuilder()
              .setAcl(null)
              .setDefaultAcl(null)
              .setIamConfiguration(
                  ublaDisabledIamConfiguration
                      .toBuilder()
                      .setIsUniformBucketLevelAccessEnabled(true)
                      .build())
              .build(),
          BucketTargetOption.metagenerationMatch());

      Bucket remoteBucket =
          storage.get(bpoBucket, Storage.BucketGetOption.fields(BucketField.IAMCONFIGURATION));

      assertTrue(remoteBucket.getIamConfiguration().isUniformBucketLevelAccessEnabled());
      assertNotNull(remoteBucket.getIamConfiguration().getUniformBucketLevelAccessLockedTime());

      remoteBucket.toBuilder().setIamConfiguration(ublaDisabledIamConfiguration).build().update();

      remoteBucket =
          storage.get(
              bpoBucket,
              Storage.BucketGetOption.fields(
                  BucketField.IAMCONFIGURATION, BucketField.ACL, BucketField.DEFAULT_OBJECT_ACL));

      assertFalse(remoteBucket.getIamConfiguration().isUniformBucketLevelAccessEnabled());
      assertEquals(User.ofAllAuthenticatedUsers(), remoteBucket.getDefaultAcl().get(0).getEntity());
      assertEquals(Role.READER, remoteBucket.getDefaultAcl().get(0).getRole());
      assertEquals(User.ofAllAuthenticatedUsers(), remoteBucket.getAcl().get(0).getEntity());
      assertEquals(Role.READER, remoteBucket.getAcl().get(0).getRole());
    }
  }

  @Test
  public void testEnforcedPublicAccessPreventionOnBucket() throws Exception {
    String papBucket = generator.randomBucketName();
    BucketInfo bucketInfo =
        BucketInfo.newBuilder(papBucket)
            .setIamConfiguration(
                IamConfiguration.newBuilder()
                    .setPublicAccessPrevention(PublicAccessPrevention.ENFORCED)
                    .build())
            .build();

    try (TemporaryBucket tempB =
        TemporaryBucket.newBuilder().setBucketInfo(bucketInfo).setStorage(storage).build()) {
      BucketInfo bucket = tempB.getBucket();
      // Making bucket public should fail.
      try {
        storage.setIamPolicy(
            papBucket,
            Policy.newBuilder()
                .setVersion(3)
                .setBindings(
                    ImmutableList.of(
                        com.google.cloud.Binding.newBuilder()
                            .setRole("roles/storage.objectViewer")
                            .addMembers("allUsers")
                            .build()))
                .build());
        fail("pap: expected adding allUsers policy to bucket should fail");
      } catch (StorageException storageException) {
        // Creating a bucket with roles/storage.objectViewer is not
        // allowed when publicAccessPrevention is enabled.
        assertEquals(storageException.getCode(), 412);
      }

      // Making object public via ACL should fail.
      try {
        // Create a public object
        storage.create(
            BlobInfo.newBuilder(bucket, "pap-test-object").build(),
            BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ));
        fail("pap: expected adding allUsers ACL to object should fail");
      } catch (StorageException storageException) {
        // Creating an object with allUsers roles/storage.viewer permission
        // is not allowed. When Public Access Prevention is enabled.
        assertEquals(storageException.getCode(), 412);
      }
    }
  }

  @Test
  public void testUnspecifiedPublicAccessPreventionOnBucket() throws Exception {
    String papBucket = generator.randomBucketName();
    BucketInfo bucketInfo =
        BucketInfo.newBuilder(papBucket)
            .setIamConfiguration(
                IamConfiguration.newBuilder()
                    .setPublicAccessPrevention(PublicAccessPrevention.INHERITED)
                    .build())
            .build();

    try (TemporaryBucket tempB =
        TemporaryBucket.newBuilder().setBucketInfo(bucketInfo).setStorage(storage).build()) {
      BucketInfo bucket = tempB.getBucket();

      // Now, making object public or making bucket public should succeed.
      try {
        // Create a public object
        storage.create(
            BlobInfo.newBuilder(bucket, "pap-test-object").build(),
            BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ));
      } catch (StorageException storageException) {
        fail("pap: expected adding allUsers ACL to object to succeed");
      }

      // Now, making bucket public should succeed.
      try {
        storage.setIamPolicy(
            papBucket,
            Policy.newBuilder()
                .setVersion(3)
                .setBindings(
                    ImmutableList.of(
                        com.google.cloud.Binding.newBuilder()
                            .setRole("roles/storage.objectViewer")
                            .addMembers("allUsers")
                            .build()))
                .build());
      } catch (StorageException storageException) {
        fail("pap: expected adding allUsers policy to bucket to succeed");
      }
    }
  }

  @Test
  public void changingPAPDoesNotAffectUBLA() throws Exception {
    String bucketName = generator.randomBucketName();
    try (TemporaryBucket tempB =
        TemporaryBucket.newBuilder()
            .setBucketInfo(
                BucketInfo.newBuilder(bucketName)
                    .setIamConfiguration(
                        BucketInfo.IamConfiguration.newBuilder()
                            .setPublicAccessPrevention(PublicAccessPrevention.INHERITED)
                            .setIsUniformBucketLevelAccessEnabled(false)
                            .build())
                    .build())
            .setStorage(storage)
            .build()) {
      BucketInfo bucket = tempB.getBucket();
      assertEquals(
          bucket.getIamConfiguration().getPublicAccessPrevention(),
          BucketInfo.PublicAccessPrevention.INHERITED);
      assertFalse(bucket.getIamConfiguration().isUniformBucketLevelAccessEnabled());
      assertFalse(bucket.getIamConfiguration().isBucketPolicyOnlyEnabled());

      IamConfiguration iamConfiguration1 =
          bucket
              .getIamConfiguration()
              .toBuilder()
              .setPublicAccessPrevention(PublicAccessPrevention.ENFORCED)
              .build();
      // Update PAP setting to ENFORCED and should not affect UBLA setting.
      storage.update(
          bucket.toBuilder().setIamConfiguration(iamConfiguration1).build(),
          BucketTargetOption.metagenerationMatch());
      Bucket bucket2 =
          storage.get(bucketName, Storage.BucketGetOption.fields(BucketField.IAMCONFIGURATION));
      assertEquals(
          bucket2.getIamConfiguration().getPublicAccessPrevention(),
          BucketInfo.PublicAccessPrevention.ENFORCED);
      assertFalse(bucket2.getIamConfiguration().isUniformBucketLevelAccessEnabled());
      assertFalse(bucket2.getIamConfiguration().isBucketPolicyOnlyEnabled());
    }
  }

  @Test
  public void changingUBLADoesNotAffectPAP() throws Exception {
    String bucketName = generator.randomBucketName();
    try (TemporaryBucket tempB =
        TemporaryBucket.newBuilder()
            .setBucketInfo(
                BucketInfo.newBuilder(bucketName)
                    .setIamConfiguration(
                        BucketInfo.IamConfiguration.newBuilder()
                            .setPublicAccessPrevention(PublicAccessPrevention.INHERITED)
                            .setIsUniformBucketLevelAccessEnabled(false)
                            .build())
                    .build())
            .setStorage(storage)
            .build()) {
      BucketInfo bucket = tempB.getBucket();
      assertEquals(
          bucket.getIamConfiguration().getPublicAccessPrevention(),
          PublicAccessPrevention.INHERITED);
      assertFalse(bucket.getIamConfiguration().isUniformBucketLevelAccessEnabled());
      assertFalse(bucket.getIamConfiguration().isBucketPolicyOnlyEnabled());

      IamConfiguration iamConfiguration1 =
          bucket
              .getIamConfiguration()
              .toBuilder()
              .setIsUniformBucketLevelAccessEnabled(true)
              .build();
      // Updating UBLA should not affect PAP setting.
      Bucket bucket2 =
          storage.update(
              bucket
                  .toBuilder()
                  .setIamConfiguration(iamConfiguration1)
                  // clear out ACL related config in conjunction with enabling UBLA
                  .setAcl(Collections.emptyList())
                  .setDefaultAcl(Collections.emptyList())
                  .build(),
              BucketTargetOption.metagenerationMatch());
      assertEquals(
          bucket2.getIamConfiguration().getPublicAccessPrevention(),
          PublicAccessPrevention.INHERITED);
      assertTrue(bucket2.getIamConfiguration().isUniformBucketLevelAccessEnabled());
      assertTrue(bucket2.getIamConfiguration().isBucketPolicyOnlyEnabled());
    }
  }

  @Test
  public void testListBucketRequesterPaysFails() throws InterruptedException {
    String projectId = storage.getOptions().getProjectId();
    Iterator<Bucket> bucketIterator =
        storage
            .list(
                Storage.BucketListOption.prefix(bucket.getName()),
                Storage.BucketListOption.fields(),
                Storage.BucketListOption.userProject(projectId))
            .iterateAll()
            .iterator();
    while (!bucketIterator.hasNext()) {
      Thread.sleep(500);
      bucketIterator =
          storage
              .list(
                  Storage.BucketListOption.prefix(bucket.getName()),
                  Storage.BucketListOption.fields())
              .iterateAll()
              .iterator();
    }
    while (bucketIterator.hasNext()) {
      Bucket remoteBucket = bucketIterator.next();
      assertTrue(remoteBucket.getName().startsWith(bucket.getName()));
      assertNull(remoteBucket.getCreateTime());
      assertNull(remoteBucket.getSelfLink());
    }
  }

  @Test
  public void testRetentionPolicyNoLock() throws Exception {
    String bucketName = generator.randomBucketName();
    try (TemporaryBucket tempB =
        TemporaryBucket.newBuilder()
            .setBucketInfo(
                BucketInfo.newBuilder(bucketName).setRetentionPeriod(RETENTION_PERIOD).build())
            .setStorage(storage)
            .build()) {
      BucketInfo remoteBucket = tempB.getBucket();

      assertThat(remoteBucket.getRetentionPeriod()).isEqualTo(RETENTION_PERIOD);
      assertThat(remoteBucket.getRetentionPeriodDuration()).isEqualTo(RETENTION_PERIOD_DURATION);
      assertNotNull(remoteBucket.getRetentionEffectiveTime());
      assertThat(remoteBucket.retentionPolicyIsLocked()).isAnyOf(null, false);

      Bucket remoteBucket2 =
          storage.get(bucketName, Storage.BucketGetOption.fields(BucketField.RETENTION_POLICY));
      assertEquals(RETENTION_PERIOD, remoteBucket2.getRetentionPeriod());
      assertThat(remoteBucket2.getRetentionPeriodDuration()).isEqualTo(RETENTION_PERIOD_DURATION);
      assertNotNull(remoteBucket2.getRetentionEffectiveTime());
      assertThat(remoteBucket2.retentionPolicyIsLocked()).isAnyOf(null, false);

      String blobName = "test-create-with-retention-policy-hold";
      BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blobName).build();
      Blob remoteBlob = storage.create(blobInfo);
      assertNotNull(remoteBlob.getRetentionExpirationTime());

      Bucket remoteBucket3 = remoteBucket2.toBuilder().setRetentionPeriod(null).build().update();
      assertNull(remoteBucket3.getRetentionPeriod());
    }
  }

  @Test
  @SuppressWarnings({"unchecked", "deprecation"})
  public void testEnableAndDisableBucketPolicyOnlyOnExistingBucket() throws Exception {
    String bpoBucket = generator.randomBucketName();
    try (TemporaryBucket tempB =
        TemporaryBucket.newBuilder()
            .setBucketInfo(
                Bucket.newBuilder(bpoBucket)
                    .setAcl(ImmutableList.of(Acl.of(User.ofAllAuthenticatedUsers(), Role.READER)))
                    .setDefaultAcl(
                        ImmutableList.of(Acl.of(User.ofAllAuthenticatedUsers(), Role.READER)))
                    .build())
            .setStorage(storage)
            .build()) {
      // BPO is disabled by default.
      BucketInfo bucket = tempB.getBucket();
      assertThat(bucket.getIamConfiguration().isBucketPolicyOnlyEnabled()).isFalse();

      BucketInfo.IamConfiguration bpoEnabledIamConfiguration =
          BucketInfo.IamConfiguration.newBuilder().setIsBucketPolicyOnlyEnabled(true).build();
      storage.update(
          bucket
              .toBuilder()
              .setAcl(null)
              .setDefaultAcl(null)
              .setIamConfiguration(bpoEnabledIamConfiguration)
              .build(),
          BucketTargetOption.metagenerationMatch());

      Bucket remoteBucket = storage.get(bpoBucket);

      assertTrue(remoteBucket.getIamConfiguration().isBucketPolicyOnlyEnabled());
      assertNotNull(remoteBucket.getIamConfiguration().getBucketPolicyOnlyLockedTime());

      remoteBucket
          .toBuilder()
          .setIamConfiguration(
              bpoEnabledIamConfiguration.toBuilder().setIsBucketPolicyOnlyEnabled(false).build())
          .build()
          .update();

      remoteBucket =
          storage.get(
              bpoBucket,
              Storage.BucketGetOption.fields(
                  BucketField.IAMCONFIGURATION, BucketField.ACL, BucketField.DEFAULT_OBJECT_ACL));

      assertFalse(remoteBucket.getIamConfiguration().isBucketPolicyOnlyEnabled());
      assertEquals(User.ofAllAuthenticatedUsers(), remoteBucket.getDefaultAcl().get(0).getEntity());
      assertEquals(Role.READER, remoteBucket.getDefaultAcl().get(0).getRole());
      assertEquals(User.ofAllAuthenticatedUsers(), remoteBucket.getAcl().get(0).getEntity());
      assertEquals(Role.READER, remoteBucket.getAcl().get(0).getRole());
    }
  }

  @Test
  @CrossRun.Ignore(transports = Transport.GRPC)
  public void testBlobAcl() {
    // TODO: break this test up into each of the respective scenarios
    //   1. get ACL for specific entity
    //   2. Create an ACL for specific entity
    //   3. Update ACL to change role of a specific entity
    //   4. List ACLs for an object
    //   5. Delete an ACL for a specific entity
    //   6. Attempt to get an acl for an object that doesn't exist
    //   7. Attempt to delete an acl for an object that doesn't exist
    //   8. Attempt to create an acl for an object that doesn't exist
    //   9. Attempt to update an acl for an object that doesn't exist
    //   10. Attempt to list acls for an object that doesn't exist
    BlobId blobId = BlobId.of(bucket.getName(), "test-blob-acl");
    BlobInfo blob = BlobInfo.newBuilder(blobId).build();
    storage.create(blob);
    assertNull(storage.getAcl(blobId, User.ofAllAuthenticatedUsers()));
    Acl acl = Acl.of(User.ofAllAuthenticatedUsers(), Role.READER);
    assertNotNull(storage.createAcl(blobId, acl));
    Acl updatedAcl = storage.updateAcl(blobId, acl.toBuilder().setRole(Role.OWNER).build());
    assertEquals(Role.OWNER, updatedAcl.getRole());
    Set<Acl> acls = new HashSet<>(storage.listAcls(blobId));
    assertTrue(acls.contains(updatedAcl));
    assertTrue(storage.deleteAcl(blobId, User.ofAllAuthenticatedUsers()));
    assertNull(storage.getAcl(blobId, User.ofAllAuthenticatedUsers()));
    // test non-existing blob
    BlobId otherBlobId = BlobId.of(bucket.getName(), "test-blob-acl", -1L);
    try {
      assertNull(storage.getAcl(otherBlobId, User.ofAllAuthenticatedUsers()));
      fail("Expected an 'Invalid argument' exception");
    } catch (StorageException e) {
      assertThat(e.getMessage()).contains("Invalid argument");
    }

    try {
      assertFalse(storage.deleteAcl(otherBlobId, User.ofAllAuthenticatedUsers()));
      fail("Expected an 'Invalid argument' exception");
    } catch (StorageException e) {
      assertThat(e.getMessage()).contains("Invalid argument");
    }

    try {
      storage.createAcl(otherBlobId, acl);
      fail("Expected StorageException");
    } catch (StorageException ex) {
      // expected
    }
    try {
      storage.updateAcl(otherBlobId, acl);
      fail("Expected StorageException");
    } catch (StorageException ex) {
      // expected
    }
    try {
      storage.listAcls(otherBlobId);
      fail("Expected StorageException");
    } catch (StorageException ex) {
      // expected
    }
  }

  static ImmutableList<Acl> dropEtags(List<Acl> defaultAcls) {
    return defaultAcls.stream()
        .map(acl -> Acl.of(acl.getEntity(), acl.getRole()))
        .collect(ImmutableList.toImmutableList());
  }

  static Predicate<Acl> hasRole(Acl.Role expected) {
    return acl -> acl.getRole().equals(expected);
  }

  static Predicate<Acl> hasProjectRole(Acl.Project.ProjectRole expected) {
    return acl -> {
      Entity entity = acl.getEntity();
      if (entity.getType().equals(Entity.Type.PROJECT)) {
        return ((Acl.Project) entity).getProjectRole().equals(expected);
      }
      return false;
    };
  }
}
