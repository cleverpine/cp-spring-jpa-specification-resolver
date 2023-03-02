package com.cleverpine.specification.core;

import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.SortDirection;

import javax.persistence.criteria.*;

public class OrderBySpecification<T> extends PathSpecification<T> {

    private final SortDirection sortDirection;

    public OrderBySpecification(String path, QueryContext<T> queryContext, SortDirection sortDirection) {
        super(path, queryContext);
        this.sortDirection = sortDirection;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<?> propertyPath = getQueryContext().isEntityDistinctRequired()
                ? buildFetchPathToAttribute(root)
                : buildJoinPathToAttribute(root);
        Order orderClause = createOrderClause(criteriaBuilder, propertyPath);
        query.orderBy(orderClause);
        return null;
    }

    private Order createOrderClause(CriteriaBuilder criteriaBuilder, Path<?> path) {
        if (sortDirection.isAscending()) {
            return criteriaBuilder.asc(path);
        }
        return criteriaBuilder.desc(path);
    }

}
