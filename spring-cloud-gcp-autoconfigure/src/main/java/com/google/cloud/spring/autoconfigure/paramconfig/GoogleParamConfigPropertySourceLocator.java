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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.parametermanager.v1.GetParameterVersionRequest;
import com.google.cloud.parametermanager.v1.ParameterManagerClient;
import com.google.cloud.parametermanager.v1.ParameterVersion;
import com.google.cloud.parametermanager.v1.ParameterVersionName;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Custom {@link PropertySourceLocator} for Google Cloud Runtime Configurator API.
 *
 * @since 1.1
 */
public class GoogleParamConfigPropertySourceLocator implements PropertySourceLocator {

  private static final String PROPERTY_SOURCE_NAME = "spring-cloud-gcp";

  private String projectId;

  private Credentials credentials;

  private String name;

  private String version;

  private String location;

  private boolean enabled;

  public GoogleParamConfigPropertySourceLocator(
      GcpProjectIdProvider projectIdProvider,
      CredentialsProvider credentialsProvider,
      GcpParamConfigProperties gcpConfigProperties)
      throws IOException {
    Assert.notNull(gcpConfigProperties, "Google Config properties must not be null");

    if (gcpConfigProperties.isEnabled()) {
      Assert.notNull(credentialsProvider, "Credentials provider cannot be null");
      Assert.notNull(projectIdProvider, "Project ID provider cannot be null");
      this.credentials =
          gcpConfigProperties.getCredentials().hasKey()
              ? new DefaultCredentialsProvider(gcpConfigProperties).getCredentials()
              : credentialsProvider.getCredentials();
      this.projectId =
          (gcpConfigProperties.getProjectId() != null)
              ? gcpConfigProperties.getProjectId()
              : projectIdProvider.getProjectId();
      Assert.notNull(this.credentials, "Credentials must not be null");

      Assert.notNull(this.projectId, "Project ID must not be null");

      this.name = gcpConfigProperties.getName();
      this.version = gcpConfigProperties.getVersion();
      this.location = gcpConfigProperties.getLocation();
      this.enabled = gcpConfigProperties.isEnabled();
      Assert.notNull(this.name, "Config name must not be null");
      Assert.notNull(this.version, "Config version must not be null");
    }
  }

  String getRemoteEnvironment() throws HttpClientErrorException {
    // Fetch the parameter from the parameter manager
    try (ParameterManagerClient parameterManagerClient = ParameterManagerClient.create()) {
      GetParameterVersionRequest request =
          GetParameterVersionRequest.newBuilder()
              .setName(
                  ParameterVersionName.of(
                          this.projectId, this.location, this.name, this.version)
                      .toString())
              .build();
      ParameterVersion response = parameterManagerClient.getParameterVersion(request);

      if (response == null) {
        throw new HttpClientErrorException(
          HttpStatusCode.valueOf(500), "Invalid response from Parameter Manager API");
      }
      return response.getPayload().getData().toStringUtf8();
    } catch (Exception ex) {
      return null;
    }
  }

  @Override
  public PropertySource<?> locate(Environment environment) {
    if (!this.enabled) {
      return new MapPropertySource(PROPERTY_SOURCE_NAME, Collections.emptyMap());
    }
    Map<String, Object> config;
    try {
      String googleConfigEnvironment = getRemoteEnvironment();
      config = convertStringToMap(googleConfigEnvironment);
      Assert.notNull(googleConfigEnvironment, "Configuration not in expected format.");
    } catch (Exception ex) {
      String message =
          String.format(
              "Error loading configuration for %s/%s_%s", this.projectId, this.name, this.version);
      throw new RuntimeException(message, ex);
    }
    System.out.println("Username from environment: " + environment.getProperty("myapp.username"));
    MapPropertySource test = new MapPropertySource(PROPERTY_SOURCE_NAME, config);
    return test;
  }

  public String getProjectId() {
    return this.projectId;
  }

  public static Map<String, Object> convertStringToMap(String data) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.readValue(data, Map.class);
    } catch (Exception e) {
      throw new RuntimeException("Error parsing JSON", e);
    }
  }
}
