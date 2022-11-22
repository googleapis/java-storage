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

package com.google.cloud.storage.it.runner.annotations;

import com.google.cloud.storage.TransportCompatibility;
import com.google.cloud.storage.TransportCompatibility.Transport;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a class to specify it should be cross run for multiple backend and transport
 * combinations.
 *
 * <p>If you only need a single backend consider using {@link SingleBackend}
 *
 * @see SingleBackend
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CrossRun {

  Backend[] backends();

  Transport[] transports();

  /**
   * Exclude a method from being included in the generated test suite if it's backend and transport
   * match with those defined. When matching, if the empty set is defined as a value it will be
   * treated as ANY rather than NONE.
   *
   * <p>{@link Ignore} Will take precedence if present on a method along with {@link Exclude}.
   */
  @Target({ElementType.METHOD, ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  @interface Exclude {

    Backend[] backends() default {};

    TransportCompatibility.Transport[] transports() default {};
  }

  /**
   * Ignore a method from being ran in the generated test suite if it's backend and transport match
   * with those defined. When matching, if the empty set is defined as a value it will be treated as
   * ANY rather than NONE.
   *
   * <p>{@link Ignore} Will take precedence if present on a method along with {@link Exclude}.
   */
  @Target({ElementType.METHOD, ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  @interface Ignore {

    Backend[] backends() default {};

    TransportCompatibility.Transport[] transports() default {};
  }
}
