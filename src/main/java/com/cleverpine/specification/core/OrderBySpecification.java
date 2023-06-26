package com.cleverpine.specification.core;

import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.SortDirection;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;

/**
 * A Specification for an order by clause in a JPA query (sorting by a certain property). This specification extends the {@link CriteriaExpressionSpecification} class.
 *
 * @param <T> the type of the root entity
 */
public class OrderBySpecification<T> extends CriteriaExpressionSpecification<T> {

    private final SortDirection sortDirection;

    /**
     * Constructs an order by specification with the given path, query context, and sort direction.
     *
     * @param attributePath the path to the attribute being ordered by
     * @param queryContext  the query context
     * @param sortDirection the sort direction
     */
    public OrderBySpecification(String attributePath, QueryContext<T> queryContext, SortDirection sortDirection) {
        super(attributePath, queryContext);
        this.sortDirection = sortDirection;
    }

    /**
     * Converts this specification into a JPA criteria API predicate.
     *
     * @param root            the root entity
     * @param query           the query to which the predicate is added
     * @param criteriaBuilder the builder to use for constructing the predicate
     * @return a predicate that corresponds to this specification
     */
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Expression<?> criteriaExpression = buildCriteriaExpression(root, criteriaBuilder);
        List<Order> orderClauses = createOrderClauses(criteriaBuilder, criteriaExpression);
        query.orderBy(orderClauses);
        return null;
    }

    private List<Order> createOrderClauses(CriteriaBuilder criteriaBuilder, Expression<?> criteriaExpression) {
        return Arrays.asList(
                criteriaBuilder.asc(criteriaBuilder.selectCase().when(criteriaBuilder.isNull(criteriaExpression), sortDirection.isAscending() ? 1 : -1).otherwise(0)),
                sortDirection.isAscending() ? criteriaBuilder.asc(criteriaExpression) : criteriaBuilder.desc(criteriaExpression)
        );
    }
}
