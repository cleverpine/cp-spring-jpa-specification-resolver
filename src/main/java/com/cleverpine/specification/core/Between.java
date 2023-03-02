package com.cleverpine.specification.core;

import com.cleverpine.specification.exception.InvalidSpecificationException;
import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.Objects;

import static com.cleverpine.specification.util.FilterConstants.INVALID_VALUES_COUNT;

public class Between<T> extends MultiValueSpecification<T> {

    private static final Integer BETWEEN_VALUES_COUNT = 2;

    public Between(String path, List<String> values, QueryContext<T> queryContext, ValueConverter valueConverter) {
        super(path, values, queryContext, valueConverter);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<String> values = getValues();
        if (Objects.isNull(values) || values.size() != BETWEEN_VALUES_COUNT) {
            throw new InvalidSpecificationException(
                    String.format(INVALID_VALUES_COUNT, this.getClass().getSimpleName(), BETWEEN_VALUES_COUNT));
        }

        Path<? extends Comparable<Object>> propertyPath = buildJoinPathToAttribute(root);
        Class<? extends Comparable<Object>> propertyType = propertyPath.getJavaType();

        Comparable<Object> firstValue = getValueConverter().convertToComparable(propertyType, values.get(0));
        Comparable<Object> secondValue = getValueConverter().convertToComparable(propertyType, values.get(1));
        return criteriaBuilder.between(propertyPath, firstValue, secondValue);
    }

}
