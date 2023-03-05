package com.cleverpine.specification.parser;

import com.cleverpine.specification.item.FilterItem;

import java.util.List;

public interface MultipleFilterParser {

    <T> List<FilterItem<T>> parseFilterParams(List<String> filterParams);

}
