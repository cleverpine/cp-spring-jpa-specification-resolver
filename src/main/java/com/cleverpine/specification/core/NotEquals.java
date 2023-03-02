package com.cleverpine.specification.core;

import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;

import javax.persistence.criteria.*;

public class NotEquals<T> extends SingleValueSpecification<T> {

    public NotEquals(String path, String value, QueryContext<T> queryContext, ValueConverter valueConverter) {
        super(path, value, queryContext, valueConverter);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<Object> propertyPath = buildJoinPathToAttribute(root);
        Class<?> propertyType = propertyPath.getJavaType();
        return criteriaBuilder.notEqual(propertyPath, getValueConverter().convert(propertyType, getValue()));
    }

}
