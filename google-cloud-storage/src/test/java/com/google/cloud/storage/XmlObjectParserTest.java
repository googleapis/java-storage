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
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class XmlObjectParserTest {

  @Mock private XmlMapper xmlMapper;

  private XmlObjectParser xmlObjectParser;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    xmlObjectParser = new XmlObjectParser(xmlMapper);
  }

  @Test
  public void testParseAndClose() throws IOException {
    InputStream in = new ByteArrayInputStream("<Test/>".getBytes(StandardCharsets.UTF_8));
    TestXmlObject expected = new TestXmlObject();
    when(xmlMapper.readValue(in, TestXmlObject.class)).thenReturn(expected);
    TestXmlObject actual = xmlObjectParser.parseAndClose(in, StandardCharsets.UTF_8, TestXmlObject.class);
    assertThat(actual).isSameInstanceAs(expected);
  }

  private static class TestXmlObject {}
}
