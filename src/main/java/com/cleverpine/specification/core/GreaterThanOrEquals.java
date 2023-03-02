package com.cleverpine.specification.core;

import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;

import javax.persistence.criteria.*;

public class GreaterThanOrEquals<T> extends SingleValueSpecification<T> {

    public GreaterThanOrEquals(String path, String value, QueryContext<T> queryContext, ValueConverter valueConverter) {
        super(path, value, queryContext, valueConverter);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<? extends Comparable<Object>> propertyPath = buildJoinPathToAttribute(root);
        Class<? extends Comparable<Object>> propertyType = propertyPath.getJavaType();
        return criteriaBuilder.greaterThanOrEqualTo(propertyPath, getValueConverter().convertToComparable(propertyType, getValue()));
    }

}
