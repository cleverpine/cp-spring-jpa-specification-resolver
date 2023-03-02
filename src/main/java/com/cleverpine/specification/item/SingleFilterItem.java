package com.cleverpine.specification.item;

import com.cleverpine.specification.exception.IllegalSpecificationException;
import com.cleverpine.specification.util.FilterOperator;
import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import static com.cleverpine.specification.util.FilterConstants.INVALID_SPECIFICATION_CREATION;

/**
 * An implementation of {@link SingleFilterItem} that represents a filter with a single value.
 * @param <T> the type of the entity
 */
public class SingleFilterItem<T> extends FilterItem<T> {

    private final String value;

    /**
     * Constructs a new {@link SingleFilterItem} instance with the specified attribute, operator, and value.
     *
     * @param attribute the name of the attribute to filter on
     * @param operator the operator to use for the filter
     * @param value the value to filter by
     * @throws NullPointerException if the {@code attribute} or {@code value} parameter is {@code null}
     */
    public SingleFilterItem(String attribute, FilterOperator operator, @NonNull String value) {
        super(attribute, operator);
        this.value = value;
    }

    /**
     * Creates a new specification based on specification type.
     *
     * @param queryContext the query context to use for the specification
     * @param valueConverter the value converter to use for the specification
     * @return a new {@link Specification} instance based on this filter item
     * @throws IllegalSpecificationException if the specification cannot be created
     */
    @Override
    @SuppressWarnings("unchecked")
    public Specification<T> createSpecification(QueryContext<T> queryContext, ValueConverter valueConverter) {
        String filterPath = queryContext.getPathToEntityField(getAttribute());
        String path = Objects.nonNull(filterPath) ? filterPath : getAttribute();
        Class<? extends Specification> specificationType = getOperator().getSpecificationType();
        try {
            return (Specification<T>) specificationType
                    .getDeclaredConstructor(String.class, String.class, QueryContext.class, ValueConverter.class)
                    .newInstance(path, value, queryContext, valueConverter);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalSpecificationException(
                    String.format(INVALID_SPECIFICATION_CREATION, specificationType.getSimpleName()));
        }
    }

}
