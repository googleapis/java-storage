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

import com.google.api.core.InternalApi;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;

/**
 * A collection of general utility functions providing convenience facilities.
 *
 * <p>Nothing in here should be Storage specific. Anything Storage specific should go in an
 * appropriately named and scoped class.
 */
@InternalApi
final class Utils {

  private Utils() {}

  /**
   * If the value provided as {@code t} is non-null, consume it via {@code c}.
   *
   * <p>Helper method to allow for more terse expression of:
   *
   * <pre>{@code
   * if (t != null) {
   *   x.setT(t);
   * }
   * }</pre>
   */
  @InternalApi
  static <T> void ifNonNull(@Nullable T t, Consumer<T> c) {
    if (t != null) {
      c.accept(t);
    }
  }

  /**
   * If the value provided as {@code t} is non-null, transform it using {@code map} and consume it
   * via {@code c}.
   *
   * <p>Helper method to allow for more terse expression of:
   *
   * <pre>{@code
   * if (t != null) {
   *   x.setT(map.apply(t));
   * }
   * }</pre>
   */
  @InternalApi
  static <T1, T2> void ifNonNull(@Nullable T1 t, Function<T1, T2> map, Consumer<T2> c) {
    if (t != null) {
      c.accept(map.apply(t));
    }
  }

  /**
   * Convenience method to "lift" a method reference to a {@link Function}.
   *
   * <p>While a method reference can be pass as an argument to a method which expects a {@code
   * Function} it does not then allow calling {@link Function#andThen(Function) #andThen(Function)}.
   * This method forces the method reference to be a {@code Function} thereby allowing {@code
   * #andThen} composition.
   */
  @InternalApi
  static <T1, T2> Function<T1, T2> lift(Function<T1, T2> f) {
    return f;
  }
}
