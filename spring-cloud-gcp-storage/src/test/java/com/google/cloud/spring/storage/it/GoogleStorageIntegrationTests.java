/*
 * Copyright 2017-2018 the original author or authors.
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

package com.google.cloud.spring.storage.it;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.WriteChannel;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.core.UserAgentHeaderProvider;
import com.google.cloud.spring.storage.GoogleStorageProtocolResolver;
import com.google.cloud.spring.storage.GoogleStorageResource;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StreamUtils;

/** Integration for Google Cloud Storage. */

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {GoogleStorageIntegrationTests.GoogleStorageIntegrationTestsConfiguration.class})
@EnabledIfSystemProperty(named = "it.storage", matches = "true")
class GoogleStorageIntegrationTests {

  private static final String CHILD_RELATIVE_NAME = "child";

  @Autowired private Storage storage;

  @Value("gs://${test.integration.storage.bucket}/integration-test")
  private Resource resource;

  private GoogleStorageResource thisResource() {
    return (GoogleStorageResource) this.resource;
  }

  private GoogleStorageResource getChildResource() throws IOException {
    return thisResource().createRelative(CHILD_RELATIVE_NAME);
  }

  private void deleteResource(GoogleStorageResource googleStorageResource) throws IOException {
    if (googleStorageResource.exists()) {
      this.storage.delete(googleStorageResource.getBlob().getBlobId());
    }
  }

  @BeforeEach
  void setUp() throws IOException {
    deleteResource(thisResource());
    deleteResource(getChildResource());
  }

  @Test
  void createAndWriteTest() throws IOException {

    String message = "test message";

    thisResource().createBlob(message.getBytes());

    assertThat(this.resource.exists()).isTrue();

    try (InputStream is = this.resource.getInputStream()) {
      assertThat(StreamUtils.copyToString(is, Charset.defaultCharset())).isEqualTo(message);
    }
  }

  @Test
  void createWithContent() throws IOException {

    String message = "test message";

    try (OutputStream os = thisResource().getOutputStream()) {
      os.write(message.getBytes());
    }

    assertThat(this.resource.exists()).isTrue();

    try (InputStream is = this.resource.getInputStream()) {
      assertThat(StreamUtils.copyToString(is, Charset.defaultCharset())).isEqualTo(message);
    }

    GoogleStorageResource childResource = getChildResource();

    assertThat(childResource.exists()).isFalse();

    try (OutputStream os = childResource.getOutputStream()) {
      os.write(message.getBytes());
    }

    assertThat(childResource.exists()).isTrue();

    try (InputStream is = childResource.getInputStream()) {
      assertThat(StreamUtils.copyToString(is, Charset.defaultCharset())).isEqualTo(message);
    }
  }

  @Test
  @DisabledInAotMode
  void testRestorableStateSerialization() throws IOException {

    String message = "test message";

    try (OutputStream os = thisResource().getOutputStream()) {
      os.write(message.getBytes());
    }

    assertThat(this.resource.exists()).isTrue();

    try (WriteChannel writer = thisResource().getBlob().writer();
         ObjectOutputStream objectOutputStream = new ObjectOutputStream(OutputStream.nullOutputStream())) {
      objectOutputStream.writeObject(writer.capture());
    }
  }

  /** Spring config for the tests. */
  @Configuration
  @PropertySource("application-test.properties")
  @Import(GoogleStorageProtocolResolver.class)
  public static class GoogleStorageIntegrationTestsConfiguration {

    @Bean
    public static Storage storage(
        CredentialsProvider credentialsProvider, GcpProjectIdProvider projectIdProvider)
        throws IOException {
      return StorageOptions.newBuilder()
          .setHeaderProvider(new UserAgentHeaderProvider(GoogleStorageIntegrationTestsConfiguration.class))
          .setCredentials(credentialsProvider.getCredentials())
          .setProjectId(projectIdProvider.getProjectId())
          .build()
          .getService();
    }

    @Bean
    public GcpProjectIdProvider gcpProjectIdProvider() {
      return new DefaultGcpProjectIdProvider();
    }

    @Bean
    public CredentialsProvider credentialsProvider() {
      try {
        return new DefaultCredentialsProvider(Credentials::new);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }
}
