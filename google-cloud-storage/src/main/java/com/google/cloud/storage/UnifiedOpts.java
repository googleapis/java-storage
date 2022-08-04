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

import static com.google.cloud.storage.Utils.todo;
import static com.google.common.base.Preconditions.checkArgument;

import com.google.api.gax.grpc.GrpcCallContext;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import com.google.storage.v2.GetObjectRequest;
import java.io.Serializable;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.crypto.spec.SecretKeySpec;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * The set of all "Options" we currently support for per-call parameters.
 *
 * <p>Most often, each of the respective types correspond to one of the parameters from <a
 * target="_blank" rel="noopener noreferrer"
 * href="https://cloud.google.com/storage/docs/json_api/v1/parameters">HTTP headers and common query
 * string parameters for JSON</a>. In the case of gRPC, sometimes the parameters are in the specific
 * request message or in grpc metadata.
 */
final class UnifiedOpts {

  /** Base interface type for each of the new options we're supporting. */
  interface Opt extends Serializable {}

  /**
   * A specialization of {@link java.util.function.UnaryOperator} which maintains its lower type for
   * {@link #identity()} and {@link #andThen(Mapper)}.
   */
  @FunctionalInterface
  interface Mapper<T> {
    T apply(T t);

    default Mapper<T> andThen(Mapper<T> then) {
      return t -> then.apply(apply(t));
    }

    static <T> Mapper<T> identity() {
      return t -> t;
    }
  }

  /** Base, marker interface of those Opts which represent a get/read/origin type relationship. */
  private interface SourceOpt extends Opt {}

  /**
   * Base, marker interface of those Opts which represent a set/write/destination type relationship.
   */
  private interface TargetOpt extends Opt {}

  /** Base, marker interface of those Opts which apply to listing operations. */
  private interface ListOpt extends Opt {}

  /** Marker interface of those Opts which are applicable to Bucket operations. */
  private interface ApplicableBucket {}

  /** Marker interface of those Opts which are applicable to Object/Blob operations. */
  private interface ApplicableObject {}

  /** Marker interface of those Opts which are applicable to HmacKey operations. */
  private interface ApplicableHmacKey {}

  /** Base interface for those Opts which may expose their values via gRPC Metadata */
  private interface GrpcMetadataMapper {
    default Mapper<GrpcCallContext> getGrpcMetadataMapper() {
      return Mapper.identity();
    }
  }

  /** Base interface for those Opts which are applicable to Object List operations */
  interface ObjectListOpt extends GrpcMetadataMapper, ListOpt, ApplicableObject {}

  /**
   * Base interface for those Opts which are applicable to Object Source (get/read/origin
   * relationship) operations
   */
  interface ObjectSourceOpt extends GrpcMetadataMapper, SourceOpt, ApplicableObject {
    Mapper<GetObjectRequest.Builder> getObject();
  }

  /**
   * Base interface for those Opts which are applicable to Object Target (set/write/destination
   * relationship) operations
   */
  interface ObjectTargetOpt extends GrpcMetadataMapper, TargetOpt, ApplicableObject {
    default Mapper<BlobInfo.Builder> blobInfo() {
      return Mapper.identity();
    }
  }

  /** Base interface for those Opts which are applicable to Bucket List operations */
  interface BucketListOpt extends GrpcMetadataMapper, ListOpt, ApplicableBucket {}

  /**
   * Base interface for those Opts which are applicable to Bucket Source (get/read/origin
   * relationship) operations
   */
  interface BucketSourceOpt extends GrpcMetadataMapper, SourceOpt, ApplicableBucket {}

  /**
   * Base interface for those Opts which are applicable to Bucket Target (set/write/destination
   * relationship) operations
   */
  interface BucketTargetOpt extends GrpcMetadataMapper, TargetOpt, ApplicableBucket {}

  /** Base interface for those Opts which are applicable to HmacKey List operations */
  interface HmacKeyListOpt extends GrpcMetadataMapper, ListOpt, ApplicableHmacKey {}

  /**
   * Base interface for those Opts which are applicable to HmacKey Source (get/read/origin
   * relationship) operations
   */
  interface HmacKeySourceOpt extends GrpcMetadataMapper, SourceOpt, ApplicableHmacKey {}

  /**
   * Base interface for those Opts which are applicable to HmacKey Target (set/write/destination
   * relationship) operations
   */
  interface HmacKeyTargetOpt extends GrpcMetadataMapper, TargetOpt, ApplicableHmacKey {}

  /**
   * Some Options have a corresponding "SOURCE" version, this interface provide a construct for
   * accessing an projecting those Opts which can be turned into a "SOURCE" version.
   */
  interface ProjectAsSource<O extends Opt> {
    O asSource();
  }

  /**
   * This class extends off {@link ObjectSourceOpt} and {@link ObjectTargetOpt} in order to satisfy
   * some the shimming constraints of the subclasses of {@link OptionShim}.
   *
   * <p>All the methods from these parent interfaces will NEVER be called, and are stubbed simply to
   * satisfy the need for them to be declared. They are stubbed to use identity methods so that if
   * they somehow do ever leak through and are called they won't cause issue for customers.
   *
   * <p>If/when we're able to remove all the {@link Option} classes, this interface should be
   * refactored to remove the inheritance, instead providing an explicit pre-processing phase to opt
   * resolution.
   */
  interface ObjectOptExtractor<O extends Opt> extends Opt, ObjectSourceOpt, ObjectTargetOpt {
    O extractFromBlobInfo(BlobInfo info);

    O extractFromBlobId(BlobId id);

    @Override
    default Mapper<GrpcCallContext> getGrpcMetadataMapper() {
      return Mapper.identity();
    }

    @Override
    default Mapper<GetObjectRequest.Builder> getObject() {
      return Mapper.identity();
    }
  }

  /**
   * This class extends off {@link ObjectSourceOpt} and {@link ObjectTargetOpt} in order to satisfy
   * some the shimming constraints of the subclasses of {@link OptionShim}.
   *
   * <p>All the methods from these parent interfaces will NEVER be called, and are stubbed simply to
   * satisfy the need for them to be declared. They are stubbed to use identity methods so that if
   * they somehow do ever leak through and are called they won't cause issue for customers.
   *
   * <p>If/when we're able to remove all the {@link Option} classes, this interface should be
   * refactored to remove the inheritance, instead providing an explicit pre-processing phase to opt
   * resolution.
   */
  interface BucketOptExtractor<O extends Opt> extends Opt, BucketSourceOpt, BucketTargetOpt {
    O extractFromBucketInfo(BucketInfo info);

    @Override
    default Mapper<GrpcCallContext> getGrpcMetadataMapper() {
      return Mapper.identity();
    }
  }

  /* --
  Factory methods for each of the supported Opts, along with some of their requisite
  compatibility overloads
  -- */

  static Crc32cMatch crc32cMatch(String i) {
    return new Crc32cMatch(i);
  }

  static Delimiter currentDirectory() {
    return new Delimiter("/");
  }

  static DecryptionKey decryptionKey(String s) {
    return new DecryptionKey(new SecretKeySpec(BaseEncoding.base64().decode(s), "AES256"));
  }

  static DecryptionKey decryptionKey(Key k) {
    return new DecryptionKey(k);
  }

  static Delimiter delimiter(String s) {
    return new Delimiter(s);
  }

  static DetectContentType detectContentType() {
    return DetectContentType.INSTANCE;
  }

  static DisableGzipContent disableGzipContent() {
    return new DisableGzipContent(true);
  }

  static GenerationMatch doesNotExist() {
    return new GenerationMatch(0);
  }

  static EncryptionKey encryptionKey(String s) {
    return new EncryptionKey(new SecretKeySpec(BaseEncoding.base64().decode(s), "AES256"));
  }

  static EncryptionKey encryptionKey(Key k) {
    return new EncryptionKey(k);
  }

  static EndOffset endOffset(String s) {
    return new EndOffset(s);
  }

  @Deprecated
  static Fields fields(String s) {
    // TODO: do we care if the string provided is empty?
    return new Fields(s);
  }

  static GenerationMatch generationMatch(long l) {
    return new GenerationMatch(l);
  }

  static GenerationNotMatch generationNotMatch(long l) {
    return new GenerationNotMatch(l);
  }

  static KmsKeyName kmsKeyName(String s) {
    return new KmsKeyName(s);
  }

  static Md5Match md5Match(String s) {
    return new Md5Match(s);
  }

  static MetagenerationMatch metagenerationMatch(long l) {
    return new MetagenerationMatch(l);
  }

  static MetagenerationNotMatch metagenerationNotMatch(long l) {
    return new MetagenerationNotMatch(l);
  }

  static PageSize pageSize(long l) {
    return new PageSize(l);
  }

  static PageToken pageToken(String s) {
    return new PageToken(s);
  }

  static PredefinedAcl predefinedAcl(Storage.PredefinedAcl p) {
    return new PredefinedAcl(p.getEntry());
  }

  static PredefinedDefaultObjectAcl predefinedDefaultObjectAcl(Storage.PredefinedAcl p) {
    return new PredefinedDefaultObjectAcl(p.getEntry());
  }

  static Prefix prefix(String s) {
    return new Prefix(s);
  }

  static ProjectId projectId(String s) {
    return new ProjectId(s);
  }

  static Projection projection(String s) {
    return new Projection(s);
  }

  static RequestedPolicyVersion requestedPolicyVersion(long l) {
    return new RequestedPolicyVersion(l);
  }

  static ReturnRawInputStream returnRawInputStream(boolean b) {
    return new ReturnRawInputStream(b);
  }

  static ServiceAccount serviceAccount(com.google.cloud.storage.ServiceAccount s) {
    return new ServiceAccount(s.getEmail());
  }

  static ShowDeletedKeys showDeletedKeys(boolean b) {
    return new ShowDeletedKeys(b);
  }

  static StartOffset startOffset(String s) {
    return new StartOffset(s);
  }

  static UserProject userProject(String s) {
    return new UserProject(s);
  }

  static VersionsFilter versionsFilter(boolean b) {
    return new VersionsFilter(b);
  }

  @Deprecated
  static GenerationMatchExtractor generationMatchExtractor() {
    return GenerationMatchExtractor.INSTANCE;
  }

  @Deprecated
  static GenerationNotMatchExtractor generationNotMatchExtractor() {
    return GenerationNotMatchExtractor.INSTANCE;
  }

  @Deprecated
  static MetagenerationMatchExtractor metagenerationMatchExtractor() {
    return MetagenerationMatchExtractor.INSTANCE;
  }

  @Deprecated
  static MetagenerationNotMatchExtractor metagenerationNotMatchExtractor() {
    return MetagenerationNotMatchExtractor.INSTANCE;
  }

  @Deprecated
  static Crc32cMatchExtractor crc32cMatchExtractor() {
    return Crc32cMatchExtractor.INSTANCE;
  }

  @Deprecated
  static Md5MatchExtractor md5MatchExtractor() {
    return Md5MatchExtractor.INSTANCE;
  }

  @Deprecated
  static final class Crc32cMatch implements ObjectTargetOpt {
    private static final long serialVersionUID = -8680237667319155418L;
    private final String val;

    private Crc32cMatch(String val) {
      this.val = val;
    }

    @Override
    public Mapper<BlobInfo.Builder> blobInfo() {
      return b -> b.setCrc32c(val);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Crc32cMatch)) {
        return false;
      }
      Crc32cMatch that = (Crc32cMatch) o;
      return Objects.equals(val, that.val);
    }

    @Override
    public int hashCode() {
      return Objects.hash(val);
    }

    @Override
    public String toString() {
      return "Crc32cMatch{val='" + val + "'}";
    }
  }

  /** @see EncryptionKey */
  static final class DecryptionKey extends RpcOptVal<Key> implements ObjectSourceOpt {
    private static final long serialVersionUID = -4583830666730826055L;

    private DecryptionKey(Key val) {
      super(StorageRpc.Option.CUSTOMER_SUPPLIED_KEY, val);
    }

    @Override
    public Mapper<ImmutableMap.Builder<StorageRpc.Option, Object>> mapper() {
      return b ->
          b.put(
              StorageRpc.Option.CUSTOMER_SUPPLIED_KEY,
              BaseEncoding.base64().encode(val.getEncoded()));
    }

    @Override
    public Mapper<GetObjectRequest.Builder> getObject() {
      return todo();
    }
  }

  static final class Delimiter extends RpcOptVal<String> implements ObjectListOpt {
    private static final long serialVersionUID = 8639409337839854122L;

    private Delimiter(String val) {
      super(StorageRpc.Option.DELIMITER, val);
    }
  }

  static final class DisableGzipContent extends RpcOptVal<@NonNull Boolean>
      implements ObjectTargetOpt {
    private static final long serialVersionUID = -8673296387131912651L;

    private DisableGzipContent(boolean val) {
      super(StorageRpc.Option.IF_DISABLE_GZIP_CONTENT, val);
    }
  }

  /** @see DecryptionKey */
  static final class EncryptionKey extends RpcOptVal<Key> implements ObjectTargetOpt {
    private static final long serialVersionUID = 7563566358784847875L;

    private EncryptionKey(Key val) {
      super(StorageRpc.Option.CUSTOMER_SUPPLIED_KEY, val);
    }

    @Override
    public Mapper<ImmutableMap.Builder<StorageRpc.Option, Object>> mapper() {
      return b ->
          b.put(
              StorageRpc.Option.CUSTOMER_SUPPLIED_KEY,
              BaseEncoding.base64().encode(val.getEncoded()));
    }
  }

  /** @see StartOffset */
  static final class EndOffset extends RpcOptVal<String> implements ObjectListOpt {
    private static final long serialVersionUID = -4571919566602569625L;

    private EndOffset(String val) {
      super(StorageRpc.Option.END_OFF_SET, val);
    }
  }

  static final class Fields extends RpcOptVal<String>
      implements ObjectSourceOpt, ObjectListOpt, BucketSourceOpt, BucketListOpt {
    private static final long serialVersionUID = -6861404961336468409L;

    private Fields(String val) {
      super(StorageRpc.Option.FIELDS, val);
    }

    @Override
    public Mapper<GetObjectRequest.Builder> getObject() {
      return Mapper.identity();
    }
  }

  /**
   * @see GenerationNotMatch
   * @see SourceGenerationMatch
   */
  static final class GenerationMatch extends RpcOptVal<@NonNull Long>
      implements ObjectSourceOpt, ObjectTargetOpt, ProjectAsSource<SourceGenerationMatch> {
    private static final long serialVersionUID = -2356341166190897807L;

    private GenerationMatch(long val) {
      super(StorageRpc.Option.IF_GENERATION_MATCH, val);
    }

    @Override
    public Mapper<GetObjectRequest.Builder> getObject() {
      return b -> b.setIfGenerationMatch(val);
    }

    @Override
    public SourceGenerationMatch asSource() {
      return new SourceGenerationMatch(val);
    }
  }

  /**
   * @see GenerationMatch
   * @see SourceGenerationNotMatch
   */
  static final class GenerationNotMatch extends RpcOptVal<@NonNull Long>
      implements ObjectSourceOpt, ObjectTargetOpt, ProjectAsSource<SourceGenerationNotMatch> {
    private static final long serialVersionUID = -6055322302594035351L;

    private GenerationNotMatch(long val) {
      super(StorageRpc.Option.IF_GENERATION_NOT_MATCH, val);
    }

    @Override
    public Mapper<GetObjectRequest.Builder> getObject() {
      return b -> b.setIfGenerationNotMatch(val);
    }

    @Override
    public SourceGenerationNotMatch asSource() {
      return new SourceGenerationNotMatch(val);
    }
  }

  static final class KmsKeyName extends RpcOptVal<String> implements ObjectTargetOpt {
    private static final long serialVersionUID = -3337302773119117013L;

    private KmsKeyName(String val) {
      super(StorageRpc.Option.KMS_KEY_NAME, val);
    }
  }

  @Deprecated
  static final class Md5Match implements ObjectTargetOpt {
    private static final long serialVersionUID = -4497633005169883788L;
    private final String val;

    private Md5Match(String val) {
      this.val = val;
    }

    @Override
    public Mapper<BlobInfo.Builder> blobInfo() {
      return b -> b.setMd5(val);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Md5Match)) {
        return false;
      }
      Md5Match md5Match = (Md5Match) o;
      return Objects.equals(val, md5Match.val);
    }

    @Override
    public int hashCode() {
      return Objects.hash(val);
    }

    @Override
    public String toString() {
      return "Md5Match{val='" + val + "'}";
    }
  }

  /**
   * @see MetagenerationNotMatch
   * @see SourceMetagenerationMatch
   */
  static final class MetagenerationMatch extends RpcOptVal<@NonNull Long>
      implements BucketSourceOpt,
          BucketTargetOpt,
          ObjectSourceOpt,
          ObjectTargetOpt,
          ProjectAsSource<SourceMetagenerationMatch> {
    private static final long serialVersionUID = 5508074897592817147L;

    private MetagenerationMatch(long val) {
      super(StorageRpc.Option.IF_METAGENERATION_MATCH, val);
    }

    @Override
    public Mapper<GetObjectRequest.Builder> getObject() {
      return b -> b.setIfMetagenerationMatch(val);
    }

    @Override
    public SourceMetagenerationMatch asSource() {
      return new SourceMetagenerationMatch(val);
    }
  }

  /**
   * @see MetagenerationMatch
   * @see SourceMetagenerationNotMatch
   */
  static final class MetagenerationNotMatch extends RpcOptVal<@NonNull Long>
      implements BucketSourceOpt,
          BucketTargetOpt,
          ObjectSourceOpt,
          ObjectTargetOpt,
          ProjectAsSource<SourceMetagenerationNotMatch> {
    private static final long serialVersionUID = 6869928996186950306L;

    private MetagenerationNotMatch(long val) {
      super(StorageRpc.Option.IF_METAGENERATION_NOT_MATCH, val);
    }

    @Override
    public Mapper<GetObjectRequest.Builder> getObject() {
      return b -> b.setIfMetagenerationNotMatch(val);
    }

    @Override
    public SourceMetagenerationNotMatch asSource() {
      return new SourceMetagenerationNotMatch(val);
    }
  }

  static final class PageSize extends RpcOptVal<@NonNull Long>
      implements BucketListOpt, ObjectListOpt, HmacKeyListOpt {
    private static final long serialVersionUID = -3510673708181397881L;

    private PageSize(long val) {
      super(StorageRpc.Option.MAX_RESULTS, val);
    }
  }

  static final class PageToken extends RpcOptVal<String>
      implements BucketListOpt, ObjectListOpt, HmacKeyListOpt {
    private static final long serialVersionUID = -542427084922230782L;

    private PageToken(String val) {
      super(StorageRpc.Option.PAGE_TOKEN, val);
    }
  }

  static final class PredefinedAcl extends RpcOptVal<String>
      implements BucketTargetOpt, ObjectTargetOpt {
    private static final long serialVersionUID = 4189588503372535057L;

    private PredefinedAcl(String val) {
      super(StorageRpc.Option.PREDEFINED_ACL, val);
    }
  }

  static final class PredefinedDefaultObjectAcl extends RpcOptVal<String>
      implements BucketTargetOpt {
    private static final long serialVersionUID = 6598022065653572605L;

    private PredefinedDefaultObjectAcl(String val) {
      super(StorageRpc.Option.PREDEFINED_DEFAULT_OBJECT_ACL, val);
    }
  }

  static final class Prefix extends RpcOptVal<String> implements BucketListOpt, ObjectListOpt {
    private static final long serialVersionUID = 155278267048093608L;

    private Prefix(String val) {
      super(StorageRpc.Option.PREFIX, val);
    }
  }

  /**
   * This is a required property of hmac related operations. Preferably, we'd be able to push the
   * defaulting to the creation of a new instance of one of the model objects
   */
  @Deprecated
  static final class ProjectId extends RpcOptVal<String>
      implements HmacKeySourceOpt, HmacKeyTargetOpt, HmacKeyListOpt {
    private static final long serialVersionUID = 1471462503030451598L;

    private ProjectId(String val) {
      super(StorageRpc.Option.PROJECT_ID, val);
    }
  }

  static final class Projection extends RpcOptVal<String> implements BucketTargetOpt {
    private static final long serialVersionUID = -1260415089938322394L;

    private Projection(String val) {
      super(StorageRpc.Option.PROJECTION, val);
    }
  }

  /**
   * @see GenerationMatch
   * @see SourceGenerationNotMatch
   */
  static final class SourceGenerationMatch extends RpcOptVal<@NonNull Long>
      implements ObjectSourceOpt, ObjectTargetOpt {
    private static final long serialVersionUID = 5530465094492461956L;

    private SourceGenerationMatch(@NonNull Long val) {
      super(StorageRpc.Option.IF_SOURCE_GENERATION_MATCH, val);
    }

    @Override
    public Mapper<GetObjectRequest.Builder> getObject() {
      return Mapper.identity();
    }
  }

  /**
   * @see GenerationNotMatch
   * @see SourceGenerationMatch
   */
  static final class SourceGenerationNotMatch extends RpcOptVal<@NonNull Long>
      implements ObjectSourceOpt, ObjectTargetOpt {
    private static final long serialVersionUID = 313414895558156715L;

    private SourceGenerationNotMatch(@NonNull Long val) {
      super(StorageRpc.Option.IF_SOURCE_GENERATION_NOT_MATCH, val);
    }

    @Override
    public Mapper<GetObjectRequest.Builder> getObject() {
      return Mapper.identity();
    }
  }

  /**
   * @see MetagenerationMatch
   * @see SourceMetagenerationNotMatch
   */
  static final class SourceMetagenerationMatch extends RpcOptVal<@NonNull Long>
      implements BucketSourceOpt, BucketTargetOpt, ObjectSourceOpt, ObjectTargetOpt {
    private static final long serialVersionUID = -3643340315457580094L;

    private SourceMetagenerationMatch(@NonNull Long val) {
      super(StorageRpc.Option.IF_SOURCE_METAGENERATION_MATCH, val);
    }

    @Override
    public Mapper<GetObjectRequest.Builder> getObject() {
      return Mapper.identity();
    }
  }

  /**
   * @see MetagenerationNotMatch
   * @see SourceMetagenerationMatch
   */
  static final class SourceMetagenerationNotMatch extends RpcOptVal<@NonNull Long>
      implements BucketSourceOpt, BucketTargetOpt, ObjectSourceOpt, ObjectTargetOpt {
    private static final long serialVersionUID = -6682202521743160969L;

    private SourceMetagenerationNotMatch(@NonNull Long val) {
      super(StorageRpc.Option.IF_SOURCE_METAGENERATION_NOT_MATCH, val);
    }

    @Override
    public Mapper<GetObjectRequest.Builder> getObject() {
      return Mapper.identity();
    }
  }

  static final class RequestedPolicyVersion extends RpcOptVal<@NonNull Long>
      implements BucketSourceOpt {
    private static final long serialVersionUID = 7044856817626952830L;

    private RequestedPolicyVersion(Long val) {
      super(StorageRpc.Option.REQUESTED_POLICY_VERSION, val);
    }
  }

  static final class ReturnRawInputStream extends RpcOptVal<@NonNull Boolean>
      implements ObjectSourceOpt {
    private static final long serialVersionUID = 505293506385742781L;

    private ReturnRawInputStream(boolean val) {
      super(StorageRpc.Option.RETURN_RAW_INPUT_STREAM, val);
    }

    @Override
    public Mapper<GetObjectRequest.Builder> getObject() {
      return todo();
    }
  }

  static final class ServiceAccount extends RpcOptVal<String> implements HmacKeyListOpt {
    private static final long serialVersionUID = 1630581690347694016L;

    private ServiceAccount(String val) {
      super(StorageRpc.Option.SERVICE_ACCOUNT_EMAIL, val);
    }
  }

  static final class SetContentType implements ObjectTargetOpt {
    private static final long serialVersionUID = -5358445952573187492L;
    private final String val;

    private SetContentType(String val) {
      this.val = val;
    }

    @Override
    public Mapper<BlobInfo.Builder> blobInfo() {
      return b -> b.setContentType(val);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof SetContentType)) {
        return false;
      }
      SetContentType that = (SetContentType) o;
      return Objects.equals(val, that.val);
    }

    @Override
    public int hashCode() {
      return Objects.hash(val);
    }

    @Override
    public String toString() {
      return "SetContentType{val='" + val + "'}";
    }
  }

  static final class ShowDeletedKeys extends RpcOptVal<@NonNull Boolean> implements HmacKeyListOpt {
    private static final long serialVersionUID = 6650364639734728488L;

    private ShowDeletedKeys(boolean val) {
      super(StorageRpc.Option.SHOW_DELETED_KEYS, val);
    }
  }

  /** @see EndOffset */
  static final class StartOffset extends RpcOptVal<String> implements ObjectListOpt {
    private static final long serialVersionUID = 7763387382950935370L;

    private StartOffset(String val) {
      super(StorageRpc.Option.START_OFF_SET, val);
    }
  }

  static final class UserProject extends RpcOptVal<String>
      implements BucketSourceOpt,
          BucketTargetOpt,
          BucketListOpt,
          ObjectSourceOpt,
          ObjectTargetOpt,
          ObjectListOpt,
          HmacKeySourceOpt,
          HmacKeyTargetOpt,
          HmacKeyListOpt {
    private static final long serialVersionUID = -3580124936740285695L;

    private UserProject(String val) {
      super(StorageRpc.Option.USER_PROJECT, val);
    }

    @Override
    public Mapper<GrpcCallContext> getGrpcMetadataMapper() {
      return ctx ->
          ctx.withExtraHeaders(ImmutableMap.of("X-Goog-User-Project", ImmutableList.of(val)));
    }

    @Override
    public Mapper<GetObjectRequest.Builder> getObject() {
      return Mapper.identity();
    }
  }

  static final class VersionsFilter extends RpcOptVal<@NonNull Boolean> implements ObjectListOpt {
    private VersionsFilter(boolean val) {
      super(StorageRpc.Option.VERSIONS, val);
    }
  }

  /**
   * Attempt to extract a crc32c value from an Object. If no crc32c value is extracted the produced
   * Opt will be an effective no-op.
   *
   * @see Crc32cMatch
   * @deprecated Use {@link BlobInfo.Builder#setCrc32c(String)}
   */
  @Deprecated
  static final class Crc32cMatchExtractor implements ObjectOptExtractor<ObjectTargetOpt> {
    private static final Crc32cMatchExtractor INSTANCE = new Crc32cMatchExtractor();
    private static final long serialVersionUID = 2222053443431466916L;

    @Deprecated
    private Crc32cMatchExtractor() {}

    @Override
    public ObjectTargetOpt extractFromBlobInfo(BlobInfo info) {
      String crc32c = info.getCrc32c();
      if (crc32c != null) {
        return crc32cMatch(crc32c);
      } else {
        return NoOpObjectTargetOpt.INSTANCE;
      }
    }

    @Override
    public ObjectTargetOpt extractFromBlobId(BlobId id) {
      return NoOpObjectTargetOpt.INSTANCE;
    }

    /** prevent java serialization from using a new instance */
    private Object readResolve() {
      return INSTANCE;
    }
  }

  /**
   * Attempt to determine the content type of an Object based on it's {@link BlobInfo#getName()}. If
   * no name value is extracted, or the value is not a known extension the content type will be
   * {@code application/octet-stream}
   *
   * @see SetContentType
   * @see URLConnection#getFileNameMap()
   * @see FileNameMap
   * @deprecated Use {@link BlobInfo.Builder#setContentType(String)}
   */
  @Deprecated
  static final class DetectContentType implements ObjectOptExtractor<ObjectTargetOpt> {
    private static final DetectContentType INSTANCE = new DetectContentType();
    private static final FileNameMap FILE_NAME_MAP = URLConnection.getFileNameMap();
    private static final long serialVersionUID = 1L;

    @Deprecated
    private DetectContentType() {}

    @Override
    public ObjectTargetOpt extractFromBlobInfo(BlobInfo info) {
      String contentType = info.getContentType();
      if (contentType != null && !contentType.isEmpty()) {
        return NoOpObjectTargetOpt.INSTANCE;
      }

      return detectForName(info.getName());
    }

    @Override
    public ObjectTargetOpt extractFromBlobId(BlobId id) {
      return detectForName(id.getName());
    }

    private ObjectTargetOpt detectForName(String name) {
      if (name != null) {
        String nameLower = name.toLowerCase(Locale.ENGLISH);
        String contentTypeFor = FILE_NAME_MAP.getContentTypeFor(nameLower);
        if (contentTypeFor != null) {
          return new SetContentType(contentTypeFor);
        }
      }
      return new SetContentType("application/octet-stream");
    }
    /** prevent java serialization from using a new instance */
    private Object readResolve() {
      return INSTANCE;
    }
  }

  /**
   * Attempt to extract a generation value from an Object. If no generation value is extracted an
   * {@link IllegalArgumentException} will be thrown.
   *
   * @see GenerationMatch
   * @deprecated Use {@link #generationMatch(long)}
   */
  @Deprecated
  static final class GenerationMatchExtractor implements ObjectOptExtractor<GenerationMatch> {
    private static final GenerationMatchExtractor INSTANCE = new GenerationMatchExtractor();
    private static final long serialVersionUID = -4016709200925410921L;

    @Deprecated
    private GenerationMatchExtractor() {}

    @Override
    public GenerationMatch extractFromBlobInfo(BlobInfo info) {
      Long generation = info.getGeneration();
      checkArgument(generation != null, "Option ifGenerationMatch is missing a value");
      return generationMatch(generation);
    }

    @Override
    public GenerationMatch extractFromBlobId(BlobId id) {
      Long generation = id.getGeneration();
      checkArgument(generation != null, "Option ifGenerationMatch is missing a value");
      return generationMatch(generation);
    }
    /** prevent java serialization from using a new instance */
    private Object readResolve() {
      return INSTANCE;
    }
  }

  /**
   * Attempt to extract a generation value from an Object. If no generation value is extracted an
   * {@link IllegalArgumentException} will be thrown.
   *
   * @see GenerationNotMatch
   * @deprecated Use {@link #generationNotMatch(long)}
   */
  @Deprecated
  static final class GenerationNotMatchExtractor implements ObjectOptExtractor<GenerationNotMatch> {
    private static final GenerationNotMatchExtractor INSTANCE = new GenerationNotMatchExtractor();
    private static final long serialVersionUID = 2419121370772040679L;

    @Deprecated
    private GenerationNotMatchExtractor() {}

    @Override
    public GenerationNotMatch extractFromBlobInfo(BlobInfo info) {
      Long generation = info.getGeneration();
      checkArgument(generation != null, "Option ifGenerationNotMatch is missing a value");
      return generationNotMatch(generation);
    }

    @Override
    public GenerationNotMatch extractFromBlobId(BlobId id) {
      Long generation = id.getGeneration();
      checkArgument(generation != null, "Option ifGenerationNotMatch is missing a value");
      return generationNotMatch(generation);
    }
    /** prevent java serialization from using a new instance */
    private Object readResolve() {
      return INSTANCE;
    }
  }

  /**
   * Attempt to extract an md5 value from an Object. If no md5 value is extracted the produced Opt
   * will be an effective no-op.
   *
   * @see Md5Match
   * @deprecated Use {@link BlobInfo.Builder#setMd5(String)}
   */
  @Deprecated
  static final class Md5MatchExtractor implements ObjectOptExtractor<ObjectTargetOpt> {
    private static final Md5MatchExtractor INSTANCE = new Md5MatchExtractor();
    private static final long serialVersionUID = -227445210555345030L;

    @Deprecated
    private Md5MatchExtractor() {}

    @Override
    public ObjectTargetOpt extractFromBlobInfo(BlobInfo info) {
      String md5 = info.getMd5();
      if (md5 != null) {
        return md5Match(md5);
      } else {
        return NoOpObjectTargetOpt.INSTANCE;
      }
    }

    @Override
    public ObjectTargetOpt extractFromBlobId(BlobId id) {
      return NoOpObjectTargetOpt.INSTANCE;
    }
    /** prevent java serialization from using a new instance */
    private Object readResolve() {
      return INSTANCE;
    }
  }

  /**
   * Attempt to extract a metageneration value from a Bucket or Object. If no metageneration value
   * is extracted an {@link IllegalArgumentException} will be thrown.
   *
   * @see MetagenerationMatch
   * @deprecated Use {@link #metagenerationMatch(long)}
   */
  @Deprecated
  static final class MetagenerationMatchExtractor
      implements ObjectOptExtractor<ObjectTargetOpt>, BucketOptExtractor<MetagenerationMatch> {
    private static final MetagenerationMatchExtractor INSTANCE = new MetagenerationMatchExtractor();
    private static final long serialVersionUID = -9012665484224118046L;

    @Deprecated
    private MetagenerationMatchExtractor() {}

    @Override
    public MetagenerationMatch extractFromBlobInfo(BlobInfo info) {
      Long metageneration = info.getMetageneration();
      checkArgument(metageneration != null, "Option ifMetagenerationMatch is missing a value");
      return metagenerationMatch(metageneration);
    }

    @Override
    public ObjectTargetOpt extractFromBlobId(BlobId id) {
      return NoOpObjectTargetOpt.INSTANCE;
    }

    @Override
    public MetagenerationMatch extractFromBucketInfo(BucketInfo info) {
      Long metageneration = info.getMetageneration();
      checkArgument(metageneration != null, "Option ifMetagenerationMatch is missing a value");
      return metagenerationMatch(metageneration);
    }

    // Both parent interfaces define this method, we need to declare a dis-ambiguous one
    @Override
    public Mapper<GrpcCallContext> getGrpcMetadataMapper() {
      return Mapper.identity();
    }
    /** prevent java serialization from using a new instance */
    private Object readResolve() {
      return INSTANCE;
    }
  }

  /**
   * Attempt to extract a metageneration value from a Bucket or Object. If no metageneration value
   * is extracted an {@link IllegalArgumentException} will be thrown.
   *
   * @see MetagenerationNotMatch
   * @deprecated Use {@link #metagenerationNotMatch(long)}
   */
  @Deprecated
  static final class MetagenerationNotMatchExtractor
      implements ObjectOptExtractor<ObjectTargetOpt>, BucketOptExtractor<MetagenerationNotMatch> {
    private static final MetagenerationNotMatchExtractor INSTANCE =
        new MetagenerationNotMatchExtractor();
    private static final long serialVersionUID = -732561730735045523L;

    @Deprecated
    private MetagenerationNotMatchExtractor() {}

    @Override
    public MetagenerationNotMatch extractFromBlobInfo(BlobInfo info) {
      Long metageneration = info.getMetageneration();
      checkArgument(metageneration != null, "Option ifMetagenerationNotMatch is missing a value");
      return metagenerationNotMatch(metageneration);
    }

    @Override
    public ObjectTargetOpt extractFromBlobId(BlobId id) {
      return NoOpObjectTargetOpt.INSTANCE;
    }

    @Override
    public MetagenerationNotMatch extractFromBucketInfo(BucketInfo info) {
      Long metageneration = info.getMetageneration();
      checkArgument(metageneration != null, "Option ifMetagenerationNotMatch is missing a value");
      return metagenerationNotMatch(metageneration);
    }
    // Both parent interfaces define this method, we need to declare a dis-ambiguous one
    @Override
    public Mapper<GrpcCallContext> getGrpcMetadataMapper() {
      return Mapper.identity();
    }

    /** prevent java serialization from using a new instance */
    private Object readResolve() {
      return INSTANCE;
    }
  }

  /**
   * Internal only implementation of {@link ObjectTargetOpt} which is a No-op.
   *
   * <p>The instance of this class can be returned when a no-op is necessary.
   */
  @VisibleForTesting
  static final class NoOpObjectTargetOpt implements ObjectTargetOpt {
    @VisibleForTesting static final NoOpObjectTargetOpt INSTANCE = new NoOpObjectTargetOpt();
    private static final long serialVersionUID = -3702724179751638748L;

    private NoOpObjectTargetOpt() {}

    @Override
    public Mapper<GrpcCallContext> getGrpcMetadataMapper() {
      return Mapper.identity();
    }

    @Override
    public Mapper<BlobInfo.Builder> blobInfo() {
      return Mapper.identity();
    }

    /** prevent java serialization from using a new instance */
    private Object readResolve() {
      return INSTANCE;
    }
  }

  /**
   * A shim class used by {@link Option} to allow a common parent which isn't part of the public
   * api.
   *
   * <p>{@link Option} itself and all it's subclasses are now obsolete, and should be removed when
   * we're able to remove them from the public api.
   */
  @Deprecated
  abstract static class OptionShim<O extends Opt> implements Serializable {
    private static final long serialVersionUID = -1026813326366179926L;
    private final O opt;

    OptionShim(O opt) {
      this.opt = opt;
    }

    O getOpt() {
      return opt;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof OptionShim)) {
        return false;
      }
      OptionShim<?> that = (OptionShim<?>) o;
      return Objects.equals(opt, that.opt);
    }

    @Override
    public int hashCode() {
      return Objects.hash(opt);
    }

    @Override
    public String toString() {
      return this.getClass().getSimpleName() + "{opt=" + opt + '}';
    }
  }

  /**
   * Base class for those {@link Opt}s which correspond to one or more {@link StorageRpc.Option}
   * keys.
   *
   * @param <T>
   */
  private abstract static class RpcOptVal<T> implements Opt {
    private static final long serialVersionUID = -86698141922923191L;
    protected final StorageRpc.Option key;
    protected final T val;

    private RpcOptVal(StorageRpc.Option key, T val) {
      this.key = key;
      this.val = val;
    }

    public Mapper<ImmutableMap.Builder<StorageRpc.Option, Object>> mapper() {
      return b -> b.put(key, val);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof RpcOptVal)) {
        return false;
      }
      RpcOptVal<?> rpcOptVal = (RpcOptVal<?>) o;
      return Objects.equals(key, rpcOptVal.key) && Objects.equals(val, rpcOptVal.val);
    }

    @Override
    public int hashCode() {
      return Objects.hash(key, val);
    }

    @Override
    public String toString() {
      return this.getClass().getSimpleName() + "{key=" + key + ", val=" + val + '}';
    }
  }

  /**
   * Internal "collection" class to represent a set of {@link Opt}s, and to provide useful
   * transformations to individual mappers or to resolve any extractors providing a new instance
   * without extractors.
   */
  @SuppressWarnings("unchecked")
  static final class Opts<T extends Opt> {

    private final ImmutableList<T> opts;

    private Opts(ImmutableList<T> opts) {
      this.opts = opts;
    }

    /**
     * Resolve any extractors relative to the provided {@link BlobInfo} and return a new instance.
     */
    Opts<T> resolveFrom(BlobInfo info) {
      ImmutableList<T> resolvedOpts =
          opts.stream()
              .map(
                  o -> {
                    if (o instanceof ObjectOptExtractor) {
                      ObjectOptExtractor<T> ex = (ObjectOptExtractor<T>) o;
                      return ex.extractFromBlobInfo(info);
                    } else {
                      return o;
                    }
                  })
              .collect(ImmutableList.toImmutableList());
      return new Opts<>(resolvedOpts);
    }

    /** Resolve any extractors relative to the provided {@link BlobId} and return a new instance. */
    Opts<T> resolveFrom(BlobId id) {
      ImmutableList<T> resolvedOpts =
          opts.stream()
              .map(
                  o -> {
                    if (o instanceof ObjectOptExtractor) {
                      ObjectOptExtractor<T> ex = (ObjectOptExtractor<T>) o;
                      return ex.extractFromBlobId(id);
                    } else {
                      return o;
                    }
                  })
              .collect(ImmutableList.toImmutableList());
      return new Opts<>(resolvedOpts);
    }

    /**
     * Resolve any extractors relative to the provided {@link BucketInfo} and return a new instance.
     */
    Opts<T> resolveFrom(BucketInfo info) {
      ImmutableList<T> resolvedOpts =
          opts.stream()
              .map(
                  o -> {
                    if (o instanceof BucketOptExtractor) {
                      BucketOptExtractor<T> ex = (BucketOptExtractor<T>) o;
                      return ex.extractFromBucketInfo(info);
                    } else {
                      return o;
                    }
                  })
              .collect(ImmutableList.toImmutableList());
      return new Opts<>(resolvedOpts);
    }

    Opts<T> projectAsSource() {
      ImmutableList<T> projectedOpts =
          opts.stream()
              .map(
                  o -> {
                    if (o instanceof ProjectAsSource) {
                      ProjectAsSource<T> p = (ProjectAsSource<T>) o;
                      return p.asSource();
                    } else {
                      return o;
                    }
                  })
              .collect(ImmutableList.toImmutableList());
      return new Opts<>(projectedOpts);
    }

    /**
     * Attempt to construct a {@link StorageRpc} compatible map of {@link StorageRpc.Option}.
     *
     * <p>Validation ensures an absence of duplicate keys, and mutually exclusive keys.
     */
    ImmutableMap<StorageRpc.Option, ?> getRpcOptions() {
      ImmutableMap.Builder<StorageRpc.Option, Object> builder =
          rpcOptionMapper().apply(ImmutableMap.builder());
      return builder.buildOrThrow();
    }

    Mapper<GrpcCallContext> grpcMetadataMapper() {
      return fuseMappers(GrpcMetadataMapper.class, GrpcMetadataMapper::getGrpcMetadataMapper);
    }

    Mapper<GetObjectRequest.Builder> getObjectRequest() {
      return fuseMappers(ObjectSourceOpt.class, ObjectSourceOpt::getObject);
    }

    Mapper<BlobInfo.Builder> blobInfoMapper() {
      return fuseMappers(ObjectTargetOpt.class, ObjectTargetOpt::blobInfo);
    }

    private Mapper<ImmutableMap.Builder<StorageRpc.Option, Object>> rpcOptionMapper() {
      return fuseMappers(RpcOptVal.class, RpcOptVal::mapper);
    }

    private <R, O> Mapper<O> fuseMappers(Class<R> c, Function<R, Mapper<O>> f) {
      return filterTo(c).map(f).reduce(Mapper.identity(), Mapper::andThen);
    }

    @SuppressWarnings("unchecked")
    private <R> Stream<R> filterTo(Class<R> c) {
      // TODO: figure out if there is need for an "isApplicableTo" predicate
      return opts.stream().filter(isInstanceOf(c)).map(x -> (R) x);
    }

    static <T extends Opt> Opts<T> from(T... ts) {
      return new Opts<>(ImmutableList.copyOf(ts));
    }

    /**
     * Given an array of OptionShim, extract the opt from each of them to construct a new instance
     * of {@link Opts}
     */
    static <O extends Opt, T extends OptionShim<O>> Opts<O> unwrap(T[] ts) {
      ImmutableList<O> collect =
          Arrays.stream(ts).map(OptionShim::getOpt).collect(ImmutableList.toImmutableList());
      return new Opts<>(collect);
    }

    /**
     * Given a collection of OptionShim, extract the opt from each of them to construct a new
     * instance of {@link Opts}
     */
    static <O extends Opt, T extends OptionShim<O>> Opts<O> unwrap(Collection<T> ts) {
      ImmutableList<O> collect =
          ts.stream().map(OptionShim::getOpt).collect(ImmutableList.toImmutableList());
      return new Opts<>(collect);
    }

    /** Create a predicate which is able to effectively perform an {@code instanceof} check */
    private static <T> Predicate<T> isInstanceOf(Class<?> c) {
      return t -> c.isAssignableFrom(t.getClass());
    }
  }
}
