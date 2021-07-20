/*
 * Copyright 2021 Google LLC
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

package com.google.cloud.storage.conformance.retry;

import static java.util.Objects.requireNonNull;

import com.google.cloud.conformance.storage.v1.InstructionList;
import com.google.cloud.conformance.storage.v1.Method;
import java.util.Objects;

final class TestCaseConfig {

  private final Method method;
  private final InstructionList instruction;
  private final boolean preconditionsProvided;
  private final BiFunc func;

  public TestCaseConfig(
      Method method, InstructionList instruction, boolean preconditionsProvided, BiFunc func) {
    this.method = requireNonNull(method, "method must be non null");
    this.instruction = requireNonNull(instruction, "instruction must be non null");
    this.preconditionsProvided = preconditionsProvided;
    this.func = requireNonNull(func, "func must be non null");
  }

  public Method getMethod() {
    return method;
  }

  public InstructionList getInstruction() {
    return instruction;
  }

  public boolean isPreconditionsProvided() {
    return preconditionsProvided;
  }

  public BiFunc getFunc() {
    return func;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TestCaseConfig)) {
      return false;
    }
    TestCaseConfig that = (TestCaseConfig) o;
    return preconditionsProvided == that.preconditionsProvided
        && method.equals(that.method)
        && instruction.equals(that.instruction)
        && func.equals(that.func);
  }

  @Override
  public int hashCode() {
    return Objects.hash(method, instruction, preconditionsProvided, func);
  }
}
