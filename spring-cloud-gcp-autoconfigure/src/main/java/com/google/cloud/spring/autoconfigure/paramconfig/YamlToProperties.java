/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.autoconfigure.paramconfig;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.yaml.snakeyaml.Yaml;

public class YamlToProperties {

  record Pair(String key, String value) {}

  static final Yaml yaml = new Yaml();
  TreeMap<String, Map<String, Object>> config;

  @SuppressWarnings({"raw_types", "unchecked"})
  public YamlToProperties(String content) {
    this.config = (TreeMap<String, Map<String, Object>>) yaml.loadAs(content, TreeMap.class);
  }

  @SuppressWarnings({"raw_types", "unchecked"})
  public YamlToProperties(InputStream inputStream) {
    this.config = (TreeMap<String, Map<String, Object>>) yaml.loadAs(inputStream, TreeMap.class);
  }

  @Override
  public String toString() {
    return toProperties(config);
  }

  public Map<String, Object> asProperties() {
    return toPropertyMap(config);
  }

  private static String toProperties(final TreeMap<String, Map<String, Object>> config) {
    StringBuilder sb = new StringBuilder();
    for (final String key : config.keySet()) {
      sb.append(toString(key, config.get(key)));
    }
    return sb.toString();
  }

  private static Map<String, Object> toPropertyMap(final TreeMap<String, Map<String, Object>> config) {
    return config.keySet().stream()
        .map(it -> toString(it, config.get(it)))
        .filter(it -> !it.isEmpty())
        .flatMap(it -> {
          String[] lines = it.split("\n");
          return Arrays.stream(lines)
              .map(line -> {
                String[] propertyParts = line.split("=");
                if (propertyParts.length == 2) {
                  return new Pair(propertyParts[0], propertyParts[1]);
                }
                return null;
              });
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(Pair::key, Pair::value));
  }

  @SuppressWarnings("unchecked")
  private static String toString(final String key, final Object o) {
    StringBuilder sb = new StringBuilder();
    if (o instanceof Map) {
      Map<String, Object> map = (Map<String, Object>) o;
      for (final String mapKey : map.keySet()) {
        if (map.get(mapKey) instanceof Map) {
          sb.append(toString(String.format("%s.%s", key, mapKey), map.get(mapKey)));
        } else {
          sb.append(String.format("%s.%s=%s%n", key, mapKey,
              (null == map.get(mapKey)) ? null : map.get(mapKey).toString()));
        }
      }
    } else {
      sb.append(String.format("%s=%s%n", key, o));
    }
    return sb.toString();
  }
}
