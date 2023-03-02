package com.cleverpine.specification.core;

import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;

import javax.persistence.criteria.*;

public class Like<T> extends SingleValueSpecification<T> {

    public Like(String path, String value, QueryContext<T> queryContext, ValueConverter valueConverter) {
        super(path, value, queryContext, valueConverter);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<String> propertyPath = buildJoinPathToAttribute(root);
        return criteriaBuilder.like(propertyPath.as(String.class), "%" + getValue() + "%");
    }

}
