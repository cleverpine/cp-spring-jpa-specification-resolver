package com.cleverpine.specification.util;

import com.cleverpine.specification.item.JoinItem;

import javax.persistence.criteria.Join;
import java.util.HashMap;
import java.util.Map;

public class QueryContext<T> {

    private final Map<String, Join<?, ?>> joinsByAlias = new HashMap<>();

    private final SpecificationQueryConfig.JoinConfig<T> joinConfig;

    private final SpecificationQueryConfig.AttributePathConfig<T> attributePathConfig;

    private boolean entityDistinctRequired;

    public QueryContext(SpecificationQueryConfig.JoinConfig<T> joinConfig, SpecificationQueryConfig.AttributePathConfig<T> attributePathConfig) {
        this.joinConfig = joinConfig;
        this.attributePathConfig = attributePathConfig;
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

    public String getPathToEntityField(String attribute) {
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

}
