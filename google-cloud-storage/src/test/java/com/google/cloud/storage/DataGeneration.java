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

package com.google.cloud.storage;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public final class DataGeneration implements TestRule {

  private final Random rand;

  public DataGeneration(Random rand) {
    this.rand = rand;
  }

  public ByteBuffer randByteBuffer(int limit) {
    ByteBuffer b = ByteBuffer.allocate(limit);
    fillByteBuffer(b);
    return b;
  }

  public void fillByteBuffer(ByteBuffer b) {
    while (b.position() < b.limit()) {
      int i = rand.nextInt('z');
      char c = (char) i;
      if (Character.isLetter(c) || Character.isDigit(c)) {
        b.put(Character.toString(c).getBytes(StandardCharsets.UTF_8));
      }
    }
    b.position(0);
  }

  @Override
  public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        base.evaluate();
      }
    };
  }
}
