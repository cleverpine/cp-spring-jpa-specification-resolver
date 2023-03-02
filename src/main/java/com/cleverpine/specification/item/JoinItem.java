package com.cleverpine.specification.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.criteria.JoinType;

@RequiredArgsConstructor
@Getter
public class JoinItem {

    private final Class<?> fromEntity;

    private final String joinAttribute;

    private final String alias;

    private final JoinType type;

}
