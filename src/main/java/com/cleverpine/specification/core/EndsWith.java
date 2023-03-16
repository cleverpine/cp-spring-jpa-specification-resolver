package com.cleverpine.specification.core;

import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class EndsWith<T> extends SingleValueSpecification<T> {

    /**
     * Constructor for a SingleValueSpecification.
     *
     * @param attributePath  the path for the specification
     * @param value          the value for the specification
     * @param queryContext   the query context for the specification
     * @param valueConverter the value converter for the specification
     */
    public EndsWith(String attributePath, String value, QueryContext<T> queryContext, ValueConverter valueConverter) {
        super(attributePath, value, queryContext, valueConverter);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Expression<String> criteriaExpression = buildCriteriaExpression(root, criteriaBuilder);

        return criteriaBuilder.like(criteriaExpression, "%" + getValue());
    }
}
