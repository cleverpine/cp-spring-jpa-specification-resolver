package com.cleverpine.specification.core;

import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.SortDirection;

import javax.persistence.criteria.*;

/**
 * A Specification for an order by clause in a JPA query (sorting by a certain property). This specification extends the {@link PathSpecification} class.
 *
 * @param <T> the type of the root entity
 */
public class OrderBySpecification<T> extends PathSpecification<T> {

    private final SortDirection sortDirection;

    /**
     * Constructs an order by specification with the given path, query context, and sort direction.
     *
     * @param path the path to the attribute being ordered by
     * @param queryContext the query context
     * @param sortDirection the sort direction
     */
    public OrderBySpecification(String path, QueryContext<T> queryContext, SortDirection sortDirection) {
        super(path, queryContext);
        this.sortDirection = sortDirection;
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
