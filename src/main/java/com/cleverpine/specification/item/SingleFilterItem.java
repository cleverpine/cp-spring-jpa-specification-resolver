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

public class SingleFilterItem<T> extends FilterItem<T> {

    private final String value;

    public SingleFilterItem(String attribute, FilterOperator operator, @NonNull String value) {
        super(attribute, operator);
        this.value = value;
    }

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
