/*
 * Copyright 2015 Google LLC
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

package com.google.cloud.storage;

import static com.google.cloud.storage.BackwardCompatibilityUtils.millisOffsetDateTimeCodec;
import static com.google.cloud.storage.BackwardCompatibilityUtils.millisUtcCodec;
import static com.google.cloud.storage.BackwardCompatibilityUtils.nullableDurationMillisCodec;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Data;
import com.google.api.client.util.DateTime;
import com.google.api.core.BetaApi;
import com.google.api.services.storage.model.Bucket.Lifecycle.Rule;
import com.google.cloud.storage.Acl.Entity;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Google Storage bucket metadata;
 *
 * @see <a href="https://cloud.google.com/storage/docs/concepts-techniques#concepts">Concepts and
 *     Terminology</a>
 */
public class BucketInfo implements Serializable {

  // this class reference (LifecycleRule.DeleteLifecycleAction) must be long form.
  // if it is not long form, and instead an import it creates a cycle of serializable classes
  // which breaks the compiler.
  //
  // The error message looks like the following:
  //     java: cannot find symbol
  //       symbol:   class Serializable
  //       location: class com.google.cloud.storage.BucketInfo
  private static final Predicate<LifecycleRule> IS_DELETE_LIFECYCLE_RULE =
      r -> r.getAction().getActionType().equals(LifecycleRule.DeleteLifecycleAction.TYPE);

  private static final long serialVersionUID = -4712013629621638459L;
  private final String generatedId;
  private final BucketWithProject bucketWithProject;
  private final Acl.Entity owner;
  private final String selfLink;
  private final Boolean requesterPays;
  private final Boolean versioningEnabled;
  private final String indexPage;
  private final String notFoundPage;
  /**
   * The getter for this property never returns null, however null awareness is critical for
   * encoding to properly determine how to process rules conversion.
   *
   * @see ApiaryConversions#bucketInfo() encoder
   */
  final List<LifecycleRule> lifecycleRules;

  private final String etag;
  private final OffsetDateTime createTime;
  private final OffsetDateTime updateTime;
  private final Long metageneration;
  private final List<Cors> cors;
  private final List<Acl> acl;
  private final List<Acl> defaultAcl;
  private final String location;
  private final Rpo rpo;
  private final StorageClass storageClass;
  private final Map<String, String> labels;
  private final String defaultKmsKeyName;
  private final Boolean defaultEventBasedHold;
  private final OffsetDateTime retentionEffectiveTime;
  private final Boolean retentionPolicyIsLocked;
  private final Duration retentionPeriod;
  private final IamConfiguration iamConfiguration;
  private final String locationType;
  private final Logging logging;

  /**
   * non-private for backward compatibility on message class. log messages are now emitted from
   *
   * @see ApiaryConversions#lifecycleRule()
   */
  static final Logger log = Logger.getLogger(BucketInfo.class.getName());

  /**
   * Public Access Prevention enum with expected values.
   *
   * @see <a
   *     href="https://cloud.google.com/storage/docs/public-access-prevention">public-access-prevention</a>
   */
  public enum PublicAccessPrevention {
    ENFORCED("enforced"),
    /**
     * Default value for Public Access Prevention
     *
     * @deprecated use {@link #INHERITED}
     */
    @Deprecated
    UNSPECIFIED("inherited"),
    /**
     * If the api returns a value that isn't defined in {@link PublicAccessPrevention} this value
     * will be returned.
     */
    UNKNOWN(null),
    INHERITED("inherited");

    private final String value;

    PublicAccessPrevention(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public static PublicAccessPrevention parse(String value) {
      String upper = value.toUpperCase();
      switch (upper) {
        case "ENFORCED":
          return ENFORCED;
        case "UNSPECIFIED":
        case "INHERITED":
          return INHERITED;
        default:
          return UNKNOWN;
      }
    }
  }

  /**
   * The Bucket's IAM Configuration.
   *
   * @see <a href="https://cloud.google.com/storage/docs/uniform-bucket-level-access">uniform
   *     bucket-level access</a>
   * @see <a
   *     href="https://cloud.google.com/storage/docs/public-access-prevention">public-access-prevention</a>
   */
  public static class IamConfiguration implements Serializable {
    private static final long serialVersionUID = -8671736104909424616L;

    private final Boolean isUniformBucketLevelAccessEnabled;
    private final OffsetDateTime uniformBucketLevelAccessLockedTime;
    private final PublicAccessPrevention publicAccessPrevention;

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof IamConfiguration)) {
        return false;
      }
      IamConfiguration that = (IamConfiguration) o;
      return Objects.equals(
              isUniformBucketLevelAccessEnabled, that.isUniformBucketLevelAccessEnabled)
          && Objects.equals(
              uniformBucketLevelAccessLockedTime, that.uniformBucketLevelAccessLockedTime)
          && publicAccessPrevention == that.publicAccessPrevention;
    }

    @Override
    public int hashCode() {
      return Objects.hash(
          isUniformBucketLevelAccessEnabled,
          uniformBucketLevelAccessLockedTime,
          publicAccessPrevention);
    }

    private IamConfiguration(Builder builder) {
      this.isUniformBucketLevelAccessEnabled = builder.isUniformBucketLevelAccessEnabled;
      this.uniformBucketLevelAccessLockedTime = builder.uniformBucketLevelAccessLockedTime;
      this.publicAccessPrevention = builder.publicAccessPrevention;
    }

    public static Builder newBuilder() {
      return new Builder();
    }

    public Builder toBuilder() {
      Builder builder = new Builder();
      builder.isUniformBucketLevelAccessEnabled = isUniformBucketLevelAccessEnabled;
      builder.uniformBucketLevelAccessLockedTime = uniformBucketLevelAccessLockedTime;
      builder.publicAccessPrevention = publicAccessPrevention;
      return builder;
    }

    /** Deprecated in favor of isUniformBucketLevelAccessEnabled(). */
    @Deprecated
    public Boolean isBucketPolicyOnlyEnabled() {
      return isUniformBucketLevelAccessEnabled;
    }

    /** Deprecated in favor of uniformBucketLevelAccessLockedTime(). */
    @Deprecated
    public Long getBucketPolicyOnlyLockedTime() {
      return getUniformBucketLevelAccessLockedTime();
    }

    public Boolean isUniformBucketLevelAccessEnabled() {
      return isUniformBucketLevelAccessEnabled;
    }

    /** @deprecated {@link #getUniformBucketLevelAccessLockedTimeOffsetDateTime()} */
    @Deprecated
    public Long getUniformBucketLevelAccessLockedTime() {
      return millisOffsetDateTimeCodec.decode(uniformBucketLevelAccessLockedTime);
    }

    public OffsetDateTime getUniformBucketLevelAccessLockedTimeOffsetDateTime() {
      return uniformBucketLevelAccessLockedTime;
    }

    /** Returns the Public Access Prevention. * */
    public PublicAccessPrevention getPublicAccessPrevention() {
      return publicAccessPrevention;
    }

    /** Builder for {@code IamConfiguration} */
    public static class Builder {
      private Boolean isUniformBucketLevelAccessEnabled;
      private OffsetDateTime uniformBucketLevelAccessLockedTime;
      private PublicAccessPrevention publicAccessPrevention;

      /** Deprecated in favor of setIsUniformBucketLevelAccessEnabled(). */
      @Deprecated
      public Builder setIsBucketPolicyOnlyEnabled(Boolean isBucketPolicyOnlyEnabled) {
        this.isUniformBucketLevelAccessEnabled = isBucketPolicyOnlyEnabled;
        return this;
      }

      /** @deprecated in favor of {@link #setUniformBucketLevelAccessLockedTime(Long)}. */
      @Deprecated
      Builder setBucketPolicyOnlyLockedTime(Long bucketPolicyOnlyLockedTime) {
        return setUniformBucketLevelAccessLockedTime(bucketPolicyOnlyLockedTime);
      }

      /**
       * Sets whether uniform bucket-level access is enabled for this bucket. When this is enabled,
       * access to the bucket will be configured through IAM, and legacy ACL policies will not work.
       * When this is first enabled, {@code uniformBucketLevelAccess.lockedTime} will be set by the
       * API automatically. This field can then be disabled until the time specified, after which it
       * will become immutable and calls to change it will fail. If this is enabled, calls to access
       * legacy ACL information will fail.
       */
      public Builder setIsUniformBucketLevelAccessEnabled(
          Boolean isUniformBucketLevelAccessEnabled) {
        this.isUniformBucketLevelAccessEnabled = isUniformBucketLevelAccessEnabled;
        return this;
      }

      /**
       * Sets the deadline for switching {@code uniformBucketLevelAccess.enabled} back to false.
       * After this time passes, calls to do so will fail. This is package-private, since in general
       * this field should never be set by a user--it's automatically set by the backend when {@code
       * enabled} is set to true.
       *
       * @deprecated {@link #setUniformBucketLevelAccessLockedTimeOffsetDateTime(OffsetDateTime)}
       */
      @Deprecated
      Builder setUniformBucketLevelAccessLockedTime(Long uniformBucketLevelAccessLockedTime) {
        return setUniformBucketLevelAccessLockedTimeOffsetDateTime(
            millisOffsetDateTimeCodec.encode(uniformBucketLevelAccessLockedTime));
      }

      /**
       * Sets the deadline for switching {@code uniformBucketLevelAccess.enabled} back to false.
       * After this time passes, calls to do so will fail. This is package-private, since in general
       * this field should never be set by a user--it's automatically set by the backend when {@code
       * enabled} is set to true.
       */
      Builder setUniformBucketLevelAccessLockedTimeOffsetDateTime(
          OffsetDateTime uniformBucketLevelAccessLockedTime) {
        this.uniformBucketLevelAccessLockedTime = uniformBucketLevelAccessLockedTime;
        return this;
      }

      /**
       * Sets the bucket's Public Access Prevention configuration. Currently supported options are
       * {@link PublicAccessPrevention#INHERITED} or {@link PublicAccessPrevention#ENFORCED}
       *
       * @see <a
       *     href="https://cloud.google.com/storage/docs/public-access-prevention">public-access-prevention</a>
       */
      public Builder setPublicAccessPrevention(PublicAccessPrevention publicAccessPrevention) {
        this.publicAccessPrevention = publicAccessPrevention;
        return this;
      }

      /** Builds an {@code IamConfiguration} object */
      public IamConfiguration build() {
        return new IamConfiguration(this);
      }
    }
  }

  /**
   * The bucket's logging configuration, which defines the destination bucket and optional name
   * prefix for the current bucket's logs.
   */
  public static class Logging implements Serializable {

    private static final long serialVersionUID = -708892101216778492L;
    private final BucketWithProject logBucketWithProject;
    private final String logObjectPrefix;

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Logging)) {
        return false;
      }
      Logging logging = (Logging) o;
      return Objects.equals(logBucketWithProject, logging.logBucketWithProject)
          && Objects.equals(logObjectPrefix, logging.logObjectPrefix);
    }

    @Override
    public int hashCode() {
      return Objects.hash(logBucketWithProject, logObjectPrefix);
    }

    public static Builder newBuilder() {
      return new Builder();
    }

    public Builder toBuilder() {
      Builder builder = new Builder();
      builder.logBucketWithProject = new BucketWithProject(logBucketWithProject);
      builder.logObjectPrefix = logObjectPrefix;
      return builder;
    }

    String getLogBucketProject() {
      return logBucketWithProject.getProject();
    }

    public String getLogBucket() {
      return logBucketWithProject.getBucketName();
    }

    public String getLogObjectPrefix() {
      return logObjectPrefix;
    }

    private Logging(Builder builder) {
      this.logBucketWithProject = builder.logBucketWithProject;
      this.logObjectPrefix = builder.logObjectPrefix;
    }

    public static class Builder {
      private BucketWithProject logBucketWithProject;
      private String logObjectPrefix;

      public Builder() {
        logBucketWithProject = new BucketWithProject();
      }

      Builder setLogBucketProject(String project) {
        this.logBucketWithProject.setProject(project);
        return this;
      }

      /** The destination bucket where the current bucket's logs should be placed. */
      public Builder setLogBucket(String logBucket) {
        this.logBucketWithProject.setBucketName(logBucket);
        return this;
      }

      /** A prefix for log object names. */
      public Builder setLogObjectPrefix(String logObjectPrefix) {
        this.logObjectPrefix = logObjectPrefix;
        return this;
      }

      /** Builds an {@code Logging} object */
      public Logging build() {
        return new Logging(this);
      }
    }
  }

  /**
   * Lifecycle rule for a bucket. Allows supported Actions, such as deleting and changing storage
   * class, to be executed when certain Conditions are met.
   *
   * <p>Versions 1.50.0-1.111.2 of this library don’t support the CustomTimeBefore,
   * DaysSinceCustomTime, DaysSinceNoncurrentTime and NoncurrentTimeBefore lifecycle conditions. To
   * read GCS objects with those lifecycle conditions, update your Java client library to the latest
   * version.
   *
   * @see <a href="https://cloud.google.com/storage/docs/lifecycle#actions">Object Lifecycle
   *     Management</a>
   */
  public static class LifecycleRule implements Serializable {

    private static final long serialVersionUID = -5739807320148748613L;
    private final LifecycleAction lifecycleAction;
    private final LifecycleCondition lifecycleCondition;

    public LifecycleRule(LifecycleAction action, LifecycleCondition condition) {
      if (condition.getIsLive() == null
          && condition.getAge() == null
          && condition.getCreatedBefore() == null
          && condition.getMatchesStorageClass() == null
          && condition.getNumberOfNewerVersions() == null
          && condition.getDaysSinceNoncurrentTime() == null
          && condition.getNoncurrentTimeBefore() == null
          && condition.getCustomTimeBefore() == null
          && condition.getDaysSinceCustomTime() == null
          && condition.getMatchesPrefix() == null
          && condition.getMatchesSuffix() == null) {
        log.warning(
            "Creating a lifecycle condition with no supported conditions:\n"
                + this
                + "\nAttempting to update with this rule may cause errors. Please update "
                + " to the latest version of google-cloud-storage");
      }

      this.lifecycleAction = action;
      this.lifecycleCondition = condition;
    }

    public LifecycleAction getAction() {
      return lifecycleAction;
    }

    public LifecycleCondition getCondition() {
      return lifecycleCondition;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("lifecycleAction", lifecycleAction)
          .add("lifecycleCondition", lifecycleCondition)
          .toString();
    }

    @Override
    public int hashCode() {
      return Objects.hash(lifecycleAction, lifecycleCondition);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof LifecycleRule)) {
        return false;
      }
      LifecycleRule that = (LifecycleRule) o;
      return Objects.equals(lifecycleAction, that.lifecycleAction)
          && Objects.equals(lifecycleCondition, that.lifecycleCondition);
    }

    /**
     * Condition for a Lifecycle rule, specifies under what criteria an Action should be executed.
     *
     * @see <a href="https://cloud.google.com/storage/docs/lifecycle#conditions">Object Lifecycle
     *     Management</a>
     */
    public static class LifecycleCondition implements Serializable {
      private static final long serialVersionUID = -6482314338394768785L;
      private final Integer age;
      private final OffsetDateTime createdBefore;
      private final Integer numberOfNewerVersions;
      private final Boolean isLive;
      private final List<StorageClass> matchesStorageClass;
      private final Integer daysSinceNoncurrentTime;
      private final OffsetDateTime noncurrentTimeBefore;
      private final OffsetDateTime customTimeBefore;
      private final Integer daysSinceCustomTime;
      private final List<String> matchesPrefix;
      private final List<String> matchesSuffix;

      private LifecycleCondition(Builder builder) {
        this.age = builder.age;
        this.createdBefore = builder.createdBefore;
        this.numberOfNewerVersions = builder.numberOfNewerVersions;
        this.isLive = builder.isLive;
        this.matchesStorageClass = builder.matchesStorageClass;
        this.daysSinceNoncurrentTime = builder.daysSinceNoncurrentTime;
        this.noncurrentTimeBefore = builder.noncurrentTimeBefore;
        this.customTimeBefore = builder.customTimeBefore;
        this.daysSinceCustomTime = builder.daysSinceCustomTime;
        this.matchesPrefix = builder.matchesPrefix;
        this.matchesSuffix = builder.matchesSuffix;
      }

      public Builder toBuilder() {
        return newBuilder()
            .setAge(this.age)
            .setCreatedBeforeOffsetDateTime(this.createdBefore)
            .setNumberOfNewerVersions(this.numberOfNewerVersions)
            .setIsLive(this.isLive)
            .setMatchesStorageClass(this.matchesStorageClass)
            .setDaysSinceNoncurrentTime(this.daysSinceNoncurrentTime)
            .setNoncurrentTimeBeforeOffsetDateTime(this.noncurrentTimeBefore)
            .setCustomTimeBeforeOffsetDateTime(this.customTimeBefore)
            .setDaysSinceCustomTime(this.daysSinceCustomTime)
            .setMatchesPrefix(this.matchesPrefix)
            .setMatchesSuffix(this.matchesSuffix);
      }

      public static Builder newBuilder() {
        return new Builder();
      }

      @Override
      public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("age", age)
            .add("createBefore", createdBefore)
            .add("numberofNewerVersions", numberOfNewerVersions)
            .add("isLive", isLive)
            .add("matchesStorageClass", matchesStorageClass)
            .add("daysSinceNoncurrentTime", daysSinceNoncurrentTime)
            .add("noncurrentTimeBefore", noncurrentTimeBefore)
            .add("customTimeBefore", customTimeBefore)
            .add("daysSinceCustomTime", daysSinceCustomTime)
            .add("matchesPrefix", matchesPrefix)
            .add("matchesSuffix", matchesSuffix)
            .toString();
      }

      public Integer getAge() {
        return age;
      }

      /** @deprecated Use {@link #getCreatedBeforeOffsetDateTime()} */
      @Deprecated
      public DateTime getCreatedBefore() {
        return Utils.dateTimeCodec.nullable().encode(createdBefore);
      }

      /**
       * Returns the date and offset from UTC for this condition. If a time other than 00:00:00.000
       * is present in the value, GCS will truncate to 00:00:00.000.
       */
      public OffsetDateTime getCreatedBeforeOffsetDateTime() {
        return createdBefore;
      }

      public Integer getNumberOfNewerVersions() {
        return numberOfNewerVersions;
      }

      public Boolean getIsLive() {
        return isLive;
      }

      public List<StorageClass> getMatchesStorageClass() {
        return matchesStorageClass;
      }

      /** Returns the number of days elapsed since the noncurrent timestamp of an object. */
      public Integer getDaysSinceNoncurrentTime() {
        return daysSinceNoncurrentTime;
      }

      /**
       * Returns the date in RFC 3339 format with only the date part (for instance, "2013-01-15").
       *
       * @deprecated Use {@link #getNoncurrentTimeBeforeOffsetDateTime()}
       */
      @Deprecated
      public DateTime getNoncurrentTimeBefore() {
        return Utils.dateTimeCodec.nullable().encode(noncurrentTimeBefore);
      }

      /**
       * Returns the date and offset from UTC for this condition. If a time other than 00:00:00.000
       * is present in the value, GCS will truncate to 00:00:00.000.
       */
      public OffsetDateTime getNoncurrentTimeBeforeOffsetDateTime() {
        return noncurrentTimeBefore;
      }

      /**
       * Returns the date in RFC 3339 format with only the date part (for instance, "2013-01-15").
       *
       * @deprecated Use {@link #getCustomTimeBeforeOffsetDateTime()}
       */
      @Deprecated
      public DateTime getCustomTimeBefore() {
        return Utils.dateTimeCodec.nullable().encode(customTimeBefore);
      }

      /**
       * Returns the date and offset from UTC for this condition. If a time other than 00:00:00.000
       * is present in the value, GCS will truncate to 00:00:00.000.
       */
      public OffsetDateTime getCustomTimeBeforeOffsetDateTime() {
        return customTimeBefore;
      }

      /** Returns the number of days elapsed since the user-specified timestamp set on an object. */
      public Integer getDaysSinceCustomTime() {
        return daysSinceCustomTime;
      }

      public List<String> getMatchesPrefix() {
        return matchesPrefix;
      }

      public List<String> getMatchesSuffix() {
        return matchesSuffix;
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) {
          return true;
        }
        if (!(o instanceof LifecycleCondition)) {
          return false;
        }
        LifecycleCondition that = (LifecycleCondition) o;
        return Objects.equals(age, that.age)
            && Objects.equals(createdBefore, that.createdBefore)
            && Objects.equals(numberOfNewerVersions, that.numberOfNewerVersions)
            && Objects.equals(isLive, that.isLive)
            && Objects.equals(matchesStorageClass, that.matchesStorageClass)
            && Objects.equals(daysSinceNoncurrentTime, that.daysSinceNoncurrentTime)
            && Objects.equals(noncurrentTimeBefore, that.noncurrentTimeBefore)
            && Objects.equals(customTimeBefore, that.customTimeBefore)
            && Objects.equals(daysSinceCustomTime, that.daysSinceCustomTime);
      }

      @Override
      public int hashCode() {
        return Objects.hash(
            age,
            createdBefore,
            numberOfNewerVersions,
            isLive,
            matchesStorageClass,
            daysSinceNoncurrentTime,
            noncurrentTimeBefore,
            customTimeBefore,
            daysSinceCustomTime);
      }

      /** Builder for {@code LifecycleCondition}. */
      public static class Builder {
        private Integer age;
        private OffsetDateTime createdBefore;
        private Integer numberOfNewerVersions;
        private Boolean isLive;
        private List<StorageClass> matchesStorageClass;
        private Integer daysSinceNoncurrentTime;
        private OffsetDateTime noncurrentTimeBefore;
        private OffsetDateTime customTimeBefore;
        private Integer daysSinceCustomTime;
        private List<String> matchesPrefix;
        private List<String> matchesSuffix;

        private Builder() {}

        /**
         * Sets the age in days. This condition is satisfied when a Blob reaches the specified age
         * (in days). When you specify the Age condition, you are specifying a Time to Live (TTL)
         * for objects in a bucket with lifecycle management configured. The time when the Age
         * condition is considered to be satisfied is calculated by adding the specified value to
         * the object creation time.
         */
        public Builder setAge(Integer age) {
          this.age = age;
          return this;
        }

        /**
         * Sets the date a Blob should be created before for an Action to be executed. Note that
         * only the date will be considered, if the time is specified it will be truncated. This
         * condition is satisfied when an object is created before midnight of the specified date in
         * UTC.
         *
         * @deprecated Use {@link #setCreatedBeforeOffsetDateTime(OffsetDateTime)}
         */
        @Deprecated
        public Builder setCreatedBefore(DateTime createdBefore) {
          return setCreatedBeforeOffsetDateTime(
              Utils.dateTimeCodec.nullable().decode(createdBefore));
        }

        /**
         * Sets the date a Blob should be created before for an Action to be executed. Note that
         * only the date will be considered, if the time is specified it will be truncated. This
         * condition is satisfied when an object is created before midnight of the specified date in
         * UTC.
         */
        public Builder setCreatedBeforeOffsetDateTime(OffsetDateTime createdBefore) {
          this.createdBefore = createdBefore;
          return this;
        }

        /**
         * Sets the number of newer versions a Blob should have for an Action to be executed.
         * Relevant only when versioning is enabled on a bucket. *
         */
        public Builder setNumberOfNewerVersions(Integer numberOfNewerVersions) {
          this.numberOfNewerVersions = numberOfNewerVersions;
          return this;
        }

        /**
         * Sets an isLive Boolean condition. If the value is true, this lifecycle condition matches
         * only live Blobs; if the value is false, it matches only archived objects. For the
         * purposes of this condition, Blobs in non-versioned buckets are considered live.
         */
        public Builder setIsLive(Boolean live) {
          this.isLive = live;
          return this;
        }

        /**
         * Sets a list of Storage Classes for a objects that satisfy the condition to execute the
         * Action. *
         */
        public Builder setMatchesStorageClass(List<StorageClass> matchesStorageClass) {
          this.matchesStorageClass = matchesStorageClass;
          return this;
        }

        /**
         * Sets the number of days elapsed since the noncurrent timestamp of an object. The
         * condition is satisfied if the days elapsed is at least this number. This condition is
         * relevant only for versioned objects. The value of the field must be a nonnegative
         * integer. If it's zero, the object version will become eligible for Lifecycle action as
         * soon as it becomes noncurrent.
         */
        public Builder setDaysSinceNoncurrentTime(Integer daysSinceNoncurrentTime) {
          this.daysSinceNoncurrentTime = daysSinceNoncurrentTime;
          return this;
        }

        /**
         * Sets the date in RFC 3339 format with only the date part (for instance, "2013-01-15").
         * Note that only date part will be considered, if the time is specified it will be
         * truncated. This condition is satisfied when the noncurrent time on an object is before
         * this date. This condition is relevant only for versioned objects.
         *
         * @deprecated Use {@link #setNoncurrentTimeBeforeOffsetDateTime(OffsetDateTime)}
         */
        @Deprecated
        public Builder setNoncurrentTimeBefore(DateTime noncurrentTimeBefore) {
          return setNoncurrentTimeBeforeOffsetDateTime(
              Utils.dateTimeCodec.nullable().decode(noncurrentTimeBefore));
        }

        /**
         * Sets the date with only the date part (for instance, "2013-01-15"). Note that only date
         * part will be considered, if the time is specified it will be truncated. This condition is
         * satisfied when the noncurrent time on an object is before this date. This condition is
         * relevant only for versioned objects.
         */
        public Builder setNoncurrentTimeBeforeOffsetDateTime(OffsetDateTime noncurrentTimeBefore) {
          this.noncurrentTimeBefore = noncurrentTimeBefore;
          return this;
        }

        /**
         * Sets the date in RFC 3339 format with only the date part (for instance, "2013-01-15").
         * Note that only date part will be considered, if the time is specified it will be
         * truncated. This condition is satisfied when the custom time on an object is before this
         * date in UTC.
         *
         * @deprecated Use {@link #setCustomTimeBeforeOffsetDateTime(OffsetDateTime)}
         */
        @Deprecated
        public Builder setCustomTimeBefore(DateTime customTimeBefore) {
          return setCustomTimeBeforeOffsetDateTime(
              Utils.dateTimeCodec.nullable().decode(customTimeBefore));
        }

        /**
         * Sets the date with only the date part (for instance, "2013-01-15"). Note that only date
         * part will be considered, if the time is specified it will be truncated. This condition is
         * satisfied when the custom time on an object is before this date in UTC.
         */
        public Builder setCustomTimeBeforeOffsetDateTime(OffsetDateTime customTimeBefore) {
          this.customTimeBefore = customTimeBefore;
          return this;
        }

        /**
         * Sets the number of days elapsed since the user-specified timestamp set on an object. The
         * condition is satisfied if the days elapsed is at least this number. If no custom
         * timestamp is specified on an object, the condition does not apply.
         */
        public Builder setDaysSinceCustomTime(Integer daysSinceCustomTime) {
          this.daysSinceCustomTime = daysSinceCustomTime;
          return this;
        }

        /**
         * Sets the list of prefixes. If any prefix matches the beginning of the object’s name, this
         * portion of the condition is satisfied for that object.
         */
        public Builder setMatchesPrefix(List<String> matchesPrefix) {
          this.matchesPrefix = matchesPrefix != null ? ImmutableList.copyOf(matchesPrefix) : null;
          return this;
        }

        /**
         * Sets the list of suffixes. If any suffix matches the end of the object’s name, this
         * portion of the condition is satisfied for that object.
         */
        public Builder setMatchesSuffix(List<String> matchesSuffix) {
          this.matchesSuffix = matchesSuffix != null ? ImmutableList.copyOf(matchesSuffix) : null;
          return this;
        }

        /** Builds a {@code LifecycleCondition} object. * */
        public LifecycleCondition build() {
          return new LifecycleCondition(this);
        }
      }
    }

    /**
     * Base class for the Action to take when a Lifecycle Condition is met. Supported Actions are
     * expressed as subclasses of this class, accessed by static factory methods.
     */
    public static class LifecycleAction implements Serializable {
      private static final long serialVersionUID = 5801228724709173284L;

      private final String actionType;

      public LifecycleAction(String actionType) {
        this.actionType = actionType;
      }

      public String getActionType() {
        return actionType;
      }

      @Override
      public String toString() {
        return MoreObjects.toStringHelper(this).add("actionType", getActionType()).toString();
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) {
          return true;
        }
        if (!(o instanceof LifecycleAction)) {
          return false;
        }
        LifecycleAction that = (LifecycleAction) o;
        return Objects.equals(actionType, that.actionType);
      }

      @Override
      public int hashCode() {
        return Objects.hash(actionType);
      }

      /**
       * Creates a new {@code DeleteLifecycleAction}. Blobs that meet the Condition associated with
       * this action will be deleted.
       */
      public static DeleteLifecycleAction newDeleteAction() {
        return new DeleteLifecycleAction();
      }

      /**
       * Creates a new {@code SetStorageClassLifecycleAction}. A Blob's storage class that meets the
       * action's conditions will be changed to the specified storage class.
       *
       * @param storageClass The new storage class to use when conditions are met for this action.
       */
      public static SetStorageClassLifecycleAction newSetStorageClassAction(
          StorageClass storageClass) {
        return new SetStorageClassLifecycleAction(storageClass);
      }

      /**
       * Create a new {@code AbortIncompleteMPUAction}. An incomplete multipart upload will be
       * aborted when the multipart upload meets the specified condition. Age is the only condition
       * supported for this action. See: https://cloud.google.com/storage/docs/lifecycle##abort-mpu
       */
      public static LifecycleAction newAbortIncompleteMPUploadAction() {
        return new AbortIncompleteMPUAction();
      }

      /**
       * Creates a new {@code LifecycleAction , with no specific supported action associated with it. This
       * is only intended as a "backup" for when the library doesn't recognize the type, and should
       * generally not be used, instead use the supported actions, and upgrade the library if necessary
       * to get new supported actions.
       */
      public static LifecycleAction newLifecycleAction(String actionType) {
        return new LifecycleAction(actionType);
      }
    }

    public static class DeleteLifecycleAction extends LifecycleAction {
      public static final String TYPE = "Delete";
      private static final long serialVersionUID = -2050986302222644873L;

      private DeleteLifecycleAction() {
        super(TYPE);
      }
    }

    public static class SetStorageClassLifecycleAction extends LifecycleAction {
      public static final String TYPE = "SetStorageClass";
      private static final long serialVersionUID = -62615467186000899L;

      private final StorageClass storageClass;

      private SetStorageClassLifecycleAction(StorageClass storageClass) {
        super(TYPE);
        this.storageClass = storageClass;
      }

      @Override
      public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("actionType", getActionType())
            .add("storageClass", storageClass.name())
            .toString();
      }

      public StorageClass getStorageClass() {
        return storageClass;
      }
    }

    public static class AbortIncompleteMPUAction extends LifecycleAction {
      public static final String TYPE = "AbortIncompleteMultipartUpload";
      private static final long serialVersionUID = -1072182310389348060L;

      private AbortIncompleteMPUAction() {
        super(TYPE);
      }
    }
  }

  /**
   * Base class for bucket's delete rules. Allows to configure automatic deletion of blobs and blobs
   * versions.
   *
   * @see <a href="https://cloud.google.com/storage/docs/lifecycle">Object Lifecycle Management</a>
   * @deprecated Use a {@code LifecycleRule} with a {@code DeleteLifecycleAction} and a {@code
   *     LifecycleCondition} which is equivalent to a subclass of DeleteRule instead.
   */
  @Deprecated
  public abstract static class DeleteRule implements Serializable {

    private static final long serialVersionUID = 3137971668395933033L;
    static final String SUPPORTED_ACTION = "Delete";
    private final Type type;

    public enum Type {
      AGE,
      CREATE_BEFORE,
      NUM_NEWER_VERSIONS,
      IS_LIVE,
      UNKNOWN
    }

    DeleteRule(Type type) {
      this.type = type;
    }

    public Type getType() {
      return type;
    }

    @Override
    public int hashCode() {
      return Objects.hash(type);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof DeleteRule)) {
        return false;
      }
      DeleteRule that = (DeleteRule) o;
      return type == that.type;
    }
  }

  /**
   * Delete rule class that sets a Time To Live for blobs in the bucket.
   *
   * @see <a href="https://cloud.google.com/storage/docs/lifecycle">Object Lifecycle Management</a>
   * @deprecated Use a {@code LifecycleRule} with a {@code DeleteLifecycleAction} and use {@code
   *     LifecycleCondition.Builder.setAge} instead.
   *     <p>For example, {@code new DeleteLifecycleAction(1)} is equivalent to {@code new
   *     LifecycleRule( LifecycleAction.newDeleteAction(),
   *     LifecycleCondition.newBuilder().setAge(1).build()))}
   */
  @Deprecated
  public static class AgeDeleteRule extends DeleteRule {

    private static final long serialVersionUID = 5697166940712116380L;
    private final int daysToLive;

    /**
     * Creates an {@code AgeDeleteRule} object.
     *
     * @param daysToLive blobs' Time To Live expressed in days. The time when the age condition is
     *     considered to be satisfied is computed by adding {@code daysToLive} days to the midnight
     *     following blob's creation time in UTC.
     */
    public AgeDeleteRule(int daysToLive) {
      super(Type.AGE);
      this.daysToLive = daysToLive;
    }

    public int getDaysToLive() {
      return daysToLive;
    }
  }

  static class RawDeleteRule extends DeleteRule {
    // TODO(benwhitehead): investigate having deleted the populateCondition method
    private static final long serialVersionUID = -7166938278642301933L;

    private transient Rule rule;

    RawDeleteRule(Rule rule) {
      super(Type.UNKNOWN);
      this.rule = rule;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeUTF(rule.toString());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      rule = new JacksonFactory().fromString(in.readUTF(), Rule.class);
    }

    Rule getRule() {
      return rule;
    }
  }

  /**
   * Delete rule class for blobs in the bucket that have been created before a certain date.
   *
   * @see <a href="https://cloud.google.com/storage/docs/lifecycle">Object Lifecycle Management</a>
   * @deprecated Use a {@code LifecycleRule} with an action {@code DeleteLifecycleAction} and a
   *     condition {@code LifecycleCondition.Builder.setCreatedBefore} instead.
   */
  @Deprecated
  public static class CreatedBeforeDeleteRule extends DeleteRule {

    private static final long serialVersionUID = 881692650279195867L;
    private final OffsetDateTime time;

    /**
     * Creates an {@code CreatedBeforeDeleteRule} object.
     *
     * @param timeMillis a date in UTC. Blobs that have been created before midnight of the provided
     *     date meet the delete condition
     * @deprecated Use {@link #CreatedBeforeDeleteRule(OffsetDateTime)} instead
     */
    @Deprecated
    public CreatedBeforeDeleteRule(long timeMillis) {
      this(millisUtcCodec.encode(timeMillis));
    }

    /**
     * Creates an {@code CreatedBeforeDeleteRule} object.
     *
     * @param time Blobs that have been created before midnight of the provided date meet the delete
     *     condition
     */
    public CreatedBeforeDeleteRule(OffsetDateTime time) {
      super(Type.CREATE_BEFORE);
      this.time = time;
    }

    /** @deprecated {@link #getTime()} */
    @Deprecated
    public long getTimeMillis() {
      return millisUtcCodec.decode(time);
    }

    public OffsetDateTime getTime() {
      return time;
    }
  }

  /**
   * Delete rule class for versioned blobs. Specifies when to delete a blob's version according to
   * the number of available newer versions for that blob.
   *
   * @see <a href="https://cloud.google.com/storage/docs/lifecycle">Object Lifecycle Management</a>
   * @deprecated Use a {@code LifecycleRule} with a {@code DeleteLifecycleAction} and a condition
   *     {@code LifecycleCondition.Builder.setNumberOfNewerVersions} instead.
   */
  @Deprecated
  public static class NumNewerVersionsDeleteRule extends DeleteRule {

    private static final long serialVersionUID = -1955554976528303894L;
    private final int numNewerVersions;

    /**
     * Creates an {@code NumNewerVersionsDeleteRule} object.
     *
     * @param numNewerVersions the number of newer versions. A blob's version meets the delete
     *     condition when {@code numNewerVersions} newer versions are available.
     */
    public NumNewerVersionsDeleteRule(int numNewerVersions) {
      super(Type.NUM_NEWER_VERSIONS);
      this.numNewerVersions = numNewerVersions;
    }

    public int getNumNewerVersions() {
      return numNewerVersions;
    }
  }

  /**
   * Delete rule class to distinguish between live and archived blobs.
   *
   * @see <a href="https://cloud.google.com/storage/docs/lifecycle">Object Lifecycle Management</a>
   * @deprecated Use a {@code LifecycleRule} with a {@code DeleteLifecycleAction} and a condition
   *     {@code LifecycleCondition.Builder.setIsLive} instead.
   */
  @Deprecated
  public static class IsLiveDeleteRule extends DeleteRule {

    private static final long serialVersionUID = -3502994563121313364L;
    private final boolean isLive;

    /**
     * Creates an {@code IsLiveDeleteRule} object.
     *
     * @param isLive if set to {@code true} live blobs meet the delete condition. If set to {@code
     *     false} delete condition is met by archived blobs.
     */
    public IsLiveDeleteRule(boolean isLive) {
      super(Type.IS_LIVE);
      this.isLive = isLive;
    }

    public boolean isLive() {
      return isLive;
    }
  }

  /** Builder for {@code BucketInfo}. */
  public abstract static class Builder {
    Builder() {}

    abstract Builder setProject(String project);

    /** Sets the bucket's name. */
    public abstract Builder setName(String name);

    abstract Builder setGeneratedId(String generatedId);

    abstract Builder setOwner(Acl.Entity owner);

    abstract Builder setSelfLink(String selfLink);

    /**
     * Sets whether a user accessing the bucket or an object it contains should assume the transit
     * costs related to the access.
     */
    public abstract Builder setRequesterPays(Boolean requesterPays);

    /**
     * Sets whether versioning should be enabled for this bucket. When set to true, versioning is
     * fully enabled.
     */
    public abstract Builder setVersioningEnabled(Boolean enable);

    /**
     * Sets the bucket's website index page. Behaves as the bucket's directory index where missing
     * blobs are treated as potential directories.
     */
    public abstract Builder setIndexPage(String indexPage);

    /** Sets the custom object to return when a requested resource is not found. */
    public abstract Builder setNotFoundPage(String notFoundPage);

    /**
     * Sets the bucket's lifecycle configuration as a number of delete rules.
     *
     * @deprecated Use {@link #setLifecycleRules(Iterable)} instead, as in {@code
     *     setLifecycleRules(Collections.singletonList( new BucketInfo.LifecycleRule(
     *     LifecycleAction.newDeleteAction(), LifecycleCondition.newBuilder().setAge(5).build())));}
     */
    @Deprecated
    public abstract Builder setDeleteRules(Iterable<? extends DeleteRule> rules);

    /**
     * Sets the bucket's lifecycle configuration as a number of lifecycle rules, consisting of an
     * action and a condition.
     *
     * @see <a href="https://cloud.google.com/storage/docs/lifecycle">Object Lifecycle
     *     Management</a>
     */
    public abstract Builder setLifecycleRules(Iterable<? extends LifecycleRule> rules);

    /** Deletes the lifecycle rules of this bucket. */
    public abstract Builder deleteLifecycleRules();

    /**
     * Sets the bucket's Recovery Point Objective (RPO). This can only be set for a dual-region
     * bucket, and determines the speed at which data will be replicated between regions. See the
     * {@code Rpo} class for supported values, and <a
     * href="https://cloud.google.com/storage/docs/turbo-replication">here</a> for additional
     * details.
     */
    public abstract Builder setRpo(Rpo rpo);

    /**
     * Sets the bucket's storage class. This defines how blobs in the bucket are stored and
     * determines the SLA and the cost of storage. A list of supported values is available <a
     * href="https://cloud.google.com/storage/docs/storage-classes">here</a>.
     */
    public abstract Builder setStorageClass(StorageClass storageClass);

    /**
     * Sets the bucket's location. Data for blobs in the bucket resides in physical storage within
     * this region or regions. A list of supported values is available <a
     * href="https://cloud.google.com/storage/docs/bucket-locations">here</a>.
     */
    public abstract Builder setLocation(String location);

    abstract Builder setEtag(String etag);

    /** @deprecated {@link #setCreateTimeOffsetDateTime(OffsetDateTime)} */
    @Deprecated
    abstract Builder setCreateTime(Long createTime);

    Builder setCreateTimeOffsetDateTime(OffsetDateTime createTime) {
      // provide an implementation for source and binary compatibility which we override ourselves
      setCreateTime(millisOffsetDateTimeCodec.decode(createTime));
      return this;
    }

    /** @deprecated {@link #setUpdateTimeOffsetDateTime(OffsetDateTime)} */
    @Deprecated
    abstract Builder setUpdateTime(Long updateTime);

    Builder setUpdateTimeOffsetDateTime(OffsetDateTime updateTime) {
      // provide an implementation for source and binary compatibility which we override ourselves
      setCreateTime(millisOffsetDateTimeCodec.decode(updateTime));
      return this;
    }

    abstract Builder setMetageneration(Long metageneration);

    abstract Builder setLocationType(String locationType);

    /**
     * Sets the bucket's Cross-Origin Resource Sharing (CORS) configuration.
     *
     * @see <a href="https://cloud.google.com/storage/docs/cross-origin">Cross-Origin Resource
     *     Sharing (CORS)</a>
     */
    public abstract Builder setCors(Iterable<Cors> cors);

    /**
     * Sets the bucket's access control configuration.
     *
     * @see <a
     *     href="https://cloud.google.com/storage/docs/access-control#About-Access-Control-Lists">
     *     About Access Control Lists</a>
     */
    public abstract Builder setAcl(Iterable<Acl> acl);

    /**
     * Sets the default access control configuration to apply to bucket's blobs when no other
     * configuration is specified.
     *
     * @see <a
     *     href="https://cloud.google.com/storage/docs/access-control#About-Access-Control-Lists">
     *     About Access Control Lists</a>
     */
    public abstract Builder setDefaultAcl(Iterable<Acl> acl);

    /** Sets the label of this bucket. */
    public abstract Builder setLabels(Map<String, String> labels);

    /** Sets the default Cloud KMS key name for this bucket. */
    public abstract Builder setDefaultKmsKeyName(String defaultKmsKeyName);

    /** Sets the default event-based hold for this bucket. */
    @BetaApi
    public abstract Builder setDefaultEventBasedHold(Boolean defaultEventBasedHold);

    /** @deprecated {@link #setRetentionEffectiveTimeOffsetDateTime(OffsetDateTime)} */
    @BetaApi
    @Deprecated
    abstract Builder setRetentionEffectiveTime(Long retentionEffectiveTime);

    @BetaApi
    Builder setRetentionEffectiveTimeOffsetDateTime(OffsetDateTime retentionEffectiveTime) {
      return setRetentionEffectiveTime(millisOffsetDateTimeCodec.decode(retentionEffectiveTime));
    }

    @BetaApi
    abstract Builder setRetentionPolicyIsLocked(Boolean retentionPolicyIsLocked);

    /**
     * If policy is not locked this value can be cleared, increased, and decreased. If policy is
     * locked the retention period can only be increased.
     *
     * @deprecated Use {@link #setRetentionPeriodDuration(Duration)}
     */
    @BetaApi
    @Deprecated
    public abstract Builder setRetentionPeriod(Long retentionPeriod);

    /**
     * If policy is not locked this value can be cleared, increased, and decreased. If policy is
     * locked the retention period can only be increased.
     */
    @BetaApi
    public Builder setRetentionPeriodDuration(Duration retentionPeriod) {
      return setRetentionPeriod(nullableDurationMillisCodec.encode(retentionPeriod));
    }

    /**
     * Sets the IamConfiguration to specify whether IAM access should be enabled.
     *
     * @see <a href="https://cloud.google.com/storage/docs/bucket-policy-only">Bucket Policy
     *     Only</a>
     */
    @BetaApi
    public abstract Builder setIamConfiguration(IamConfiguration iamConfiguration);

    public abstract Builder setLogging(Logging logging);

    /** Creates a {@code BucketInfo} object. */
    public abstract BucketInfo build();
  }

  static final class BuilderImpl extends Builder {

    private String generatedId;
    private BucketWithProject bucketWithProject;
    private Acl.Entity owner;
    private String selfLink;
    private Boolean requesterPays;
    private Boolean versioningEnabled;
    private String indexPage;
    private String notFoundPage;
    @Nullable private List<LifecycleRule> lifecycleRules;
    private Rpo rpo;
    private StorageClass storageClass;
    private String location;
    private String etag;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
    private Long metageneration;
    private List<Cors> cors;
    private List<Acl> acl;
    private List<Acl> defaultAcl;
    private Map<String, String> labels;
    private String defaultKmsKeyName;
    private Boolean defaultEventBasedHold;
    private OffsetDateTime retentionEffectiveTime;
    private Boolean retentionPolicyIsLocked;
    private Duration retentionPeriod;
    private IamConfiguration iamConfiguration;
    private String locationType;
    private Logging logging;

    BuilderImpl(String name) {
      this.bucketWithProject = new BucketWithProject(name);
    }

    BuilderImpl(BucketInfo bucketInfo) {
      generatedId = bucketInfo.generatedId;
      bucketWithProject = bucketInfo.bucketWithProject;
      etag = bucketInfo.etag;
      createTime = bucketInfo.createTime;
      updateTime = bucketInfo.updateTime;
      metageneration = bucketInfo.metageneration;
      location = bucketInfo.location;
      rpo = bucketInfo.rpo;
      storageClass = bucketInfo.storageClass;
      cors = bucketInfo.cors;
      acl = bucketInfo.acl;
      defaultAcl = bucketInfo.defaultAcl;
      owner = bucketInfo.owner;
      selfLink = bucketInfo.selfLink;
      versioningEnabled = bucketInfo.versioningEnabled;
      indexPage = bucketInfo.indexPage;
      notFoundPage = bucketInfo.notFoundPage;
      lifecycleRules = bucketInfo.lifecycleRules;
      labels = bucketInfo.labels;
      requesterPays = bucketInfo.requesterPays;
      defaultKmsKeyName = bucketInfo.defaultKmsKeyName;
      defaultEventBasedHold = bucketInfo.defaultEventBasedHold;
      retentionEffectiveTime = bucketInfo.retentionEffectiveTime;
      retentionPolicyIsLocked = bucketInfo.retentionPolicyIsLocked;
      retentionPeriod = bucketInfo.retentionPeriod;
      iamConfiguration = bucketInfo.iamConfiguration;
      locationType = bucketInfo.locationType;
      logging = bucketInfo.logging;
    }

    @Override
    public Builder setName(String name) {
      this.bucketWithProject.setBucketName(checkNotNull(name));
      return this;
    }

    @Override
    Builder setProject(String project) {
      this.bucketWithProject.setProject(project);
      return this;
    }

    @Override
    Builder setGeneratedId(String generatedId) {
      this.generatedId = generatedId;
      return this;
    }

    @Override
    Builder setOwner(Acl.Entity owner) {
      this.owner = owner;
      return this;
    }

    @Override
    Builder setSelfLink(String selfLink) {
      this.selfLink = selfLink;
      return this;
    }

    @Override
    public Builder setVersioningEnabled(Boolean enable) {
      this.versioningEnabled = firstNonNull(enable, Data.<Boolean>nullOf(Boolean.class));
      return this;
    }

    @Override
    public Builder setRequesterPays(Boolean enable) {
      this.requesterPays = firstNonNull(enable, Data.<Boolean>nullOf(Boolean.class));
      return this;
    }

    @Override
    public Builder setIndexPage(String indexPage) {
      this.indexPage = indexPage;
      return this;
    }

    @Override
    public Builder setNotFoundPage(String notFoundPage) {
      this.notFoundPage = notFoundPage;
      return this;
    }

    /** @deprecated Use {@code setLifecycleRules} method instead. * */
    @Override
    @Deprecated
    public Builder setDeleteRules(Iterable<? extends DeleteRule> rules) {
      // if the provided rules are null or empty clear all current delete rules
      if (rules == null) {
        return clearDeleteLifecycleRules();
      } else {
        ArrayList<? extends DeleteRule> deleteRules = newArrayList(rules);
        if (deleteRules.isEmpty()) {
          if (lifecycleRules != null) {
            return clearDeleteLifecycleRules();
          } else {
            lifecycleRules = ImmutableList.of();
            return this;
          }
        } else {
          // if the provided rules are non-empty, replace all existing delete rules

          Stream<LifecycleRule> newDeleteRules =
              deleteRules.stream().map(BackwardCompatibilityUtils.deleteRuleCodec::encode);

          // if our current lifecycleRules are null, set to the newDeleteRules
          if (lifecycleRules == null) {
            return setLifecycleRules(newDeleteRules.collect(ImmutableList.toImmutableList()));
          } else {
            // if lifecycleRules is non-null, filter out existing delete rules, then add our new
            // ones
            ImmutableList<LifecycleRule> newLifecycleRules =
                Streams.concat(
                        lifecycleRules.stream().filter(IS_DELETE_LIFECYCLE_RULE.negate()),
                        newDeleteRules)
                    .collect(ImmutableList.toImmutableList());
            return setLifecycleRules(newLifecycleRules);
          }
        }
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder setLifecycleRules(Iterable<? extends LifecycleRule> rules) {
      if (rules != null) {
        if (rules instanceof ImmutableList) {
          this.lifecycleRules = (ImmutableList<LifecycleRule>) rules;
        } else {
          this.lifecycleRules = ImmutableList.copyOf(rules);
        }
      } else {
        this.lifecycleRules = ImmutableList.of();
      }
      return this;
    }

    @Override
    public Builder deleteLifecycleRules() {
      setLifecycleRules(null);
      return this;
    }

    @Override
    public Builder setRpo(Rpo rpo) {
      this.rpo = rpo;
      return this;
    }

    @Override
    public Builder setStorageClass(StorageClass storageClass) {
      this.storageClass = storageClass;
      return this;
    }

    @Override
    public Builder setLocation(String location) {
      this.location = location;
      return this;
    }

    @Override
    Builder setEtag(String etag) {
      this.etag = etag;
      return this;
    }

    /** @deprecated {@link #setCreateTimeOffsetDateTime(OffsetDateTime)} */
    @Deprecated
    @Override
    Builder setCreateTime(Long createTime) {
      this.createTime = millisOffsetDateTimeCodec.encode(createTime);
      return this;
    }

    @Override
    Builder setCreateTimeOffsetDateTime(OffsetDateTime createTime) {
      this.createTime = createTime;
      return this;
    }

    /** @deprecated {@link #setUpdateTimeOffsetDateTime(OffsetDateTime)} */
    @Deprecated
    @Override
    Builder setUpdateTime(Long updateTime) {
      this.updateTime = millisOffsetDateTimeCodec.encode(updateTime);
      return this;
    }

    @Override
    Builder setUpdateTimeOffsetDateTime(OffsetDateTime updateTime) {
      this.updateTime = updateTime;
      return this;
    }

    @Override
    Builder setMetageneration(Long metageneration) {
      this.metageneration = metageneration;
      return this;
    }

    @Override
    public Builder setCors(Iterable<Cors> cors) {
      this.cors = cors != null ? ImmutableList.copyOf(cors) : ImmutableList.<Cors>of();
      return this;
    }

    @Override
    public Builder setAcl(Iterable<Acl> acl) {
      this.acl = acl != null ? ImmutableList.copyOf(acl) : null;
      return this;
    }

    @Override
    public Builder setDefaultAcl(Iterable<Acl> acl) {
      this.defaultAcl = acl != null ? ImmutableList.copyOf(acl) : null;
      return this;
    }

    @Override
    public Builder setLabels(Map<String, String> labels) {
      if (labels != null) {
        this.labels =
            Maps.transformValues(
                labels,
                new Function<String, String>() {
                  @Override
                  public String apply(String input) {
                    // replace null values with empty strings
                    return input == null ? Data.<String>nullOf(String.class) : input;
                  }
                });
      }
      return this;
    }

    @Override
    public Builder setDefaultKmsKeyName(String defaultKmsKeyName) {
      this.defaultKmsKeyName =
          defaultKmsKeyName != null ? defaultKmsKeyName : Data.<String>nullOf(String.class);
      return this;
    }

    @Override
    public Builder setDefaultEventBasedHold(Boolean defaultEventBasedHold) {
      this.defaultEventBasedHold =
          firstNonNull(defaultEventBasedHold, Data.<Boolean>nullOf(Boolean.class));
      return this;
    }

    /** @deprecated Use {@link #setRetentionEffectiveTimeOffsetDateTime(OffsetDateTime)} */
    @Override
    @Deprecated
    Builder setRetentionEffectiveTime(Long retentionEffectiveTime) {
      return setRetentionEffectiveTimeOffsetDateTime(
          millisOffsetDateTimeCodec.encode(retentionEffectiveTime));
    }

    @Override
    Builder setRetentionEffectiveTimeOffsetDateTime(OffsetDateTime retentionEffectiveTime) {
      this.retentionEffectiveTime = retentionEffectiveTime;
      return this;
    }

    @Override
    Builder setRetentionPolicyIsLocked(Boolean retentionPolicyIsLocked) {
      this.retentionPolicyIsLocked =
          firstNonNull(retentionPolicyIsLocked, Data.<Boolean>nullOf(Boolean.class));
      return this;
    }

    /** @deprecated Use {@link #setRetentionPeriodDuration(Duration)} */
    @Override
    public Builder setRetentionPeriod(Long retentionPeriod) {
      return setRetentionPeriodDuration(nullableDurationMillisCodec.decode(retentionPeriod));
    }

    @Override
    public Builder setRetentionPeriodDuration(Duration retentionPeriod) {
      this.retentionPeriod = retentionPeriod;
      return this;
    }

    @Override
    public Builder setIamConfiguration(IamConfiguration iamConfiguration) {
      this.iamConfiguration = iamConfiguration;
      return this;
    }

    @Override
    public Builder setLogging(Logging logging) {
      this.logging = logging != null ? logging : BucketInfo.Logging.newBuilder().build();
      return this;
    }

    @Override
    Builder setLocationType(String locationType) {
      this.locationType = locationType;
      return this;
    }

    @Override
    public BucketInfo build() {
      checkNotNull(bucketWithProject.getBucketName());
      return new BucketInfo(this);
    }

    private Builder clearDeleteLifecycleRules() {
      if (lifecycleRules != null && !lifecycleRules.isEmpty()) {
        ImmutableList<LifecycleRule> nonDeleteRules =
            lifecycleRules.stream()
                .filter(IS_DELETE_LIFECYCLE_RULE.negate())
                .collect(ImmutableList.toImmutableList());
        return setLifecycleRules(nonDeleteRules);
      } else {
        return this;
      }
    }
  }

  BucketInfo(BuilderImpl builder) {
    generatedId = builder.generatedId;
    bucketWithProject = builder.bucketWithProject;
    etag = builder.etag;
    createTime = builder.createTime;
    updateTime = builder.updateTime;
    metageneration = builder.metageneration;
    location = builder.location;
    rpo = builder.rpo;
    storageClass = builder.storageClass;
    cors = builder.cors;
    acl = builder.acl;
    defaultAcl = builder.defaultAcl;
    owner = builder.owner;
    selfLink = builder.selfLink;
    versioningEnabled = builder.versioningEnabled;
    indexPage = builder.indexPage;
    notFoundPage = builder.notFoundPage;
    lifecycleRules = builder.lifecycleRules;
    labels = builder.labels;
    requesterPays = builder.requesterPays;
    defaultKmsKeyName = builder.defaultKmsKeyName;
    defaultEventBasedHold = builder.defaultEventBasedHold;
    retentionEffectiveTime = builder.retentionEffectiveTime;
    retentionPolicyIsLocked = builder.retentionPolicyIsLocked;
    retentionPeriod = builder.retentionPeriod;
    iamConfiguration = builder.iamConfiguration;
    locationType = builder.locationType;
    logging = builder.logging;
  }

  String getProject() {
    return bucketWithProject.getProject();
  }

  /** Returns the service-generated id for the bucket. */
  public String getGeneratedId() {
    return generatedId;
  }

  /** Returns the bucket's name. */
  public String getName() {
    return bucketWithProject.getBucketName();
  }

  /** Returns the bucket's owner. This is always the project team's owner group. */
  public Entity getOwner() {
    return owner;
  }

  /** Returns the URI of this bucket as a string. */
  public String getSelfLink() {
    return selfLink;
  }

  /**
   * Returns a {@code Boolean} with either {@code true}, {@code null} and in certain cases {@code
   * false}.
   *
   * <p>Case 1: {@code true} the field {@link
   * com.google.cloud.storage.Storage.BucketField#VERSIONING} is selected in a {@link
   * Storage#get(String, Storage.BucketGetOption...)} and versions for the bucket is enabled.
   *
   * <p>Case 2.1: {@code null} the field {@link
   * com.google.cloud.storage.Storage.BucketField#VERSIONING} is selected in a {@link
   * Storage#get(String, Storage.BucketGetOption...)}, but versions for the bucket is not enabled.
   * This case can be considered implicitly {@code false}.
   *
   * <p>Case 2.2: {@code null} the field {@link
   * com.google.cloud.storage.Storage.BucketField#VERSIONING} is not selected in a {@link
   * Storage#get(String, Storage.BucketGetOption...)}, and the state for this field is unknown.
   *
   * <p>Case 3: {@code false} versions is explicitly set to false client side for a follow-up
   * request for example {@link Storage#update(BucketInfo, Storage.BucketTargetOption...)} in which
   * case the value of versions will remain {@code false} for for the given instance.
   */
  public Boolean versioningEnabled() {
    return Data.isNull(versioningEnabled) ? null : versioningEnabled;
  }

  /**
   * Returns a {@code Boolean} with either {@code true}, {@code false}, and in a specific case
   * {@code null}.
   *
   * <p>Case 1: {@code true} the field {@link com.google.cloud.storage.Storage.BucketField#BILLING}
   * is selected in a {@link Storage#get(String, Storage.BucketGetOption...)} and requester pays for
   * the bucket is enabled.
   *
   * <p>Case 2: {@code false} the field {@link com.google.cloud.storage.Storage.BucketField#BILLING}
   * in a {@link Storage#get(String, Storage.BucketGetOption...)} is selected and requester pays for
   * the bucket is disable.
   *
   * <p>Case 3: {@code null} the field {@link com.google.cloud.storage.Storage.BucketField#BILLING}
   * in a {@link Storage#get(String, Storage.BucketGetOption...)} is not selected, the value is
   * unknown.
   */
  public Boolean requesterPays() {
    return Data.isNull(requesterPays) ? null : requesterPays;
  }

  /**
   * Returns bucket's website index page. Behaves as the bucket's directory index where missing
   * blobs are treated as potential directories.
   */
  public String getIndexPage() {
    return indexPage;
  }

  /** Returns the custom object to return when a requested resource is not found. */
  public String getNotFoundPage() {
    return notFoundPage;
  }

  /**
   * Returns bucket's lifecycle configuration as a number of delete rules.
   *
   * @see <a href="https://cloud.google.com/storage/docs/lifecycle">Lifecycle Management</a>
   */
  @Deprecated
  public List<? extends DeleteRule> getDeleteRules() {
    return getLifecycleRules().stream()
        .filter(IS_DELETE_LIFECYCLE_RULE)
        .map(BackwardCompatibilityUtils.deleteRuleCodec::decode)
        .collect(ImmutableList.toImmutableList());
  }

  @NonNull
  public List<? extends LifecycleRule> getLifecycleRules() {
    return lifecycleRules != null ? lifecycleRules : ImmutableList.<LifecycleRule>of();
  }

  /**
   * Returns HTTP 1.1 Entity tag for the bucket.
   *
   * @see <a href="http://tools.ietf.org/html/rfc2616#section-3.11">Entity Tags</a>
   */
  public String getEtag() {
    return etag;
  }

  /**
   * Returns the time at which the bucket was created.
   *
   * @deprecated {@link #getCreateTimeOffsetDateTime()}
   */
  @Deprecated
  public Long getCreateTime() {
    return millisOffsetDateTimeCodec.decode(createTime);
  }

  public OffsetDateTime getCreateTimeOffsetDateTime() {
    return createTime;
  }

  /**
   * Returns the last modification time of the bucket's metadata expressed as the number of
   * milliseconds since the Unix epoch.
   *
   * @deprecated {@link #getUpdateTimeOffsetDateTime()}
   */
  @Deprecated
  public Long getUpdateTime() {
    return millisOffsetDateTimeCodec.decode(updateTime);
  }

  public OffsetDateTime getUpdateTimeOffsetDateTime() {
    return updateTime;
  }

  /** Returns the metadata generation of this bucket. */
  public Long getMetageneration() {
    return metageneration;
  }

  /**
   * Returns the bucket's location. Data for blobs in the bucket resides in physical storage within
   * this region or regions.
   *
   * @see <a href="https://cloud.google.com/storage/docs/bucket-locations">Bucket Locations</a>
   */
  public String getLocation() {
    return location;
  }

  /**
   * Returns the bucket's locationType.
   *
   * @see <a href="https://cloud.google.com/storage/docs/bucket-locations">Bucket LocationType</a>
   */
  public String getLocationType() {
    return locationType;
  }

  /**
   * Returns the bucket's recovery point objective (RPO). This defines how quickly data is
   * replicated between regions in a dual-region bucket. Not defined for single-region buckets.
   *
   * @see <a href="https://cloud.google.com/storage/docs/turbo-replication"Turbo Replication"</a>
   */
  public Rpo getRpo() {
    return rpo;
  }

  /**
   * Returns the bucket's storage class. This defines how blobs in the bucket are stored and
   * determines the SLA and the cost of storage.
   *
   * @see <a href="https://cloud.google.com/storage/docs/storage-classes">Storage Classes</a>
   */
  public StorageClass getStorageClass() {
    return storageClass;
  }

  /**
   * Returns the bucket's Cross-Origin Resource Sharing (CORS) configuration.
   *
   * @see <a href="https://cloud.google.com/storage/docs/cross-origin">Cross-Origin Resource Sharing
   *     (CORS)</a>
   */
  public List<Cors> getCors() {
    return cors;
  }

  /**
   * Returns the bucket's access control configuration.
   *
   * @see <a href="https://cloud.google.com/storage/docs/access-control#About-Access-Control-Lists">
   *     About Access Control Lists</a>
   */
  public List<Acl> getAcl() {
    return acl;
  }

  /**
   * Returns the default access control configuration for this bucket's blobs.
   *
   * @see <a href="https://cloud.google.com/storage/docs/access-control#About-Access-Control-Lists">
   *     About Access Control Lists</a>
   */
  public List<Acl> getDefaultAcl() {
    return defaultAcl;
  }

  /** Returns the labels for this bucket. */
  public Map<String, String> getLabels() {
    return labels;
  }

  /** Returns the default Cloud KMS key to be applied to newly inserted objects in this bucket. */
  public String getDefaultKmsKeyName() {
    return defaultKmsKeyName;
  }

  /**
   * Returns a {@code Boolean} with either {@code true}, {@code null} and in certain cases {@code
   * false}.
   *
   * <p>Case 1: {@code true} the field {@link
   * com.google.cloud.storage.Storage.BucketField#DEFAULT_EVENT_BASED_HOLD} is selected in a {@link
   * Storage#get(String, Storage.BucketGetOption...)} and default event-based hold for the bucket is
   * enabled.
   *
   * <p>Case 2.1: {@code null} the field {@link
   * com.google.cloud.storage.Storage.BucketField#DEFAULT_EVENT_BASED_HOLD} is selected in a {@link
   * Storage#get(String, Storage.BucketGetOption...)}, but default event-based hold for the bucket
   * is not enabled. This case can be considered implicitly {@code false}.
   *
   * <p>Case 2.2: {@code null} the field {@link
   * com.google.cloud.storage.Storage.BucketField#DEFAULT_EVENT_BASED_HOLD} is not selected in a
   * {@link Storage#get(String, Storage.BucketGetOption...)}, and the state for this field is
   * unknown.
   *
   * <p>Case 3: {@code false} default event-based hold is explicitly set to false using in a {@link
   * Builder#setDefaultEventBasedHold(Boolean)} client side for a follow-up request e.g. {@link
   * Storage#update(BucketInfo, Storage.BucketTargetOption...)} in which case the value of default
   * event-based hold will remain {@code false} for the given instance.
   */
  @BetaApi
  public Boolean getDefaultEventBasedHold() {
    return Data.isNull(defaultEventBasedHold) ? null : defaultEventBasedHold;
  }

  /**
   * Returns the retention effective time a policy took effect if a retention policy is defined as a
   * {@code Long}.
   *
   * @deprecated Use {@link #getRetentionPeriodDuration()}
   */
  @BetaApi
  @Deprecated
  public Long getRetentionEffectiveTime() {
    return Data.isNull(retentionEffectiveTime)
        ? null
        : millisOffsetDateTimeCodec.decode(retentionEffectiveTime);
  }

  /** Returns the retention effective time a policy took effect if a retention policy is defined. */
  @BetaApi
  public OffsetDateTime getRetentionEffectiveTimeOffsetDateTime() {
    return retentionEffectiveTime;
  }

  /**
   * Returns a {@code Boolean} with either {@code true} or {@code null}.
   *
   * <p>Case 1: {@code true} the field {@link
   * com.google.cloud.storage.Storage.BucketField#RETENTION_POLICY} is selected in a {@link
   * Storage#get(String, Storage.BucketGetOption...)} and retention policy for the bucket is locked.
   *
   * <p>Case 2.1: {@code null} the field {@link
   * com.google.cloud.storage.Storage.BucketField#RETENTION_POLICY} is selected in a {@link
   * Storage#get(String, Storage.BucketGetOption...)}, but retention policy for the bucket is not
   * locked. This case can be considered implicitly {@code false}.
   *
   * <p>Case 2.2: {@code null} the field {@link
   * com.google.cloud.storage.Storage.BucketField#RETENTION_POLICY} is not selected in a {@link
   * Storage#get(String, Storage.BucketGetOption...)}, and the state for this field is unknown.
   */
  @BetaApi
  public Boolean retentionPolicyIsLocked() {
    return Data.isNull(retentionPolicyIsLocked) ? null : retentionPolicyIsLocked;
  }

  /**
   * Returns the retention policy retention period.
   *
   * @deprecated Use {@link #getRetentionPeriodDuration()}
   */
  @BetaApi
  @Deprecated
  public Long getRetentionPeriod() {
    return nullableDurationMillisCodec.encode(retentionPeriod);
  }

  /** Returns the retention policy retention period. */
  @BetaApi
  public Duration getRetentionPeriodDuration() {
    return retentionPeriod;
  }

  /** Returns the IAM configuration */
  @BetaApi
  public IamConfiguration getIamConfiguration() {
    return iamConfiguration;
  }

  /** Returns the Logging */
  public Logging getLogging() {
    return logging;
  }

  /** Returns a builder for the current bucket. */
  public Builder toBuilder() {
    return new BuilderImpl(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        generatedId,
        bucketWithProject,
        owner,
        selfLink,
        requesterPays,
        versioningEnabled,
        indexPage,
        notFoundPage,
        lifecycleRules,
        etag,
        createTime,
        updateTime,
        metageneration,
        cors,
        acl,
        defaultAcl,
        location,
        rpo,
        storageClass,
        labels,
        defaultKmsKeyName,
        defaultEventBasedHold,
        retentionEffectiveTime,
        retentionPolicyIsLocked,
        retentionPeriod,
        iamConfiguration,
        locationType,
        logging);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BucketInfo)) {
      return false;
    }
    BucketInfo that = (BucketInfo) o;
    return Objects.equals(generatedId, that.generatedId)
        && Objects.equals(bucketWithProject, that.bucketWithProject)
        && Objects.equals(owner, that.owner)
        && Objects.equals(selfLink, that.selfLink)
        && Objects.equals(requesterPays, that.requesterPays)
        && Objects.equals(versioningEnabled, that.versioningEnabled)
        && Objects.equals(indexPage, that.indexPage)
        && Objects.equals(notFoundPage, that.notFoundPage)
        && Objects.equals(lifecycleRules, that.lifecycleRules)
        && Objects.equals(etag, that.etag)
        && Objects.equals(createTime, that.createTime)
        && Objects.equals(updateTime, that.updateTime)
        && Objects.equals(metageneration, that.metageneration)
        && Objects.equals(cors, that.cors)
        && Objects.equals(acl, that.acl)
        && Objects.equals(defaultAcl, that.defaultAcl)
        && Objects.equals(location, that.location)
        && Objects.equals(rpo, that.rpo)
        && Objects.equals(storageClass, that.storageClass)
        && Objects.equals(labels, that.labels)
        && Objects.equals(defaultKmsKeyName, that.defaultKmsKeyName)
        && Objects.equals(defaultEventBasedHold, that.defaultEventBasedHold)
        && Objects.equals(retentionEffectiveTime, that.retentionEffectiveTime)
        && Objects.equals(retentionPolicyIsLocked, that.retentionPolicyIsLocked)
        && Objects.equals(retentionPeriod, that.retentionPeriod)
        && Objects.equals(iamConfiguration, that.iamConfiguration)
        && Objects.equals(locationType, that.locationType)
        && Objects.equals(logging, that.logging);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("name", bucketWithProject.getBucketName())
        .toString();
  }

  /**
   * Attach this instance to an instance of {@link Storage} thereby allowing RPCs to be performed
   * using the methods from the resulting {@link Bucket}
   */
  Bucket asBucket(Storage storage) {
    return new Bucket(storage, new BucketInfo.BuilderImpl(this));
  }

  /** Creates a {@code BucketInfo} object for the provided bucket name. */
  public static BucketInfo of(String name) {
    return newBuilder(name).build();
  }

  /** Returns a {@code BucketInfo} builder where the bucket's name is set to the provided name. */
  public static Builder newBuilder(String name) {
    return new BuilderImpl(name);
  }

  private static class BucketWithProject implements Serializable {
    private static final long serialVersionUID = 3620131016523045533L;
    private String project = "_";
    private String bucketName;

    BucketWithProject() {}

    BucketWithProject(String bucketName) {
      this.bucketName = bucketName;
    }

    BucketWithProject(BucketWithProject bucketWithProject) {
      this.project = bucketWithProject.project;
      this.bucketName = bucketWithProject.bucketName;
    }

    void setProject(String project) {
      this.project = project;
    }

    void setBucketName(String bucketName) {
      this.bucketName = bucketName;
    }

    String getProject() {
      return this.project;
    }

    String getBucketName() {
      return this.bucketName;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      BucketWithProject other = (BucketWithProject) o;
      return other.project.equals(this.project) && other.bucketName.equals(this.bucketName);
    }

    @Override
    public int hashCode() {
      return Objects.hash(project, bucketName);
    }
  }
}
