package com.cleverpine.specification.item;


import com.cleverpine.specification.core.OrderBySpecification;
import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.SortDirection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public class OrderByItem<T> {

    private final String attribute;

    private final SortDirection direction;

    public Specification<T> createSpecification(QueryContext<T> queryContext) {
        String fieldFullPath = queryContext.getPathToEntityField(getAttribute());
        String path = Objects.nonNull(fieldFullPath) ? fieldFullPath : getAttribute();
        return new OrderBySpecification<>(path, queryContext, direction);
    }
}
