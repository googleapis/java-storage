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

import org.junit.Assert;
import org.junit.Test;

public class Crc32cUtilityTest {
    @Test
    public void testCrc32cCombine() {
        long expected = 0xd501dfa8;
        long object1_hash = 0x1b25b8fd;
        long object2_hash = 0x1ba7e51b;
        long object2_size = 4464;
        long combined = Crc32cUtility.crc32cCombine(object1_hash, object2_hash, object2_size);
        Assert.assertEquals(expected, combined);
    }
}
