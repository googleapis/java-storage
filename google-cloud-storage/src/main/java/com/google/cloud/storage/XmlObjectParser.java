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

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.api.client.util.ObjectParser;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

final class XmlObjectParser implements ObjectParser {
  private final XmlMapper xmlMapper;

  @VisibleForTesting
  public XmlObjectParser(XmlMapper xmlMapper) {
    this.xmlMapper = xmlMapper;
  }

  @Override
  public <T> T parseAndClose(InputStream in, Charset charset, Class<T> dataClass)
      throws IOException {
    return parseAndClose(new InputStreamReader(in, charset), dataClass);
  }

  @Override
  public Object parseAndClose(InputStream in, Charset charset, Type dataType) throws IOException {
    throw new UnsupportedOperationException(
        "XmlObjectParse#"
            + CrossTransportUtils.fmtMethodName(
                "parseAndClose", InputStream.class, Charset.class, Type.class));
  }

  @Override
  public <T> T parseAndClose(Reader reader, Class<T> dataClass) throws IOException {
    try (Reader r = reader) {
      return xmlMapper.readValue(r, dataClass);
    }
  }

  @Override
  public Object parseAndClose(Reader reader, Type dataType) throws IOException {
    throw new UnsupportedOperationException(
        "XmlObjectParse#"
            + CrossTransportUtils.fmtMethodName("parseAndClose", Reader.class, Type.class));
  }
}
