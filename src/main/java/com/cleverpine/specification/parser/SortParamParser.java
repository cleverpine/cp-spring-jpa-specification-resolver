package com.cleverpine.specification.parser;

import com.cleverpine.specification.item.OrderByItem;

import java.util.List;

public interface SortParamParser {

    <T>List<OrderByItem<T>> parseSortParam(String sortParam);
}
