package com.cleverpine.specification.util;

import java.util.Objects;

import static com.cleverpine.specification.util.FilterConstants.ATTRIBUTE_SEPARATOR;
import static com.cleverpine.specification.util.FilterConstants.EMPTY_STRING;

public final class SpecificationUtil {

    public static String buildFullPathToEntityAttribute(String... args) {
        if (Objects.isNull(args)) {
            return EMPTY_STRING;
        }
        return String.join(ATTRIBUTE_SEPARATOR, args);
    }

}