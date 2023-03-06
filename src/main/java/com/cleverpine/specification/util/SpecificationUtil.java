package com.cleverpine.specification.util;

import java.util.Objects;

import static com.cleverpine.specification.util.FilterConstants.ENTITY_ATTRIBUTE_SEPARATOR;
import static com.cleverpine.specification.util.FilterConstants.EMPTY_STRING;

/**
 * Utility class used in the Specification producing.
 */
public final class SpecificationUtil {

    /**
     * Builds the full path to an entity attribute.
     * @param args the parts of the path
     * @return the full path as a string
     */
    public static String buildFullPathToEntityAttribute(String... args) {
        if (Objects.isNull(args)) {
            return EMPTY_STRING;
        }
        return String.join(ENTITY_ATTRIBUTE_SEPARATOR, args);
    }

}
