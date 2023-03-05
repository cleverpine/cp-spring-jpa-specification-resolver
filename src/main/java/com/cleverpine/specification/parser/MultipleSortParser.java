package com.cleverpine.specification.parser;

import com.cleverpine.specification.item.OrderByItem;

import java.util.List;

public interface MultipleSortParser {

    <T> List<OrderByItem<T>> parseSortParams(List<String> sortParams);

}
