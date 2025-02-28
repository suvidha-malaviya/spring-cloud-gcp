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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

/** Tests for Config bootstrap configuration. */
class GcpParamConfigBootstrapConfigurationTest {

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(GcpParamConfigBootstrapConfiguration.class))
          .withUserConfiguration(TestConfiguration.class);

  @Test
  void testParamConfigurationValueDefaultsAreAsExpected() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.paramconfig.enabled=true")
        .run(
            context -> {
              GcpParamConfigProperties config = context.getBean(GcpParamConfigProperties.class);
              assertThat(config.getName()).isEqualTo("application");
              assertThat(config.getVersion()).isEqualTo("default");
              assertThat(config.isEnabled()).isTrue();
            });
  }

  @Test
  void testParamConfigurationValuesAreCorrectlyLoaded() {
    this.contextRunner
        .withPropertyValues(
            "spring.application.name=myapp",
            "spring.cloud.gcp.paramconfig.version=prod",
            "spring.cloud.gcp.paramconfig.enabled=true",
            "spring.cloud.gcp.paramconfig.project-id=pariah",
            "spring.cloud.gcp.paramconfig.location=us-central1")
        .run(
            context -> {
              GcpParamConfigProperties config = context.getBean(GcpParamConfigProperties.class);
              assertThat(config.getName()).isEqualTo("myapp");
              assertThat(config.getVersion()).isEqualTo("prod");
              assertThat(config.getLocation()).isEqualTo("us-central1");
              assertThat(config.isEnabled()).isTrue();
              assertThat(config.getProjectId()).isEqualTo("pariah");
            });
  }

  @Test
  void testParamConfigurationDisabled() {
    this.contextRunner.run(
        context ->
            assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
                .isThrownBy(() -> context.getBean(GcpParamConfigProperties.class)));
  }

  private static class TestConfiguration {

    @Bean
    public GoogleParamConfigPropertySourceLocator googleParamConfigPropertySourceLocator() {
      return mock(GoogleParamConfigPropertySourceLocator.class);
    }
  }
}
