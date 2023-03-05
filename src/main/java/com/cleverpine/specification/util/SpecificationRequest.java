package com.cleverpine.specification.util;

import com.cleverpine.specification.item.FilterItem;
import com.cleverpine.specification.item.OrderByItem;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Builder(setterPrefix = "with")
@Getter
public class SpecificationRequest<T> {

    private final String filterParam;

    private final List<String> filterParams;

    private final List<FilterItem<T>> filterItems;

    private final String sortParam;

    private final List<String> sortParams;

    private final List<OrderByItem<T>> sortItems;

    public static <T> SpecificationRequest<T> createEmpty() {
        return SpecificationRequest.<T>builder()
                .build();
    }
}
