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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.api.core.ApiFunction;
import com.google.cloud.StringEnumType;
import com.google.cloud.StringEnumValue;
import com.google.common.base.MoreObjects;
import java.io.Serializable;
import java.util.Objects;

/**
 * Access Control List for buckets or blobs.
 *
 * @see <a href="https://cloud.google.com/storage/docs/access-control#About-Access-Control-Lists">
 *     About Access Control Lists</a>
 */
public final class Acl implements Serializable {

  private static final long serialVersionUID = -1000021464049679956L;

  private final Entity entity;
  private final Role role;
  private final String id;
  private final String etag;

  public static final class Role extends StringEnumValue {
    private static final long serialVersionUID = 2067949416720207403L;

    private Role(String constant) {
      super(constant);
    }

    private static final ApiFunction<String, Role> CONSTRUCTOR = Role::new;

    private static final StringEnumType<Role> type = new StringEnumType<>(Role.class, CONSTRUCTOR);

    public static final Role OWNER = type.createAndRegister("OWNER");
    public static final Role READER = type.createAndRegister("READER");
    public static final Role WRITER = type.createAndRegister("WRITER");

    /**
     * Get the Role for the given String constant, and throw an exception if the constant is not
     * recognized.
     */
    public static Role valueOfStrict(String constant) {
      return type.valueOfStrict(constant);
    }

    /** Get the Role for the given String constant, and allow unrecognized values. */
    public static Role valueOf(String constant) {
      return type.valueOf(constant);
    }

    /** Return the known values for Role. */
    public static Role[] values() {
      return type.values();
    }
  }

  /** Builder for {@code Acl} objects. */
  public static class Builder {

    private Entity entity;
    private Role role;
    private String id;
    private String etag;

    private Builder(Entity entity, Role role) {
      this.entity = entity;
      this.role = role;
    }

    private Builder(Acl acl) {
      this.entity = acl.entity;
      this.role = acl.role;
      this.id = acl.id;
      this.etag = acl.etag;
    }

    /** Sets the entity for the ACL object. */
    public Builder setEntity(Entity entity) {
      this.entity = entity;
      return this;
    }

    /** Sets the role to associate to the {@code entity} object. */
    public Builder setRole(Role role) {
      this.role = role;
      return this;
    }

    Builder setId(String id) {
      this.id = id;
      return this;
    }

    Builder setEtag(String etag) {
      this.etag = etag;
      return this;
    }

    /** Creates an {@code Acl} object from this builder. */
    public Acl build() {
      return new Acl(this);
    }
  }

  /** Base class for Access Control List entities. */
  public abstract static class Entity implements Serializable {

    private static final long serialVersionUID = 2321254094152522444L;

    private final Type type;
    private final String value;

    public enum Type {
      DOMAIN,
      GROUP,
      USER,
      PROJECT,
      UNKNOWN
    }

    Entity(Type type, String value) {
      this.type = type;
      this.value = value;
    }

    /** Returns the type of entity. */
    public Type getType() {
      return type;
    }

    /** Returns the entity's value. */
    protected String getValue() {
      return value;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      Entity entity = (Entity) obj;
      return Objects.equals(type, entity.type) && Objects.equals(value, entity.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(type, value);
    }

    @Override
    public String toString() {
      return Conversions.json().entity().encode(this);
    }
  }

  /** Class for ACL Domain entities. */
  public static final class Domain extends Entity {

    private static final long serialVersionUID = 6852649665598880139L;

    /**
     * Creates a domain entity.
     *
     * @param domain the domain associated to this entity
     */
    public Domain(String domain) {
      super(Type.DOMAIN, domain);
    }

    /** Returns the domain associated to this entity. */
    public String getDomain() {
      return getValue();
    }
  }

  /** Class for ACL Group entities. */
  public static final class Group extends Entity {

    private static final long serialVersionUID = 5642929747944714384L;

    /**
     * Creates a group entity.
     *
     * @param email the group email
     */
    public Group(String email) {
      super(Type.GROUP, email);
    }

    /** Returns the group email. */
    public String getEmail() {
      return getValue();
    }
  }

  /** Class for ACL User entities. */
  public static final class User extends Entity {

    private static final long serialVersionUID = -4113630416489429660L;
    static final String ALL_USERS = "allUsers";
    static final String ALL_AUTHENTICATED_USERS = "allAuthenticatedUsers";

    /**
     * Creates a user entity.
     *
     * @param email the user email
     */
    public User(String email) {
      super(Type.USER, email);
    }

    /** Returns the user email. */
    public String getEmail() {
      return getValue();
    }

    public static User ofAllUsers() {
      return new User(ALL_USERS);
    }

    public static User ofAllAuthenticatedUsers() {
      return new User(ALL_AUTHENTICATED_USERS);
    }
  }

  /** Class for ACL Project entities. */
  public static final class Project extends Entity {

    private static final long serialVersionUID = -743189540406339074L;

    private final ProjectRole projectRole;
    private final String projectId;

    public static final class ProjectRole extends StringEnumValue {
      private static final long serialVersionUID = 1284991422168016498L;

      private ProjectRole(String constant) {
        super(constant);
      }

      private static final ApiFunction<String, ProjectRole> CONSTRUCTOR = ProjectRole::new;

      private static final StringEnumType<ProjectRole> type =
          new StringEnumType<>(ProjectRole.class, CONSTRUCTOR);

      public static final ProjectRole OWNERS = type.createAndRegister("OWNERS");
      public static final ProjectRole EDITORS = type.createAndRegister("EDITORS");
      public static final ProjectRole VIEWERS = type.createAndRegister("VIEWERS");

      /**
       * Get the ProjectRole for the given String constant, and throw an exception if the constant
       * is not recognized.
       */
      public static ProjectRole valueOfStrict(String constant) {
        return type.valueOfStrict(constant);
      }

      /** Get the ProjectRole for the given String constant, and allow unrecognized values. */
      public static ProjectRole valueOf(String constant) {
        return type.valueOf(constant);
      }

      /** Return the known values for ProjectRole. */
      public static ProjectRole[] values() {
        return type.values();
      }
    }

    /**
     * Creates a project entity.
     *
     * @param projectRole a role in the project, used to select project's teams
     * @param projectId id of the project
     */
    public Project(ProjectRole projectRole, String projectId) {
      super(Type.PROJECT, projectRole.name().toLowerCase() + "-" + projectId);
      this.projectRole = projectRole;
      this.projectId = projectId;
    }

    /** Returns the role in the project for this entity. */
    public ProjectRole getProjectRole() {
      return projectRole;
    }

    /** Returns the project id for this entity. */
    public String getProjectId() {
      return projectId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Project)) {
        return false;
      }
      if (!super.equals(o)) {
        return false;
      }
      Project project = (Project) o;
      return Objects.equals(projectRole, project.projectRole)
          && Objects.equals(projectId, project.projectId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), projectRole, projectId);
    }
  }

  public static final class RawEntity extends Entity {

    private static final long serialVersionUID = -3049252571732490102L;

    RawEntity(String entity) {
      super(Type.UNKNOWN, entity);
    }
  }

  private Acl(Builder builder) {
    this.entity = checkNotNull(builder.entity);
    this.role = checkNotNull(builder.role);
    this.id = builder.id;
    this.etag = builder.etag;
  }

  /** Returns the entity for this ACL object. */
  public Entity getEntity() {
    return entity;
  }

  /** Returns the role associated to the entity in this ACL object. */
  public Role getRole() {
    return role;
  }

  /** Returns the ID of the ACL entry. */
  public String getId() {
    return id;
  }

  /**
   * Returns HTTP 1.1 Entity tag for the ACL entry.
   *
   * @see <a href="http://tools.ietf.org/html/rfc2616#section-3.11">Entity Tags</a>
   */
  public String getEtag() {
    return etag;
  }

  /** Returns a builder for this {@code Acl} object. */
  public Builder toBuilder() {
    return new Builder(this);
  }

  /**
   * Returns an {@code Acl} object.
   *
   * @param entity the entity for this ACL object
   * @param role the role to associate to the {@code entity} object
   */
  public static Acl of(Entity entity, Role role) {
    return newBuilder(entity, role).build();
  }

  /**
   * Returns a builder for {@code Acl} objects.
   *
   * @param entity the entity for this ACL object
   * @param role the role to associate to the {@code entity} object
   */
  public static Builder newBuilder(Entity entity, Role role) {
    return new Builder(entity, role);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("entity", entity)
        .add("role", role)
        .add("etag", etag)
        .add("id", id)
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(entity, role);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Acl other = (Acl) obj;
    return Objects.equals(this.entity, other.entity)
        && Objects.equals(this.role, other.role)
        && Objects.equals(this.etag, other.etag)
        && Objects.equals(this.id, other.id);
  }
}
