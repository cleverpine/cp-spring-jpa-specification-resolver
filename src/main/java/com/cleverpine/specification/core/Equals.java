package com.cleverpine.specification.core;

import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;

import javax.persistence.criteria.*;

/**
 * The {@link Equals} class represents a single-value specification that checks whether a given property is equal to a
 * specific value. It extends the {@link SingleValueSpecification} abstract class, which means it can handle only one value
 * and implements the {@link #toPredicate(Root, CriteriaQuery, CriteriaBuilder)} method to generate a predicate for the criteria API.
 *
 * @param <T> the type of the root entity
 */
public class Equals<T> extends SingleValueSpecification<T> {

    /**
     * Constructs an instance of the {@link Equals} specification with the given path, value, query context, and value converter.
     *
     * @param path the path of the property to filter on
     * @param value the value to filter by
     * @param queryContext the query context to use for the specification that
     * @param valueConverter the value converter to use for converting values to the appropriate types
     */
    public Equals(String path, String value, QueryContext<T> queryContext, ValueConverter valueConverter) {
        super(path, value, queryContext, valueConverter);
    }

    /**
     * Converts this specification into a JPA criteria API predicate.
     *
     * @param root the root entity
     * @param query the query to which the predicate is added
     * @param criteriaBuilder the builder to use for constructing the predicate
     *
     * @return a predicate that corresponds to this specification
     */
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<Object> propertyPath = buildJoinPathToAttribute(root);
        Class<?> propertyType = propertyPath.getJavaType();
        return criteriaBuilder.equal(propertyPath, getValueConverter().convert(propertyType, getValue()));
    }

}
