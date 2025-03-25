/*
 * Copyright 2025 Google LLC
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

package com.google.cloud.spring.autoconfigure.parametermanager;

import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.context.config.ConfigDataResource;

public class ParameterManagerConfigDataResource extends ConfigDataResource {

  private final ConfigDataLocation location;

  public ParameterManagerConfigDataResource(ConfigDataLocation location) {
    this.location = location;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ParameterManagerConfigDataResource that)) {
      return false;
    }
    return location.equals(that.location);
  }

  @Override
  public int hashCode() {
    return location.hashCode();
  }

  @Override
  public String toString() {
    return "ParameterManagerConfigDataResource{" + "location=" + location + '}';
  }
}
