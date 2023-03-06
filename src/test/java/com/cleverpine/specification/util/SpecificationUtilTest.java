package com.cleverpine.specification.util;

import org.junit.jupiter.api.Test;

import static com.cleverpine.specification.util.FilterConstants.ENTITY_ATTRIBUTE_SEPARATOR;
import static com.cleverpine.specification.util.FilterConstants.EMPTY_STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpecificationUtilTest {

    @Test
    void buildFullPathToEntityAttribute_onNull_shouldReturnEmptyString() {
        String actual = SpecificationUtil.buildFullPathToEntityAttribute(null);
        assertEquals(EMPTY_STRING, actual);
    }

    @Test
    void buildFullPathToEntityAttribute_onValidSingleArg_shouldReturnIt() {
        String expectedPath = "genre";
        String actual = SpecificationUtil.buildFullPathToEntityAttribute(expectedPath);
        assertEquals(expectedPath, actual);
    }

    @Test
    void buildFullPathToEntityAttribute_onValidMultipleArgs_shouldReturnTheFullPath() {
        String firstArgument = "genre";
        String secondArgument = "name";

        String actual = SpecificationUtil.buildFullPathToEntityAttribute(firstArgument, secondArgument);
        String expected = firstArgument + ENTITY_ATTRIBUTE_SEPARATOR + secondArgument;
        assertEquals(expected, actual);
    }

}
