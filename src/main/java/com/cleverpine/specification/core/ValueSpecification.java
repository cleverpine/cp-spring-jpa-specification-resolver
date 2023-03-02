package com.cleverpine.specification.core;

import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;
import lombok.Getter;

@Getter
public abstract class ValueSpecification<T> extends PathSpecification<T> {

    private final ValueConverter valueConverter;

    public ValueSpecification(String path, QueryContext<T> queryContext, ValueConverter valueConverter) {
        super(path, queryContext);
        this.valueConverter = valueConverter;
    }

}
