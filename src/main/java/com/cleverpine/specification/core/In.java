package com.cleverpine.specification.core;

import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;

import javax.persistence.criteria.*;
import java.util.List;

public class In<T> extends MultiValueSpecification<T> {

    public In(String path, List<String> values, QueryContext<T> queryContext, ValueConverter valueConverter) {
        super(path, values, queryContext, valueConverter);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<Object> propertyPath = buildJoinPathToAttribute(root);
        Class<?> propertyType = propertyPath.getJavaType();
        return propertyPath.in(getValueConverter().convert(propertyType, getValues()));
    }

}
