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

package com.google.cloud.aiplatform.v1.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for VertexRagDataService client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.aiplatform.v1.vertex-rag-data-service")
public class VertexRagDataServiceSpringProperties implements CredentialsSupplier {
  /** OAuth2 credentials to authenticate and authorize calls to Google Cloud Client Libraries. */
  @NestedConfigurationProperty
  private final Credentials credentials =
      new Credentials("https://www.googleapis.com/auth/cloud-platform");
  /** Quota project to use for billing. */
  private String quotaProjectId;
  /** Number of threads used for executors. */
  private Integer executorThreadCount;
  /** Allow override of retry settings at service level, applying to all of its RPC methods. */
  @NestedConfigurationProperty private Retry retry;
  /**
   * Allow override of retry settings at method-level for getRagCorpus. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getRagCorpusRetry;
  /**
   * Allow override of retry settings at method-level for listRagCorpora. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listRagCorporaRetry;
  /**
   * Allow override of retry settings at method-level for uploadRagFile. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry uploadRagFileRetry;
  /**
   * Allow override of retry settings at method-level for getRagFile. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getRagFileRetry;
  /**
   * Allow override of retry settings at method-level for listRagFiles. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listRagFilesRetry;
  /**
   * Allow override of retry settings at method-level for getRagEngineConfig. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getRagEngineConfigRetry;
  /**
   * Allow override of retry settings at method-level for listLocations. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listLocationsRetry;
  /**
   * Allow override of retry settings at method-level for getLocation. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getLocationRetry;
  /**
   * Allow override of retry settings at method-level for setIamPolicy. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry setIamPolicyRetry;
  /**
   * Allow override of retry settings at method-level for getIamPolicy. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getIamPolicyRetry;
  /**
   * Allow override of retry settings at method-level for testIamPermissions. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry testIamPermissionsRetry;

  @Override
  public Credentials getCredentials() {
    return this.credentials;
  }

  public String getQuotaProjectId() {
    return this.quotaProjectId;
  }

  public void setQuotaProjectId(String quotaProjectId) {
    this.quotaProjectId = quotaProjectId;
  }

  public Integer getExecutorThreadCount() {
    return this.executorThreadCount;
  }

  public void setExecutorThreadCount(Integer executorThreadCount) {
    this.executorThreadCount = executorThreadCount;
  }

  public Retry getRetry() {
    return this.retry;
  }

  public void setRetry(Retry retry) {
    this.retry = retry;
  }

  public Retry getGetRagCorpusRetry() {
    return this.getRagCorpusRetry;
  }

  public void setGetRagCorpusRetry(Retry getRagCorpusRetry) {
    this.getRagCorpusRetry = getRagCorpusRetry;
  }

  public Retry getListRagCorporaRetry() {
    return this.listRagCorporaRetry;
  }

  public void setListRagCorporaRetry(Retry listRagCorporaRetry) {
    this.listRagCorporaRetry = listRagCorporaRetry;
  }

  public Retry getUploadRagFileRetry() {
    return this.uploadRagFileRetry;
  }

  public void setUploadRagFileRetry(Retry uploadRagFileRetry) {
    this.uploadRagFileRetry = uploadRagFileRetry;
  }

  public Retry getGetRagFileRetry() {
    return this.getRagFileRetry;
  }

  public void setGetRagFileRetry(Retry getRagFileRetry) {
    this.getRagFileRetry = getRagFileRetry;
  }

  public Retry getListRagFilesRetry() {
    return this.listRagFilesRetry;
  }

  public void setListRagFilesRetry(Retry listRagFilesRetry) {
    this.listRagFilesRetry = listRagFilesRetry;
  }

  public Retry getGetRagEngineConfigRetry() {
    return this.getRagEngineConfigRetry;
  }

  public void setGetRagEngineConfigRetry(Retry getRagEngineConfigRetry) {
    this.getRagEngineConfigRetry = getRagEngineConfigRetry;
  }

  public Retry getListLocationsRetry() {
    return this.listLocationsRetry;
  }

  public void setListLocationsRetry(Retry listLocationsRetry) {
    this.listLocationsRetry = listLocationsRetry;
  }

  public Retry getGetLocationRetry() {
    return this.getLocationRetry;
  }

  public void setGetLocationRetry(Retry getLocationRetry) {
    this.getLocationRetry = getLocationRetry;
  }

  public Retry getSetIamPolicyRetry() {
    return this.setIamPolicyRetry;
  }

  public void setSetIamPolicyRetry(Retry setIamPolicyRetry) {
    this.setIamPolicyRetry = setIamPolicyRetry;
  }

  public Retry getGetIamPolicyRetry() {
    return this.getIamPolicyRetry;
  }

  public void setGetIamPolicyRetry(Retry getIamPolicyRetry) {
    this.getIamPolicyRetry = getIamPolicyRetry;
  }

  public Retry getTestIamPermissionsRetry() {
    return this.testIamPermissionsRetry;
  }

  public void setTestIamPermissionsRetry(Retry testIamPermissionsRetry) {
    this.testIamPermissionsRetry = testIamPermissionsRetry;
  }
}
