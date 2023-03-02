package com.cleverpine.specification.item;

import com.cleverpine.specification.exception.IllegalSpecificationException;
import com.cleverpine.specification.util.FilterOperator;
import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

import static com.cleverpine.specification.util.FilterConstants.INVALID_SPECIFICATION_CREATION;

public class MultiFilterItem<T> extends FilterItem<T> {

    private final List<String> values;

    public MultiFilterItem(String attribute, FilterOperator operator, @NonNull List<String> values) {
        super(attribute, operator);
        this.values = values;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Specification<T> createSpecification(QueryContext<T> queryContext, ValueConverter valueConverter) {
        String filterPath = queryContext.getPathToEntityField(getAttribute());
        String path = Objects.nonNull(filterPath) ? filterPath : getAttribute();
        Class<? extends Specification> specificationType = getOperator().getSpecificationType();
        try {
            return (Specification<T>) specificationType
                    .getDeclaredConstructor(String.class, List.class, QueryContext.class, ValueConverter.class)
                    .newInstance(path, values, queryContext, valueConverter);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalSpecificationException(
                    String.format(INVALID_SPECIFICATION_CREATION, specificationType.getSimpleName()));
        }
    }

}
