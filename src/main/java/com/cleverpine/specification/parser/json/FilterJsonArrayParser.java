package com.cleverpine.specification.parser.json;

import com.cleverpine.specification.exception.InvalidSpecificationException;
import com.cleverpine.specification.item.FilterItem;
import com.cleverpine.specification.item.MultiFilterItem;
import com.cleverpine.specification.item.SingleFilterItem;
import com.cleverpine.specification.parser.SingleFilterParser;
import com.cleverpine.specification.util.FilterOperator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cleverpine.specification.util.FilterConstants.*;

@RequiredArgsConstructor
public class FilterJsonArrayParser implements SingleFilterParser {

    private static final Integer VALID_FILTER_ARGS_COUNT = 3;

    private static final String FILTER_PARAM_TYPE = "Json array";

    private final ObjectMapper objectMapper;

    @Override
    public <T> List<FilterItem<T>> parseFilterParam(String filterParam) {
        if (Objects.isNull(filterParam) || filterParam.isEmpty()) {
            return new ArrayList<>();
        }
        return parseJson(filterParam, new TypeReference<List<List<String>>>() {})
                .stream()
                .map(this::<T>createFilterItem)
                .collect(Collectors.toList());
    }

    private <T> FilterItem<T> createFilterItem(List<String> filterArgs) {
        if (!isFilterItemValid(filterArgs)) {
            throw new InvalidSpecificationException(
                    String.format(INVALID_FILTER_ARGS_COUNT, VALID_FILTER_ARGS_COUNT));
        }

        String filterAttribute = filterArgs.get(0);
        String operatorValue = filterArgs.get(1);
        String value = filterArgs.get(2);

        Optional<FilterOperator> operatorCandidate = FilterOperator.getByValue(operatorValue);
        if (operatorCandidate.isEmpty()) {
            throw new InvalidSpecificationException(String.format(INVALID_FILTER_OPERATOR, operatorValue));
        }

        FilterOperator operator = operatorCandidate.get();
        if (operator.isSingleFilterValue()) {
            return new SingleFilterItem<>(filterAttribute, operator, value);
        } else {
            List<String> values = parseJson(value, new TypeReference<>() {});
            return new MultiFilterItem<>(filterAttribute, operator, values);
        }
    }

    private <T> T parseJson(String json, TypeReference<T> targetType) {
        try {
            return objectMapper.readValue(json, targetType);
        } catch (JsonProcessingException e) {
            throw new InvalidSpecificationException(
                    String.format(INVALID_FILTER_QUERY_PARAMETER, FILTER_PARAM_TYPE));
        }
    }

    private boolean isFilterItemValid(List<String> filterArgs) {
        return filterArgs.size() == VALID_FILTER_ARGS_COUNT;
    }

}
