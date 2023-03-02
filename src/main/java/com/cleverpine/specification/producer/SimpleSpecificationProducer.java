package com.cleverpine.specification.producer;

import com.cleverpine.specification.exception.InvalidSpecificationException;
import com.cleverpine.specification.item.FilterItem;
import com.cleverpine.specification.item.OrderByItem;
import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.cleverpine.specification.util.FilterConstants.EMPTY_FILTER_ATTRIBUTE;
import static com.cleverpine.specification.util.FilterConstants.INVALID_FILTER_ATTRIBUTE;

public class SimpleSpecificationProducer {

    public <T> List<Specification<T>> produceFilterSpecifications(Class<?> filterType,
                                                                  List<FilterItem<T>> filterItems,
                                                                  QueryContext<T> queryContext,
                                                                  ValueConverter valueConverter) {
        if (Objects.isNull(filterItems)) {
            return new ArrayList<>();
        }
        return filterItems.stream()
                .map(filterItem ->
                        produceSimpleSpecification(filterItem, filterType, queryContext, valueConverter))
                .collect(Collectors.toList());
    }

    public <T> List<Specification<T>> produceOrderBySpecifications(Class<?> filterType,
                                                                   List<OrderByItem<T>> orderByItems,
                                                                   QueryContext<T> queryContext) {
        if (Objects.isNull(orderByItems)) {
            return new ArrayList<>();
        }
        return orderByItems.stream()
                .map(orderByItem ->
                        produceSimpleSpecification(orderByItem, filterType, queryContext))
                .collect(Collectors.toList());
    }

    private <T> Specification<T> produceSimpleSpecification(FilterItem<T> filterItem, Class<?> filterType, QueryContext<T> queryContext, ValueConverter valueConverter) {
        validateFilterAttribute(filterItem.getAttribute(), filterType);
        return filterItem.createSpecification(queryContext, valueConverter);
    }

    private <T> Specification<T> produceSimpleSpecification(OrderByItem<T> orderByItem, Class<?> filterType, QueryContext<T> queryContext) {
        validateFilterAttribute(orderByItem.getAttribute(), filterType);
        return orderByItem.createSpecification(queryContext);
    }
    private void validateFilterAttribute(String attribute, Class<?> type) {
        if (Objects.isNull(attribute)) {
            throw new InvalidSpecificationException(EMPTY_FILTER_ATTRIBUTE);
        }
        if (!getAllAttributes(type).contains(attribute)) {
            throw new InvalidSpecificationException(
                    String.format(INVALID_FILTER_ATTRIBUTE, attribute, type.getSimpleName()));
        }
    }
    private Set<String> getAllAttributes(Class<?> type) {
        Set<String> fields = Arrays.stream(type.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());
        if (Objects.nonNull(type.getSuperclass())) {
            fields.addAll(getAllAttributes(type.getSuperclass()));
        }
        return fields;
    }

}
