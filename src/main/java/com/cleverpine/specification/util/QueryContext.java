package com.cleverpine.specification.util;

import com.cleverpine.specification.expression.SpecificationExpression;
import com.cleverpine.specification.item.JoinItem;

import javax.persistence.criteria.Join;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class to hold context information required to build JPA criteria queries using
 * {@link SpecificationExpression}. It provides access to {@link Join} objects and attribute paths
 * configured in {@link SpecificationQueryConfig} to be used while building the criteria query.
 *
 * @param <T> the type of the root entity
 */
public class QueryContext<T> {

    private final Map<String, Join<?, ?>> joinsByAlias = new HashMap<>();

    private final SpecificationQueryConfig.JoinConfig<T> joinConfig;

    private final SpecificationQueryConfig.AttributePathConfig<T> attributePathConfig;

    private final SpecificationQueryConfig.CustomExpressionConfig<T> customExpressionConfig;

    private boolean entityDistinctRequired;

    public QueryContext(SpecificationQueryConfig<T> specificationQueryConfig) {
        this.joinConfig = specificationQueryConfig.getJoinConfig();
        this.attributePathConfig = specificationQueryConfig.getAttributePathConfig();
        this.customExpressionConfig = specificationQueryConfig.getCustomExpressionConfig();
    }

    public void addJoin(String alias, Join<?, ?> join) {
        joinsByAlias.put(alias, join);
    }

    public boolean isJoinPresent(String alias) {
        return joinsByAlias.containsKey(alias);
    }

    public Join<?, ?> getJoinByAlias(String alias) {
        return joinsByAlias.get(alias);
    }

    public String getPathToEntityAttribute(String attribute) {
        return attributePathConfig.getPathToEntityAttribute(attribute);
    }

    public JoinItem getJoinItemByAlias(String alias) {
        return joinConfig.getJoinItemByAlias(alias);
    }

    public void clearState() {
        joinsByAlias.clear();
        setEntityDistinctRequired(false);
    }

    public boolean isEntityDistinctRequired() {
        return entityDistinctRequired;
    }

    public void setEntityDistinctRequired(boolean entityDistinctRequired) {
        this.entityDistinctRequired = entityDistinctRequired;
    }

    @SuppressWarnings("rawtypes")
    public Class<? extends SpecificationExpression> getCustomSpecificationExpressionByAttribute(String attribute) {
        return customExpressionConfig.getCustomSpecificationExpressionByAttribute(attribute);
    }
}
