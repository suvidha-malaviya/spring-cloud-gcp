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

package com.google.cloud.spring.data.spanner.repository.query;

import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import java.lang.reflect.Method;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.ValueExpressionDelegate;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;

/**
 * Determines the type of the user's custom-defined Query Methods and instantiates their
 * implementations.
 *
 * @since 1.1
 */
public class SpannerQueryLookupStrategy implements QueryLookupStrategy {

  private final SpannerTemplate spannerTemplate;

  private final SpannerMappingContext spannerMappingContext;

  private final ValueExpressionDelegate valueExpressionDelegate;

  private final SpelExpressionParser expressionParser;

  private QueryMethodEvaluationContextProvider evaluationContextProvider;

  public SpannerQueryLookupStrategy(
      SpannerMappingContext spannerMappingContext,
      SpannerTemplate spannerTemplate,
      ValueExpressionDelegate valueExpressionDelegate,
      SpelExpressionParser expressionParser) {
    Assert.notNull(spannerMappingContext, "A valid SpannerMappingContext is required.");
    Assert.notNull(spannerTemplate, "A valid SpannerTemplate is required.");
    Assert.notNull(valueExpressionDelegate, "A valid ValueExpressionDelegate is required.");
    Assert.notNull(expressionParser, "A valid SpelExpressionParser is required.");
    this.spannerMappingContext = spannerMappingContext;
    this.valueExpressionDelegate = valueExpressionDelegate;
    this.evaluationContextProvider = null;
    this.spannerTemplate = spannerTemplate;
    this.expressionParser = expressionParser;
  }

  /**
   * @deprecated Use {@link
   *     SpannerQueryLookupStrategy#SpannerQueryLookupStrategy(SpannerMappingContext,
   *     SpannerTemplate, ValueExpressionDelegate, SpelExpressionParser)} instead.
   */
  @Deprecated
  public SpannerQueryLookupStrategy(
      SpannerMappingContext spannerMappingContext,
      SpannerTemplate spannerTemplate,
      QueryMethodEvaluationContextProvider evaluationContextProvider,
      SpelExpressionParser expressionParser) {
    Assert.notNull(spannerMappingContext, "A valid SpannerMappingContext is required.");
    Assert.notNull(spannerTemplate, "A valid SpannerTemplate is required.");
    Assert.notNull(evaluationContextProvider, "A valid EvaluationContextProvider is required.");
    Assert.notNull(expressionParser, "A valid SpelExpressionParser is required.");
    this.spannerMappingContext = spannerMappingContext;
    this.valueExpressionDelegate = null;
    this.evaluationContextProvider = evaluationContextProvider;
    this.spannerTemplate = spannerTemplate;
    this.expressionParser = expressionParser;
  }

  Class<?> getEntityType(QueryMethod queryMethod) {
    return queryMethod.getResultProcessor().getReturnedType().getDomainType();
  }

  SpannerQueryMethod createQueryMethod(
      Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
    return new SpannerQueryMethod(method, metadata, factory);
  }

  @Override
  public RepositoryQuery resolveQuery(
      Method method,
      RepositoryMetadata metadata,
      ProjectionFactory factory,
      NamedQueries namedQueries) {
    SpannerQueryMethod queryMethod = createQueryMethod(method, metadata, factory);
    Class<?> entityType = getEntityType(queryMethod);
    Query queryAnnotation = queryMethod.getQueryAnnotation();
    boolean isDml = queryAnnotation != null && queryAnnotation.dmlStatement();

    if (queryAnnotation != null && queryMethod.hasAnnotatedQuery()) {
      return createSqlSpannerQuery(entityType, queryMethod, queryAnnotation.value(), isDml);
    } else if (namedQueries.hasQuery(queryMethod.getNamedQueryName())) {
      String sql = namedQueries.getQuery(queryMethod.getNamedQueryName());
      return createSqlSpannerQuery(entityType, queryMethod, sql, isDml);
    }

    return createPartTreeSpannerQuery(entityType, queryMethod);
  }

  <T> SqlSpannerQuery<T> createSqlSpannerQuery(
      Class<T> entityType, SpannerQueryMethod queryMethod, String sql, boolean isDml) {
    if (this.valueExpressionDelegate != null) {
      return new SqlSpannerQuery<>(
          entityType,
          queryMethod,
          this.spannerTemplate,
          sql,
          this.valueExpressionDelegate,
          this.expressionParser,
          this.spannerMappingContext,
          isDml);
    }
    return new SqlSpannerQuery<>(
        entityType,
        queryMethod,
        this.spannerTemplate,
        sql,
        this.evaluationContextProvider,
        this.expressionParser,
        this.spannerMappingContext,
        isDml);
  }

  <T> PartTreeSpannerQuery<T> createPartTreeSpannerQuery(
      Class<T> entityType, SpannerQueryMethod queryMethod) {
    return new PartTreeSpannerQuery<>(
        entityType, queryMethod, this.spannerTemplate, this.spannerMappingContext);
  }
}
