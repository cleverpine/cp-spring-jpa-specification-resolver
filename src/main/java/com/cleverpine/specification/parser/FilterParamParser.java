package com.cleverpine.specification.parser;

import com.cleverpine.specification.item.FilterItem;

import java.util.List;

public interface FilterParamParser {

    <T> List<FilterItem<T>> parseFilterParam(String filterParam);

}
