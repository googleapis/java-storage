# Copyright 2018 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""This script is used to synthesize generated parts of this library."""

import os
import synthtool as s
import synthtool.languages.java as java


for library in s.get_staging_dirs():
  # put any special-case replacements here
  if os.path.exists("owl-bot-staging/v2/gapic-google-cloud-storage-v2/src/main/java/com/google/storage/v2/gapic_metadata.json"):
    os.remove("owl-bot-staging/v2/gapic-google-cloud-storage-v2/src/main/java/com/google/storage/v2/gapic_metadata.json")
  s.move(library)

s.remove_staging_dirs()

java.common_templates(excludes=[
  '.kokoro/nightly/integration.cfg',
  '.kokoro/nightly/java11-integration.cfg',
  '.kokoro/nightly/samples.cfg',
  '.kokoro/presubmit/integration.cfg',
  '.kokoro/presubmit/samples.cfg',
  '.kokoro/presubmit/graalvm-native.cfg',
  '.kokoro/presubmit/graalvm-native-17.cfg',
  '.kokoro/requirements.in',
  '.kokoro/requirements.txt',
  '.github/trusted-contribution.yml',
  '.github/workflows/auto-release.yaml',
  'CONTRIBUTING.md',
  'renovate.json'
])
