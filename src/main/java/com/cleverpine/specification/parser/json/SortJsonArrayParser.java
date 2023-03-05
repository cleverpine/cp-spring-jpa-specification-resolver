package com.cleverpine.specification.parser.json;

import com.cleverpine.specification.exception.InvalidSpecificationException;

import com.cleverpine.specification.item.OrderByItem;

import com.cleverpine.specification.parser.SingleSortParser;
import com.cleverpine.specification.util.SortDirection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.cleverpine.specification.util.FilterConstants.*;


@RequiredArgsConstructor
public class SortJsonArrayParser implements SingleSortParser {

    private static final Integer VALID_SORT_ARGS_COUNT = 2;

    private static final String SORT_PARAM_TYPE = "Json array";

    private final ObjectMapper objectMapper;

    @Override
    public <T> List<OrderByItem<T>> parseSortParam(String sortParam) {
        if (Objects.isNull(sortParam) || sortParam.isEmpty()) {
            return new ArrayList<>();
        }

        List<OrderByItem<T>> createdSortItem = new ArrayList<>();
        createdSortItem.add(createSortItem(parseJson(sortParam, new TypeReference<>() {})));

        return createdSortItem;

    }

    private <T> OrderByItem<T> createSortItem(List<String> sortArgs) {
        if (!isSortItemValid(sortArgs)) {
            throw new InvalidSpecificationException(
                    String.format(INVALID_SORT_ARGS_COUNT, VALID_SORT_ARGS_COUNT));
        }

        String sortAttribute = sortArgs.get(0);
        String value = sortArgs.get(1).toUpperCase();

        SortDirection sortDirection = getSortDirection(value);

        return new OrderByItem<>(sortAttribute, sortDirection);

    }

    private SortDirection getSortDirection(String value) {
        try {
            return SortDirection.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidSpecificationException(String.format(INVALID_SORT_DIRECTION, value.toLowerCase()));
        }
    }

    private <T> T parseJson(String json, TypeReference<T> targetType) {
        try {
            return objectMapper.readValue(json, targetType);
        } catch (JsonProcessingException e) {
            throw new InvalidSpecificationException(
                    String.format(INVALID_SORT_QUERY_PARAMETER, SORT_PARAM_TYPE));
        }
    }

    private boolean isSortItemValid(List<String> filterArgs) {
        return filterArgs.size() == VALID_SORT_ARGS_COUNT;
    }
}
