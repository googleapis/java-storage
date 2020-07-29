/*
 * Copyright 2020 Google LLC
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

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class PostPolicyV4Test {

  private void assertNotSameButEqual(Map<String, String> expected, Map<String, String> returned) {
    assertNotSame(expected, returned);
    assertEquals(expected.size(), returned.size());
    for (String key : expected.keySet()) {
      assertEquals(expected.get(key), returned.get(key));
    }
  }

  private static final String[] VALID_FIELDS = {
    "acl",
    "bucket",
    "cache-control",
    "content-disposition",
    "content-encoding",
    "content-type",
    "expires",
    "file",
    "key",
    "policy",
    "success_action_redirect",
    "success_action_status",
    "x-goog-algorithm",
    "x-goog-credential",
    "x-goog-date",
    "x-goog-signature",
  };

  private static Map<String, String> initAllFields() {
    Map<String, String> fields = new HashMap<>();
    for (String key : VALID_FIELDS) {
      fields.put(key, "value of " + key);
    }
    fields.put("x-goog-meta-custom", "value of custom field");
    return Collections.unmodifiableMap(fields);
  }

  private static final Map<String, String> ALL_FIELDS = initAllFields();

  @Test
  public void testPostPolicyV4_of() {
    String url = "http://example.com";
    PostPolicyV4 policy = PostPolicyV4.of(url, ALL_FIELDS);
    assertEquals(url, policy.getUrl());
    assertNotSameButEqual(ALL_FIELDS, policy.getFields());
  }

  @Test
  public void testPostPolicyV4_ofMalformedURL() {
    try {
      PostPolicyV4.of("not a url", new HashMap<String, String>());
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("java.net.MalformedURLException: no protocol: not a url", e.getMessage());
    }
  }

  @Test
  public void testPostPolicyV4_ofInvalidField() {
    Map<String, String> fields = new HashMap<>(ALL_FIELDS);
    fields.put("$file", "file.txt");
    try {
      PostPolicyV4.of("http://google.com", fields);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid key: $file", e.getMessage());
    }
  }

  @Test
  public void testPostFieldsV4_of() {
    PostPolicyV4.PostFieldsV4 fields = PostPolicyV4.PostFieldsV4.of(ALL_FIELDS);
    assertNotSameButEqual(ALL_FIELDS, fields.getFieldsMap());
  }

  @Test
  public void testPostFieldsV4_ofInvalidField() {
    Map<String, String> map = new HashMap<>();
    map.put("$file", "file.txt");
    try {
      PostPolicyV4.PostFieldsV4.of(map);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid key: $file", e.getMessage());
    }
  }

  @Test
  public void testPostConditionsV4_create() {
    PostPolicyV4.PostConditionsV4.Builder builder = PostPolicyV4.PostConditionsV4.newBuilder();

    assertTrue(builder.build().getConditions().isEmpty());

    builder.addBucketCondition(PostPolicyV4.ConditionV4Type.MATCHES, "my-bucket");
    builder.addAclCondition(PostPolicyV4.ConditionV4Type.STARTS_WITH, "pub");
    Set<PostPolicyV4.ConditionV4> conditions = builder.build().getConditions();

    assertEquals(2, conditions.size());
    try {
      conditions.clear();
      fail();
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  @Test
  public void testPostConditionsV4_toString() {
    PostPolicyV4.PostConditionsV4.Builder builder = PostPolicyV4.PostConditionsV4.newBuilder();
    builder.addKeyCondition(PostPolicyV4.ConditionV4Type.MATCHES, "test-object");
    builder.addAclCondition(PostPolicyV4.ConditionV4Type.STARTS_WITH, "public");
    builder.addContentLengthRangeCondition(246, 266);

    Set<String> toStringSet = new HashSet<>();
    for (PostPolicyV4.ConditionV4 conditionV4 : builder.build().getConditions()) {
      toStringSet.add(conditionV4.toString());
    }
    assertEquals(3, toStringSet.size());

    String[] expectedStrings = {
      "[\"eq\", \"$key\", \"test-object\"]",
      "[\"starts-with\", \"$acl\", \"public\"]",
      "[\"content-length-range\", 246, 266]"
    };

    for (String expected : expectedStrings) {
      assertTrue(expected + "/" + toStringSet, toStringSet.contains(expected));
    }
  }
}
