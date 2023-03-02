package com.cleverpine.specification.item;

import com.cleverpine.specification.util.FilterOperator;
import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public abstract class FilterItem<T> {

    @NonNull
    private final String attribute;

    @NonNull
    private final FilterOperator operator;

    public String getAttribute() {
        return attribute;
    }

    public abstract Specification<T> createSpecification(QueryContext<T> queryContext, ValueConverter valueConverter);

}
