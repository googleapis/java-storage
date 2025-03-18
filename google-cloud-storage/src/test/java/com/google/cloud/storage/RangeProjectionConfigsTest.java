/*
 * Copyright 2025 Google LLC
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

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.storage.RangeProjectionConfigs.RangeAsChannel;
import com.google.cloud.storage.RangeProjectionConfigs.RangeAsFutureByteString;
import com.google.cloud.storage.RangeProjectionConfigs.RangeAsFutureBytes;
import com.google.cloud.storage.RangeProjectionConfigs.SeekableChannelConfig;
import org.junit.Test;

public final class RangeProjectionConfigsTest {

  @Test
  public void sameInstanceMustBeReturnedIfNoChange_seekable_hasher_true() {
    SeekableChannelConfig config1 = RangeProjectionConfigs.asSeekableChannel();

    assertThat(config1.getCrc32cValidationEnabled()).isEqualTo(true);

    SeekableChannelConfig config2 = config1.withCrc32cValidationEnabled(true);
    assertThat(config2).isSameInstanceAs(config1);
  }

  @Test
  public void sameInstanceMustBeReturnedIfNoChange_seekable_hasher_false() {
    SeekableChannelConfig config1 =
        RangeProjectionConfigs.asSeekableChannel().withCrc32cValidationEnabled(false);

    assertThat(config1.getCrc32cValidationEnabled()).isEqualTo(false);

    SeekableChannelConfig config2 = config1.withCrc32cValidationEnabled(false);
    assertThat(config2).isSameInstanceAs(config1);
  }

  @Test
  public void differentInstanceWhenChanged_seekable_hasher() {
    SeekableChannelConfig config1 = RangeProjectionConfigs.asSeekableChannel();
    SeekableChannelConfig config2 = config1.withCrc32cValidationEnabled(false);

    assertThat(config2).isNotSameInstanceAs(config1);
  }

  @Test
  public void sameInstanceMustBeReturnedIfNoChange_bytes_hasher_true() {
    RangeAsFutureBytes config1 = RangeProjectionConfigs.asFutureBytes();

    assertThat(config1.getCrc32cValidationEnabled()).isEqualTo(true);

    RangeAsFutureBytes config2 = config1.withCrc32cValidationEnabled(true);
    assertThat(config2).isSameInstanceAs(config1);
  }

  @Test
  public void sameInstanceMustBeReturnedIfNoChange_bytes_hasher_false() {
    RangeAsFutureBytes config1 =
        RangeProjectionConfigs.asFutureBytes().withCrc32cValidationEnabled(false);

    assertThat(config1.getCrc32cValidationEnabled()).isEqualTo(false);

    RangeAsFutureBytes config2 = config1.withCrc32cValidationEnabled(false);
    assertThat(config2).isSameInstanceAs(config1);
  }

  @Test
  public void differentInstanceWhenChanged_bytes_hasher() {
    RangeAsFutureBytes config1 = RangeProjectionConfigs.asFutureBytes();
    RangeAsFutureBytes config2 = config1.withCrc32cValidationEnabled(false);

    assertThat(config2).isNotSameInstanceAs(config1);
  }

  @Test
  public void sameInstanceMustBeReturnedIfNoChange_byteString_hasher_true() {
    RangeAsFutureByteString config1 = RangeProjectionConfigs.asFutureByteString();

    assertThat(config1.getCrc32cValidationEnabled()).isEqualTo(true);

    RangeAsFutureByteString config2 = config1.withCrc32cValidationEnabled(true);
    assertThat(config2).isSameInstanceAs(config1);
  }

  @Test
  public void sameInstanceMustBeReturnedIfNoChange_byteString_hasher_false() {
    RangeAsFutureByteString config1 =
        RangeProjectionConfigs.asFutureByteString().withCrc32cValidationEnabled(false);

    assertThat(config1.getCrc32cValidationEnabled()).isEqualTo(false);

    RangeAsFutureByteString config2 = config1.withCrc32cValidationEnabled(false);
    assertThat(config2).isSameInstanceAs(config1);
  }

  @Test
  public void differentInstanceWhenChanged_byteString_hasher() {
    RangeAsFutureByteString config1 = RangeProjectionConfigs.asFutureByteString();
    RangeAsFutureByteString config2 = config1.withCrc32cValidationEnabled(false);

    assertThat(config2).isNotSameInstanceAs(config1);
  }

  @Test
  public void sameInstanceMustBeReturnedIfNoChange_channel_hasher_true() {
    RangeAsChannel config1 = RangeProjectionConfigs.asChannel();

    assertThat(config1.getCrc32cValidationEnabled()).isEqualTo(true);

    RangeAsChannel config2 = config1.withCrc32cValidationEnabled(true);
    assertThat(config2).isSameInstanceAs(config1);
  }

  @Test
  public void sameInstanceMustBeReturnedIfNoChange_channel_hasher_false() {
    RangeAsChannel config1 = RangeProjectionConfigs.asChannel().withCrc32cValidationEnabled(false);

    assertThat(config1.getCrc32cValidationEnabled()).isEqualTo(false);

    RangeAsChannel config2 = config1.withCrc32cValidationEnabled(false);
    assertThat(config2).isSameInstanceAs(config1);
  }

  @Test
  public void differentInstanceWhenChanged_channel_hasher() {
    RangeAsChannel config1 = RangeProjectionConfigs.asChannel();
    RangeAsChannel config2 = config1.withCrc32cValidationEnabled(false);

    assertThat(config2).isNotSameInstanceAs(config1);
  }
}
