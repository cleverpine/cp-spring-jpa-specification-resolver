package com.cleverpine.specification.util;

import com.cleverpine.specification.exception.InvalidSpecificationException;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cleverpine.specification.util.FilterConstants.INVALID_VALUE_FOR_CLASS;
import static com.cleverpine.specification.util.FilterConstants.NULL_VALUE;

public class ValueConverter {

    private final Map<Class<?>, Function<String, Object>> valueParsers = new HashMap<>();

    public ValueConverter() {
        valueParsers.put(Integer.class, this::parseToInt);
        valueParsers.put(int.class, this::parseToInt);
        valueParsers.put(Long.class, this::parseToLong);
        valueParsers.put(long.class, this::parseToLong);
        valueParsers.put(Double.class, this::parseToDouble);
        valueParsers.put(double.class, this::parseToDouble);
        valueParsers.put(Float.class, this::parseToFloat);
        valueParsers.put(float.class, this::parseToFloat);
        valueParsers.put(BigDecimal.class, this::parseToBigDecimal);
        valueParsers.put(UUID.class, this::parseToUUID);
        valueParsers.put(ZonedDateTime.class, this::parseToZonedDateTime);
    }

    public Object convert(Class<?> type, String value) {
        if (Objects.isNull(value)) {
            throw new InvalidSpecificationException(NULL_VALUE);
        }
        Function<String, Object> parseFunction = valueParsers.get(type);
        if (Objects.isNull(parseFunction)) {
            return value;
        }
        return parseFunction.apply(value);
    }

    @SuppressWarnings("unchecked")
    public Comparable<Object> convertToComparable(Class<?> type, String value) {
        if (!Comparable.class.isAssignableFrom(type)) {
            throw new InvalidSpecificationException(
                    String.format(INVALID_VALUE_FOR_CLASS, value, type.getSimpleName()));
        }
        Object convertedValue = convert(type, value);
        return (Comparable<Object>) convertedValue;
    }

    public List<Object> convert(Class<?> type, List<String> values) {
        return values.stream()
                .map(value -> this.convert(type, value))
                .collect(Collectors.toList());
    }

    public void addCustomValueConverters(Map<Class<?>, Function<String, Object>> converterMap) {
        valueParsers.putAll(converterMap);
    }

    private UUID parseToUUID(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new InvalidSpecificationException(
                    String.format(INVALID_VALUE_FOR_CLASS, value, UUID.class.getSimpleName()));
        }
    }

    private ZonedDateTime parseToZonedDateTime(String value) {
        try {
            return ZonedDateTime.parse(value);
        } catch (DateTimeParseException ex) {
            throw new InvalidSpecificationException(
                    String.format(INVALID_VALUE_FOR_CLASS, value, ZonedDateTime.class.getSimpleName()));
        }
    }

    private Integer parseToInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new InvalidSpecificationException(
                    String.format(INVALID_VALUE_FOR_CLASS, value, Integer.class.getSimpleName()));
        }
    }

    private Long parseToLong(String value) {
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException ex) {
            throw new InvalidSpecificationException(
                    String.format(INVALID_VALUE_FOR_CLASS, value, Long.class.getSimpleName()));
        }
    }

    private Double parseToDouble(String value) {
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException ex) {
            throw new InvalidSpecificationException(
                    String.format(INVALID_VALUE_FOR_CLASS, value, Double.class.getSimpleName()));
        }
    }

    private Float parseToFloat(String value) {
        try {
            return Float.valueOf(value);
        } catch (NumberFormatException ex) {
            throw new InvalidSpecificationException(
                    String.format(INVALID_VALUE_FOR_CLASS, value, Float.class.getSimpleName()));
        }
    }

    private BigDecimal parseToBigDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException ex) {
            throw new InvalidSpecificationException(
                    String.format(INVALID_VALUE_FOR_CLASS, value, BigDecimal.class.getSimpleName()));
        }
    }

}
