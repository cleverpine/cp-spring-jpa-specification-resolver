package com.cleverpine.specification.parser;

import com.cleverpine.specification.item.OrderByItem;

import java.util.List;

public interface SingleSortParser {

    <T>List<OrderByItem<T>> parseSortParam(String sortParam);
}
