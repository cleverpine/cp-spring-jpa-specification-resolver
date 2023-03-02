package com.cleverpine.specification.core;

import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class MultiValueSpecification<T> extends ValueSpecification<T> {

    private final List<String> values;

    public MultiValueSpecification(String path, List<String> values, QueryContext<T> queryContext, ValueConverter valueConverter) {
        super(path, queryContext, valueConverter);
        this.values = values;
    }
}
