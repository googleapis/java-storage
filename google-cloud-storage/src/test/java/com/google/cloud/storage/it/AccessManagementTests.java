package com.google.cloud.storage.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.cloud.Condition;
import com.google.cloud.Identity;
import com.google.cloud.Policy;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BucketField;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageRoles;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class AccessManagementTests {
  @Test
  public void testBucketAcl() {
    unsetRequesterPays();
    testBucketAclRequesterPays(true);
    testBucketAclRequesterPays(false);
  }

  private void testBucketAclRequesterPays(boolean requesterPays) {
    if (requesterPays) {
      Bucket remoteBucket =
          storage.get(
              BUCKET_REQUESTER_PAYS,
              Storage.BucketGetOption.fields(BucketField.ID, BucketField.BILLING));
      assertTrue(remoteBucket.requesterPays() == null || !remoteBucket.requesterPays());
      remoteBucket = remoteBucket.toBuilder().setRequesterPays(true).build();
      Bucket updatedBucket = storage.update(remoteBucket);
      assertTrue(updatedBucket.requesterPays());
    }

    String projectId = storage.getOptions().getProjectId();

    Storage.BucketSourceOption[] bucketOptions =
        requesterPays
            ? new Storage.BucketSourceOption[] {Storage.BucketSourceOption.userProject(projectId)}
            : new Storage.BucketSourceOption[] {};

    assertNull(
        storage.getAcl(BUCKET_REQUESTER_PAYS, User.ofAllAuthenticatedUsers(), bucketOptions));
    assertFalse(
        storage.deleteAcl(BUCKET_REQUESTER_PAYS, User.ofAllAuthenticatedUsers(), bucketOptions));
    Acl acl = Acl.of(User.ofAllAuthenticatedUsers(), Role.READER);
    assertNotNull(storage.createAcl(BUCKET_REQUESTER_PAYS, acl, bucketOptions));
    Acl updatedAcl =
        storage.updateAcl(
            BUCKET_REQUESTER_PAYS, acl.toBuilder().setRole(Role.WRITER).build(), bucketOptions);
    assertEquals(Role.WRITER, updatedAcl.getRole());
    Set<Acl> acls = new HashSet<>();
    acls.addAll(storage.listAcls(BUCKET_REQUESTER_PAYS, bucketOptions));
    assertTrue(acls.contains(updatedAcl));
    assertTrue(
        storage.deleteAcl(BUCKET_REQUESTER_PAYS, User.ofAllAuthenticatedUsers(), bucketOptions));
    assertNull(
        storage.getAcl(BUCKET_REQUESTER_PAYS, User.ofAllAuthenticatedUsers(), bucketOptions));
    if (requesterPays) {
      Bucket remoteBucket =
          storage.get(
              BUCKET_REQUESTER_PAYS,
              Storage.BucketGetOption.fields(BucketField.ID, BucketField.BILLING),
              Storage.BucketGetOption.userProject(projectId));
      assertTrue(remoteBucket.requesterPays());
      remoteBucket = remoteBucket.toBuilder().setRequesterPays(false).build();
      Bucket updatedBucket =
          storage.update(remoteBucket, Storage.BucketTargetOption.userProject(projectId));
      assertFalse(updatedBucket.requesterPays());
    }
  }

  @Test
  public void testBucketDefaultAcl() {
    assertNull(storage.getDefaultAcl(BUCKET, User.ofAllAuthenticatedUsers()));
    assertFalse(storage.deleteDefaultAcl(BUCKET, User.ofAllAuthenticatedUsers()));
    Acl acl = Acl.of(User.ofAllAuthenticatedUsers(), Role.READER);
    assertNotNull(storage.createDefaultAcl(BUCKET, acl));
    Acl updatedAcl = storage.updateDefaultAcl(BUCKET, acl.toBuilder().setRole(Role.OWNER).build());
    assertEquals(Role.OWNER, updatedAcl.getRole());
    Set<Acl> acls = new HashSet<>();
    acls.addAll(storage.listDefaultAcls(BUCKET));
    assertTrue(acls.contains(updatedAcl));
    assertTrue(storage.deleteDefaultAcl(BUCKET, User.ofAllAuthenticatedUsers()));
    assertNull(storage.getDefaultAcl(BUCKET, User.ofAllAuthenticatedUsers()));
  }

  @Test
  public void testBucketPolicyV1RequesterPays() throws ExecutionException, InterruptedException {
    unsetRequesterPays();
    Bucket bucketDefault =
        storage.get(
            BUCKET_REQUESTER_PAYS,
            Storage.BucketGetOption.fields(BucketField.ID, BucketField.BILLING));
    assertTrue(bucketDefault.requesterPays() == null || !bucketDefault.requesterPays());

    Bucket bucketTrue = storage.update(bucketDefault.toBuilder().setRequesterPays(true).build());
    assertTrue(bucketTrue.requesterPays());

    String projectId = storage.getOptions().getProjectId();

    Storage.BucketSourceOption[] bucketOptions =
        new Storage.BucketSourceOption[] {Storage.BucketSourceOption.userProject(projectId)};
    Identity projectOwner = Identity.projectOwner(projectId);
    Identity projectEditor = Identity.projectEditor(projectId);
    Identity projectViewer = Identity.projectViewer(projectId);
    Map<com.google.cloud.Role, Set<Identity>> bindingsWithoutPublicRead =
        ImmutableMap.of(
            StorageRoles.legacyBucketOwner(),
            new HashSet<>(Arrays.asList(projectOwner, projectEditor)),
            StorageRoles.legacyBucketReader(),
            (Set<Identity>) new HashSet<>(Collections.singleton(projectViewer)));
    Map<com.google.cloud.Role, Set<Identity>> bindingsWithPublicRead =
        ImmutableMap.of(
            StorageRoles.legacyBucketOwner(),
            new HashSet<>(Arrays.asList(projectOwner, projectEditor)),
            StorageRoles.legacyBucketReader(),
            new HashSet<>(Collections.singleton(projectViewer)),
            StorageRoles.legacyObjectReader(),
            (Set<Identity>) new HashSet<>(Collections.singleton(Identity.allUsers())));

    // Validate getting policy.
    Policy currentPolicy = storage.getIamPolicy(BUCKET_REQUESTER_PAYS, bucketOptions);
    assertEquals(bindingsWithoutPublicRead, currentPolicy.getBindings());

    // Validate updating policy.
    Policy updatedPolicy =
        storage.setIamPolicy(
            BUCKET_REQUESTER_PAYS,
            currentPolicy
                .toBuilder()
                .addIdentity(StorageRoles.legacyObjectReader(), Identity.allUsers())
                .build(),
            bucketOptions);
    assertEquals(bindingsWithPublicRead, updatedPolicy.getBindings());
    Policy revertedPolicy =
        storage.setIamPolicy(
            BUCKET_REQUESTER_PAYS,
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
            BUCKET_REQUESTER_PAYS,
            ImmutableList.of("storage.buckets.getIamPolicy", "storage.buckets.setIamPolicy"),
            bucketOptions));
    Bucket bucketFalse =
        storage.update(
            bucketTrue.toBuilder().setRequesterPays(false).build(),
            Storage.BucketTargetOption.userProject(projectId));
    assertFalse(bucketFalse.requesterPays());
  }

  @Test
  public void testBucketPolicyV1() {
    String projectId = storage.getOptions().getProjectId();

    Storage.BucketSourceOption[] bucketOptions = new Storage.BucketSourceOption[] {};
    Identity projectOwner = Identity.projectOwner(projectId);
    Identity projectEditor = Identity.projectEditor(projectId);
    Identity projectViewer = Identity.projectViewer(projectId);
    Map<com.google.cloud.Role, Set<Identity>> bindingsWithoutPublicRead =
        ImmutableMap.of(
            StorageRoles.legacyBucketOwner(),
            new HashSet<>(Arrays.asList(projectOwner, projectEditor)),
            StorageRoles.legacyBucketReader(),
            (Set<Identity>) new HashSet<>(Collections.singleton(projectViewer)));
    Map<com.google.cloud.Role, Set<Identity>> bindingsWithPublicRead =
        ImmutableMap.of(
            StorageRoles.legacyBucketOwner(),
            new HashSet<>(Arrays.asList(projectOwner, projectEditor)),
            StorageRoles.legacyBucketReader(),
            new HashSet<>(Collections.singleton(projectViewer)),
            StorageRoles.legacyObjectReader(),
            (Set<Identity>) new HashSet<>(Collections.singleton(Identity.allUsers())));

    // Validate getting policy.
    Policy currentPolicy = storage.getIamPolicy(BUCKET, bucketOptions);
    assertEquals(bindingsWithoutPublicRead, currentPolicy.getBindings());

    // Validate updating policy.
    Policy updatedPolicy =
        storage.setIamPolicy(
            BUCKET,
            currentPolicy
                .toBuilder()
                .addIdentity(StorageRoles.legacyObjectReader(), Identity.allUsers())
                .build(),
            bucketOptions);
    assertEquals(bindingsWithPublicRead, updatedPolicy.getBindings());
    Policy revertedPolicy =
        storage.setIamPolicy(
            BUCKET,
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
            BUCKET,
            ImmutableList.of("storage.buckets.getIamPolicy", "storage.buckets.setIamPolicy"),
            bucketOptions));
  }

  @Test
  public void testBucketPolicyV3() {
    // Enable Uniform Bucket-Level Access
    storage.update(
        BucketInfo.newBuilder(BUCKET)
            .setIamConfiguration(
                BucketInfo.IamConfiguration.newBuilder()
                    .setIsUniformBucketLevelAccessEnabled(true)
                    .build())
            .build());
    String projectId = storage.getOptions().getProjectId();

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

    // Validate getting policy.
    Policy currentPolicy = storage.getIamPolicy(BUCKET, bucketOptions);
    assertEquals(bindingsWithoutPublicRead, currentPolicy.getBindingsList());

    // Validate updating policy.
    List<com.google.cloud.Binding> currentBindings = new ArrayList(currentPolicy.getBindingsList());
    currentBindings.add(
        com.google.cloud.Binding.newBuilder()
            .setRole(StorageRoles.legacyObjectReader().getValue())
            .addMembers(Identity.allUsers().strValue())
            .build());
    Policy updatedPolicy =
        storage.setIamPolicy(
            BUCKET, currentPolicy.toBuilder().setBindings(currentBindings).build(), bucketOptions);
    assertTrue(
        bindingsWithPublicRead.size() == updatedPolicy.getBindingsList().size()
            && bindingsWithPublicRead.containsAll(updatedPolicy.getBindingsList()));

    // Remove a member
    List<com.google.cloud.Binding> updatedBindings = new ArrayList(updatedPolicy.getBindingsList());
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
            BUCKET, updatedPolicy.toBuilder().setBindings(updatedBindings).build(), bucketOptions);

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
            BUCKET,
            revertedPolicy.toBuilder().setBindings(conditionalBindings).setVersion(3).build(),
            bucketOptions);
    assertTrue(
        bindingsWithConditionalPolicy.size() == conditionalPolicy.getBindingsList().size()
            && bindingsWithConditionalPolicy.containsAll(conditionalPolicy.getBindingsList()));

    // Remove Conditional Policy
    conditionalPolicy =
        storage.setIamPolicy(
            BUCKET,
            conditionalPolicy.toBuilder().setBindings(updatedBindings).setVersion(3).build(),
            bucketOptions);

    // Validate testing permissions.
    List<Boolean> expectedPermissions = ImmutableList.of(true, true);
    assertEquals(
        expectedPermissions,
        storage.testIamPermissions(
            BUCKET,
            ImmutableList.of("storage.buckets.getIamPolicy", "storage.buckets.setIamPolicy"),
            bucketOptions));

    // Disable Uniform Bucket-Level Access
    storage.update(
        BucketInfo.newBuilder(BUCKET)
            .setIamConfiguration(
                BucketInfo.IamConfiguration.newBuilder()
                    .setIsUniformBucketLevelAccessEnabled(false)
                    .build())
            .build());
  }

  @Test
  @SuppressWarnings({"unchecked", "deprecation"})
  public void testBucketWithBucketPolicyOnlyEnabled() throws Exception {
    String bucket = RemoteStorageHelper.generateBucketName();
    try {
      storage.create(
          Bucket.newBuilder(bucket)
              .setIamConfiguration(
                  BucketInfo.IamConfiguration.newBuilder()
                      .setIsBucketPolicyOnlyEnabled(true)
                      .build())
              .build());

      Bucket remoteBucket =
          storage.get(bucket, Storage.BucketGetOption.fields(BucketField.IAMCONFIGURATION));

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
      RemoteStorageHelper.forceDelete(storage, bucket, 1, TimeUnit.MINUTES);
    }
  }

  @Test
  public void testBucketWithUniformBucketLevelAccessEnabled() throws Exception {
    String bucket = RemoteStorageHelper.generateBucketName();
    try {
      storage.create(
          Bucket.newBuilder(bucket)
              .setIamConfiguration(
                  BucketInfo.IamConfiguration.newBuilder()
                      .setIsUniformBucketLevelAccessEnabled(true)
                      .build())
              .build());

      Bucket remoteBucket =
          storage.get(bucket, Storage.BucketGetOption.fields(BucketField.IAMCONFIGURATION));

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
      RemoteStorageHelper.forceDelete(storage, bucket, 1, TimeUnit.MINUTES);
    }
  }

  @Test
  public void testEnableAndDisableUniformBucketLevelAccessOnExistingBucket() throws Exception {
    String bpoBucket = RemoteStorageHelper.generateBucketName();
    try {
      BucketInfo.IamConfiguration ublaDisabledIamConfiguration =
          BucketInfo.IamConfiguration.newBuilder()
              .setIsUniformBucketLevelAccessEnabled(false)
              .build();
      Bucket bucket =
          storage.create(
              Bucket.newBuilder(bpoBucket)
                  .setIamConfiguration(ublaDisabledIamConfiguration)
                  .setAcl(ImmutableList.of(Acl.of(User.ofAllAuthenticatedUsers(), Role.READER)))
                  .setDefaultAcl(
                      ImmutableList.of(Acl.of(User.ofAllAuthenticatedUsers(), Role.READER)))
                  .build());

      bucket
          .toBuilder()
          .setAcl(null)
          .setDefaultAcl(null)
          .setIamConfiguration(
              ublaDisabledIamConfiguration
                  .toBuilder()
                  .setIsUniformBucketLevelAccessEnabled(true)
                  .build())
          .build()
          .update();

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
    } finally {
      RemoteStorageHelper.forceDelete(storage, bpoBucket, 1, TimeUnit.MINUTES);
    }
  }

  @Test
  public void testEnforcedPublicAccessPreventionOnBucket() throws Exception {
    String papBucket = RemoteStorageHelper.generateBucketName();
    try {
      Bucket bucket = generatePublicAccessPreventionBucket(papBucket, true);
      // Making bucket public should fail.
      try {
        storage.setIamPolicy(
            papBucket,
            Policy.newBuilder()
                .setVersion(3)
                .setBindings(
                    ImmutableList.<com.google.cloud.Binding>of(
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
        bucket.create(
            "pap-test-object",
            "".getBytes(),
            Bucket.BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ));
        fail("pap: expected adding allUsers ACL to object should fail");
      } catch (StorageException storageException) {
        // Creating an object with allUsers roles/storage.viewer permission
        // is not allowed. When Public Access Prevention is enabled.
        assertEquals(storageException.getCode(), 412);
      }
    } finally {
      RemoteStorageHelper.forceDelete(storage, papBucket, 1, TimeUnit.MINUTES);
    }
  }

  @Test
  public void testUnspecifiedPublicAccessPreventionOnBucket() throws Exception {
    String papBucket = RemoteStorageHelper.generateBucketName();
    try {
      Bucket bucket = generatePublicAccessPreventionBucket(papBucket, false);

      // Now, making object public or making bucket public should succeed.
      try {
        // Create a public object
        bucket.create(
            "pap-test-object",
            "".getBytes(),
            Bucket.BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ));
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
                    ImmutableList.<com.google.cloud.Binding>of(
                        com.google.cloud.Binding.newBuilder()
                            .setRole("roles/storage.objectViewer")
                            .addMembers("allUsers")
                            .build()))
                .build());
      } catch (StorageException storageException) {
        fail("pap: expected adding allUsers policy to bucket to succeed");
      }
    } finally {
      RemoteStorageHelper.forceDelete(storage, papBucket, 1, TimeUnit.MINUTES);
    }
  }

  @Test
  public void testUBLAWithPublicAccessPreventionOnBucket() throws Exception {
    String papBucket = RemoteStorageHelper.generateBucketName();
    try {
      Bucket bucket = generatePublicAccessPreventionBucket(papBucket, false);
      assertEquals(
          bucket.getIamConfiguration().getPublicAccessPrevention(),
          BucketInfo.PublicAccessPrevention.INHERITED);
      assertFalse(bucket.getIamConfiguration().isUniformBucketLevelAccessEnabled());
      assertFalse(bucket.getIamConfiguration().isBucketPolicyOnlyEnabled());

      // Update PAP setting to ENFORCED and should not affect UBLA setting.
      bucket
          .toBuilder()
          .setIamConfiguration(
              bucket
                  .getIamConfiguration()
                  .toBuilder()
                  .setPublicAccessPrevention(BucketInfo.PublicAccessPrevention.ENFORCED)
                  .build())
          .build()
          .update();
      bucket = storage.get(papBucket, Storage.BucketGetOption.fields(BucketField.IAMCONFIGURATION));
      assertEquals(
          bucket.getIamConfiguration().getPublicAccessPrevention(),
          BucketInfo.PublicAccessPrevention.ENFORCED);
      assertFalse(bucket.getIamConfiguration().isUniformBucketLevelAccessEnabled());
      assertFalse(bucket.getIamConfiguration().isBucketPolicyOnlyEnabled());

      // Updating UBLA should not affect PAP setting.
      bucket =
          bucket
              .toBuilder()
              .setIamConfiguration(
                  bucket
                      .getIamConfiguration()
                      .toBuilder()
                      .setIsUniformBucketLevelAccessEnabled(true)
                      .build())
              .build()
              .update();
      assertTrue(bucket.getIamConfiguration().isUniformBucketLevelAccessEnabled());
      assertTrue(bucket.getIamConfiguration().isBucketPolicyOnlyEnabled());
      assertEquals(
          bucket.getIamConfiguration().getPublicAccessPrevention(),
          BucketInfo.PublicAccessPrevention.ENFORCED);
    } finally {
      RemoteStorageHelper.forceDelete(storage, papBucket, 1, TimeUnit.MINUTES);
    }
  }

  @Test
  public void testListBucketRequesterPaysFails() throws InterruptedException {
    String projectId = storage.getOptions().getProjectId();
    Iterator<Bucket> bucketIterator =
        storage
            .list(
                Storage.BucketListOption.prefix(BUCKET),
                Storage.BucketListOption.fields(),
                Storage.BucketListOption.userProject(projectId))
            .iterateAll()
            .iterator();
    while (!bucketIterator.hasNext()) {
      Thread.sleep(500);
      bucketIterator =
          storage
              .list(Storage.BucketListOption.prefix(BUCKET), Storage.BucketListOption.fields())
              .iterateAll()
              .iterator();
    }
    while (bucketIterator.hasNext()) {
      Bucket remoteBucket = bucketIterator.next();
      assertTrue(remoteBucket.getName().startsWith(BUCKET));
      assertNull(remoteBucket.getCreateTime());
      assertNull(remoteBucket.getSelfLink());
    }
  }

  @Test
  public void testRetentionPolicyNoLock() throws ExecutionException, InterruptedException {
    String bucketName = RemoteStorageHelper.generateBucketName();
    Bucket remoteBucket =
        storage.create(
            BucketInfo.newBuilder(bucketName).setRetentionPeriod(RETENTION_PERIOD).build());
    try {
      assertEquals(RETENTION_PERIOD, remoteBucket.getRetentionPeriod());
      assertNotNull(remoteBucket.getRetentionEffectiveTime());
      assertNull(remoteBucket.retentionPolicyIsLocked());
      remoteBucket =
          storage.get(bucketName, Storage.BucketGetOption.fields(BucketField.RETENTION_POLICY));
      assertEquals(RETENTION_PERIOD, remoteBucket.getRetentionPeriod());
      assertNotNull(remoteBucket.getRetentionEffectiveTime());
      assertNull(remoteBucket.retentionPolicyIsLocked());
      String blobName = "test-create-with-retention-policy-hold";
      BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blobName).build();
      Blob remoteBlob = storage.create(blobInfo);
      assertNotNull(remoteBlob.getRetentionExpirationTime());
      remoteBucket = remoteBucket.toBuilder().setRetentionPeriod(null).build().update();
      assertNull(remoteBucket.getRetentionPeriod());
      remoteBucket = remoteBucket.toBuilder().setRetentionPeriod(null).build().update();
      assertNull(remoteBucket.getRetentionPeriod());
    } finally {
      RemoteStorageHelper.forceDelete(storage, bucketName, 5, TimeUnit.SECONDS);
    }
  }

}
