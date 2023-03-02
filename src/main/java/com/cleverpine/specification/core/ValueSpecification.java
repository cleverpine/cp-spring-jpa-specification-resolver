package com.cleverpine.specification.core;

import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;
import lombok.Getter;

/**
 * This {@link ValueSpecification} is used for all the specifications. It has a {@link ValueConverter} that is used in all the
 * specification implementation to convert the input value to the required one.
 *
 * @param <T> the type of the entity being queried
 */
@Getter
public abstract class ValueSpecification<T> extends PathSpecification<T> {

    private final ValueConverter valueConverter;

    public ValueSpecification(String path, QueryContext<T> queryContext, ValueConverter valueConverter) {
        super(path, queryContext);
        this.valueConverter = valueConverter;
    }

}
