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

package com.google.cloud.spring.autoconfigure.paramconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.StandardEnvironment;

/** Tests for the config property source locator. */
class GoogleParamConfigPropertySourceLocatorTest {

  private GcpParamConfigProperties gcpParamConfigProperties;

  private Map<String, Object> expectedProperties;

  private GoogleParamConfigPropertySourceLocator googleParamConfigPropertySourceLocator;

  private GcpProjectIdProvider projectIdProvider;

  private CredentialsProvider credentialsProvider;

  @BeforeEach
  void setUp() {
    this.gcpParamConfigProperties = mock(GcpParamConfigProperties.class);
    when(this.gcpParamConfigProperties.getName()).thenReturn("test");
    when(this.gcpParamConfigProperties.isEnabled()).thenReturn(true);
    com.google.cloud.spring.core.Credentials configCredentials =
        mock(com.google.cloud.spring.core.Credentials.class);
    when(this.gcpParamConfigProperties.getCredentials()).thenReturn(configCredentials);
    when(this.gcpParamConfigProperties.getVersion()).thenReturn("default");
    when(this.gcpParamConfigProperties.getLocation()).thenReturn("global");
    this.expectedProperties = new HashMap<>();
    this.expectedProperties.put("property-int", 10);
    this.expectedProperties.put("property-bool", true);
    this.projectIdProvider = () -> "projectid";
    this.credentialsProvider = () -> mock(Credentials.class);
  }

  /*@Test
  void locateReturnsMapPropertySource() throws Exception {
    GoogleParamConfigEnvironment googleConfigEnvironment = mock(GoogleParamConfigEnvironment.class);
    when(googleConfigEnvironment.getConfig()).thenReturn(this.expectedProperties);
    this.googleConfigPropertySourceLocator =
        spy(
            new GoogleConfigPropertySourceLocator(
                this.projectIdProvider, this.credentialsProvider, this.gcpConfigProperties));
    doReturn(googleConfigEnvironment)
        .when(this.googleConfigPropertySourceLocator)
        .getRemoteEnvironment();
    PropertySource<?> propertySource =
        this.googleConfigPropertySourceLocator.locate(new StandardEnvironment());
    assertThat(propertySource.getName()).isEqualTo("spring-cloud-gcp");
    assertThat(propertySource.getProperty("property-int")).isEqualTo(10);
    assertThat((Boolean) propertySource.getProperty("property-bool")).isTrue();
    assertThat(this.googleConfigPropertySourceLocator.getProjectId()).isEqualTo("projectid");
  }*/

  /*@Test
  void locateReturnsMapPropertySource_disabled() throws Exception {
    when(this.gcpParamConfigProperties.isEnabled()).thenReturn(false);
    GoogleParamConfigEnvironment googleConfigEnvironment = mock(GoogleParamConfigEnvironment.class);
    when(googleConfigEnvironment.getConfig()).thenReturn(this.expectedProperties);
    this.googleParamConfigPropertySourceLocator =
        spy(
            new GoogleParamConfigPropertySourceLocator(
                this.projectIdProvider, this.credentialsProvider, this.gcpParamConfigProperties));
    doReturn(googleConfigEnvironment)
        .when(this.googleParamConfigPropertySourceLocator)
        .getRemoteEnvironment();
    PropertySource<?> propertySource =
        this.googleParamConfigPropertySourceLocator.locate(new StandardEnvironment());
    assertThat(propertySource.getName()).isEqualTo("spring-cloud-gcp");
    assertThat(((MapPropertySource) propertySource).getPropertyNames()).isEmpty();
  }*/

  @Test
  void disabledPropertySourceReturnsNull() throws Exception {
    when(this.gcpParamConfigProperties.isEnabled()).thenReturn(false);
    this.googleParamConfigPropertySourceLocator =
        spy(
            new GoogleParamConfigPropertySourceLocator(
                this.projectIdProvider, this.credentialsProvider, this.gcpParamConfigProperties));
    this.googleParamConfigPropertySourceLocator.locate(new StandardEnvironment());
    verify(this.googleParamConfigPropertySourceLocator, never()).getRemoteEnvironment();
  }

  @Test
  void disabledPropertySourceAvoidChecks() throws IOException {
    when(this.gcpParamConfigProperties.isEnabled()).thenReturn(false);
    this.googleParamConfigPropertySourceLocator =
        spy(new GoogleParamConfigPropertySourceLocator(null, null, this.gcpParamConfigProperties));
    this.googleParamConfigPropertySourceLocator.locate(new StandardEnvironment());
    verify(this.googleParamConfigPropertySourceLocator, never()).getRemoteEnvironment();
  }

  @Test
  void testProjectIdInConfigProperties() throws IOException {
    when(this.gcpParamConfigProperties.getProjectId()).thenReturn("pariah");
    this.googleParamConfigPropertySourceLocator =
        new GoogleParamConfigPropertySourceLocator(
            this.projectIdProvider, this.credentialsProvider, this.gcpParamConfigProperties);

    assertThat(this.googleParamConfigPropertySourceLocator.getProjectId()).isEqualTo("pariah");
  }
}
