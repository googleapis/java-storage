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

package com.google.cloud.storage;

import static java.util.Objects.requireNonNull;

import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.StatusCode;
import com.google.api.services.storage.model.Bucket.Lifecycle.Rule;
import com.google.cloud.storage.BucketInfo.AgeDeleteRule;
import com.google.cloud.storage.BucketInfo.CreatedBeforeDeleteRule;
import com.google.cloud.storage.BucketInfo.DeleteRule;
import com.google.cloud.storage.BucketInfo.IsLiveDeleteRule;
import com.google.cloud.storage.BucketInfo.LifecycleRule;
import com.google.cloud.storage.BucketInfo.LifecycleRule.LifecycleAction;
import com.google.cloud.storage.BucketInfo.LifecycleRule.LifecycleCondition;
import com.google.cloud.storage.BucketInfo.NumNewerVersionsDeleteRule;
import com.google.cloud.storage.BucketInfo.RawDeleteRule;
import com.google.cloud.storage.Conversions.Codec;
import com.google.common.annotations.VisibleForTesting;
import io.grpc.Status.Code;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A collection of utilities that only exist to enable backward compatibility.
 *
 * <p>In general, the expectation is that any references to this class only come from @Deprecated
 * things.
 */
final class BackwardCompatibilityUtils {

  @SuppressWarnings("RedundantTypeArguments")
  // the <Long, OffsetDateTime> doesn't auto carry all the way through like intellij thinks it
  // would.
  static final Codec<@Nullable Long, @Nullable OffsetDateTime> millisOffsetDateTimeCodec =
      Codec.<Long, OffsetDateTime>of(
              l ->
                  Instant.ofEpochMilli(requireNonNull(l, "l must be non null"))
                      .atOffset(ZoneOffset.systemDefault().getRules().getOffset(Instant.now())),
              odt -> requireNonNull(odt, "odt must be non null").toInstant().toEpochMilli())
          .nullable();

  static final Codec<Long, OffsetDateTime> millisUtcCodec =
      Codec.of(
          l ->
              Instant.ofEpochMilli(requireNonNull(l, "l must be non null"))
                  .atOffset(ZoneOffset.UTC),
          odt -> requireNonNull(odt, "odt must be non null").toInstant().toEpochMilli());

  static final Codec<@Nullable Duration, @Nullable Long> nullableDurationMillisCodec =
      Utils.durationMillisCodec.nullable();

  @SuppressWarnings("deprecation")
  static final Codec<DeleteRule, LifecycleRule> deleteRuleCodec =
      Codec.of(
          BackwardCompatibilityUtils::deleteRuleEncode,
          BackwardCompatibilityUtils::deleteRuleDecode);

  private BackwardCompatibilityUtils() {}

  /**
   * When translating from gRPC Status Codes to the HTTP codes all of our middle ware expects, we
   * must take care to translate in accordance with the expected retry semantics already outlined
   * and validated for the JSON implementation. This is why we do not simply use {@link
   * GrpcStatusCode#of(Code)}{@link GrpcStatusCode#getCode() .getCode}{@link
   * StatusCode.Code#getHttpStatusCode() .getHttpStatusCode()} as it sometimes returns conflicting
   * HTTP codes for our retry handling.
   */
  @VisibleForTesting
  static int grpcCodeToHttpStatusCode(Code code) {
    switch (code) {
        // 200 Ok
      case OK:
        return 200;
        // 400 Bad Request
      case INVALID_ARGUMENT:
      case OUT_OF_RANGE:
        return 400;
        // 401 Unauthorized
      case UNAUTHENTICATED:
        return 401;
        // 403 Forbidden
      case PERMISSION_DENIED:
        return 403;
        // 404 Not Found
      case NOT_FOUND:
        return 404;
        // 408 Request Timeout
        // TODO
        // 412 Precondition Failed
      case FAILED_PRECONDITION:
        return 412;
        // 409 Conflict
      case ALREADY_EXISTS:
        return 409;
        // 429 Too Many Requests
      case RESOURCE_EXHAUSTED:
        return 429;
        // 500 Internal Server Error
      case INTERNAL:
        return 500;
        // 501 Not Implemented
      case UNIMPLEMENTED:
        return 501;
        // 503 Service Unavailable
      case UNAVAILABLE:
        return 503;
        // 504 Gateway Timeout
      case DEADLINE_EXCEEDED:
        return 504;
        // TODO

      case ABORTED: // ?
      case CANCELLED: // ?
      case UNKNOWN: // ?
      case DATA_LOSS: // ?
      default:
        return 0;
    }
  }

  @SuppressWarnings("deprecation")
  private static LifecycleRule deleteRuleEncode(DeleteRule from) {
    if (from instanceof RawDeleteRule) {
      RawDeleteRule raw = (RawDeleteRule) from;
      Rule rule = raw.getRule();
      String msg =
          "The lifecycle condition "
              + resolveRuleActionType(from)
              + " is not currently supported. Please update to the latest version of google-cloud-java."
              + " Also, use LifecycleRule rather than the deprecated DeleteRule.";
      // manually construct a log record, so we maintain class name and method name
      // from the old implicit values.
      LogRecord record = new LogRecord(Level.WARNING, msg);
      record.setLoggerName(BucketInfo.RawDeleteRule.class.getName());
      record.setSourceClassName(BucketInfo.RawDeleteRule.class.getName());
      record.setSourceMethodName("populateCondition");
      BucketInfo.log.log(record);

      LifecycleCondition condition =
          Conversions.apiary().lifecycleCondition().decode(rule.getCondition());
      return new LifecycleRule(LifecycleAction.newDeleteAction(), condition);
    }
    LifecycleCondition.Builder condition = LifecycleCondition.newBuilder();
    if (from instanceof CreatedBeforeDeleteRule) {
      CreatedBeforeDeleteRule r = (CreatedBeforeDeleteRule) from;
      condition.setCreatedBeforeOffsetDateTime(r.getTime());
    } else if (from instanceof AgeDeleteRule) {
      AgeDeleteRule r = (AgeDeleteRule) from;
      condition.setAge(r.getDaysToLive());
    } else if (from instanceof NumNewerVersionsDeleteRule) {
      NumNewerVersionsDeleteRule r = (NumNewerVersionsDeleteRule) from;
      condition.setNumberOfNewerVersions(r.getNumNewerVersions());
    } else if (from instanceof IsLiveDeleteRule) {
      IsLiveDeleteRule r = (IsLiveDeleteRule) from;
      condition.setIsLive(r.isLive());
    } // else would be RawDeleteRule which is handled above
    return new LifecycleRule(LifecycleAction.newDeleteAction(), condition.build());
  }

  @SuppressWarnings("deprecation")
  private static DeleteRule deleteRuleDecode(LifecycleRule from) {
    if (from.getAction() != null
        && BucketInfo.DeleteRule.SUPPORTED_ACTION.endsWith(resolveRuleActionType(from))) {
      LifecycleCondition condition = from.getCondition();
      Integer age = condition.getAge();
      if (age != null) {
        return new BucketInfo.AgeDeleteRule(age);
      }
      OffsetDateTime createdBefore = condition.getCreatedBeforeOffsetDateTime();
      if (createdBefore != null) {
        return new BucketInfo.CreatedBeforeDeleteRule(createdBefore);
      }
      Integer numNewerVersions = condition.getNumberOfNewerVersions();
      if (numNewerVersions != null) {
        return new BucketInfo.NumNewerVersionsDeleteRule(numNewerVersions);
      }
      Boolean isLive = condition.getIsLive();
      if (isLive != null) {
        return new BucketInfo.IsLiveDeleteRule(isLive);
      }
    }
    return new RawDeleteRule(Conversions.apiary().lifecycleRule().encode(from));
  }

  @SuppressWarnings("deprecation")
  private static String resolveRuleActionType(DeleteRule deleteRule) {
    if (deleteRule != null && deleteRule.getType() != null) {
      return deleteRule.getType().name();
    } else {
      return null;
    }
  }

  private static String resolveRuleActionType(LifecycleRule rule) {
    if (rule != null && rule.getAction() != null) {
      return rule.getAction().getActionType();
    } else {
      return null;
    }
  }
}
