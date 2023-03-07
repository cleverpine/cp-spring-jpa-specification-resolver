package com.cleverpine.specification.util;

import com.cleverpine.specification.core.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.cleverpine.specification.util.FilterConstants.*;

/**
 * The {@link FilterOperator} enumeration defines different types of operators that can be used in a filter
 * expression. Each operator has a unique value, a corresponding {@link Specification} implementation
 * class, and a flag indicating if it requires a single filter value or a range of filter values.
 */
@RequiredArgsConstructor
@Getter
public enum FilterOperator {

    EQUAL(EQUAL_OPERATOR, Equals.class, true),
    NOT_EQUAL(NOT_EQUAL_OPERATOR, NotEquals.class, true),
    GREATER_THAN(GREATER_THAN_OPERATOR, GreaterThan.class, true),
    LESS_THAN(LESS_THAN_OPERATOR, LessThan.class, true),
    GREATER_THAN_EQUAL(GREATER_THAN_EQUAL_OPERATOR, GreaterThanOrEquals.class, true),
    LESS_THAN_EQUAL(LESS_THAN_EQUAL_OPERATOR, LessThanOrEquals.class, true),
    LIKE(LIKE_OPERATOR, Like.class, true),
    BETWEEN(BETWEEN_OPERATOR, Between.class,  false),
    IN(IN_OPERATOR, In.class, false);

    private static final Map<String, FilterOperator> VALUES = new HashMap<>();

    static {
        Arrays.stream(FilterOperator.values())
                .forEach(filterOperator -> VALUES.put(filterOperator.getValue(), filterOperator));
    }

    private final String value;

    private final Class<? extends Specification> specificationType;

    private final boolean singleFilterValue;

    /**
     * Returns the {@link FilterOperator} enumeration value corresponding to the provided operator value.
     *
     * @param value The operator value to find the corresponding enumeration value for.
     * @return An {@link Optional} object containing the corresponding {@link FilterOperator} value if one exists,
     *         or an empty {@link Optional} object if one does not.
     */
    public static Optional<FilterOperator> getByValue(String value) {
        return Optional.ofNullable(VALUES.get(value));
    }

}
