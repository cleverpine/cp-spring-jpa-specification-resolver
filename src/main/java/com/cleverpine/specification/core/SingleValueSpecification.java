package com.cleverpine.specification.core;

import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;
import lombok.Getter;

/**
 * Abstract class representing a specification with single value to be used in a query.
 *
 * @param <T> the type of the query result
 */
@Getter
public abstract class SingleValueSpecification<T> extends ValueSpecification<T> {

    private final String value;

    /**
     * Constructor for a SingleValueSpecification.
     *
     * @param path the path for the specification
     * @param value the value for the specification
     * @param queryContext the query context for the specification
     * @param valueConverter the value converter for the specification
     */
    public SingleValueSpecification(String path, String value, QueryContext<T> queryContext, ValueConverter valueConverter) {
        super(path, queryContext, valueConverter);
        this.value = value;
    }

}
