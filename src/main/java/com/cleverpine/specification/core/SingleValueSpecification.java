package com.cleverpine.specification.core;

import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;
import lombok.Getter;

@Getter
public abstract class SingleValueSpecification<T> extends ValueSpecification<T> {

    private final String value;

    public SingleValueSpecification(String path, String value, QueryContext<T> queryContext, ValueConverter valueConverter) {
        super(path, queryContext, valueConverter);
        this.value = value;
    }

}
