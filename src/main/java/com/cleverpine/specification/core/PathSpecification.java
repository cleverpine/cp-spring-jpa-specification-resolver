package com.cleverpine.specification.core;

import com.cleverpine.specification.exception.IllegalSpecificationException;
import com.cleverpine.specification.item.JoinItem;
import com.cleverpine.specification.util.QueryContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Objects;

import static com.cleverpine.specification.util.FilterConstants.*;

@RequiredArgsConstructor
@Getter
public abstract class PathSpecification<T> implements Specification<T> {

    private final String path;

    private final QueryContext<T> queryContext;

    protected <G> Path<G> buildJoinPathToAttribute(Root<T> root) {
        if (isSingleAttributePath()) {
            return root.get(path);
        }

        String[] attributePathTokens = path.split("\\" + ATTRIBUTE_SEPARATOR);
        Deque<String> joinPathAliasesQueue = getJoinPathAliases(attributePathTokens);

        From<?, ?> joinPath = root;

        while (!joinPathAliasesQueue.isEmpty()) {
            joinPath = joinWith(joinPath, joinPathAliasesQueue.poll());
        }

        String attribute = attributePathTokens[attributePathTokens.length - 1];
        return joinPath.get(attribute);
    }

    protected <G> Path<G> buildFetchPathToAttribute(Root<T> root) {
        if (isSingleAttributePath()) {
            return root.get(path);
        }

        String[] attributePathTokens = path.split("\\" + ATTRIBUTE_SEPARATOR);
        Deque<String> joinPathAliasesQueue = getJoinPathAliases(attributePathTokens);

        From<?, ?> joinPath = root;

        while (!joinPathAliasesQueue.isEmpty()) {
            joinPath = joinFetchWith(joinPath, joinPathAliasesQueue.poll());
        }

        String attribute = attributePathTokens[attributePathTokens.length - 1];
        return joinPath.get(attribute);
    }

    private boolean isSingleAttributePath() {
        return !path.contains(ATTRIBUTE_SEPARATOR);
    }

    private Deque<String> getJoinPathAliases(String[] attributePathToken) {
        Deque<String> joinPathAliases = new ArrayDeque<>();
        Arrays.stream(Arrays.copyOfRange(attributePathToken, 0, attributePathToken.length - 1))
                .forEach(joinPathAliases::offer);
        return joinPathAliases;
    }

    private Join<?, ?> joinWith(From<?, ?> from, String joinPathAlias) {
        if (queryContext.isJoinPresent(joinPathAlias)) {
            return queryContext.getJoinByAlias(joinPathAlias);
        }

        JoinItem joinItem = queryContext.getJoinItemByAlias(joinPathAlias);
        validateJoin(joinItem, from.getJavaType(), joinPathAlias);

        Join<?, ?> joinPath = from.join(joinItem.getJoinAttribute(), joinItem.getType());
        queryContext.addJoin(joinPathAlias, joinPath);
        return joinPath;
    }

    private Join<?, ?> joinFetchWith(From<?, ?> from, String joinPathAlias) {
        if (queryContext.isJoinPresent(joinPathAlias)) {
            return queryContext.getJoinByAlias(joinPathAlias);
        }

        JoinItem joinItem = queryContext.getJoinItemByAlias(joinPathAlias);
        validateJoin(joinItem, from.getJavaType(), joinPathAlias);

        Join<?, ?> joinPath = (Join<?, ?>) from.fetch(joinItem.getJoinAttribute(), joinItem.getType());
        queryContext.addJoin(joinPathAlias, joinPath);
        return joinPath;
    }

    private void validateJoin(JoinItem joinItem, Class<?> joinType, String joinPathAlias) {
        if (Objects.isNull(joinItem)) {
            throw new IllegalSpecificationException(String.format(JOIN_NOT_DEFINED, joinPathAlias));
        }
        if (!joinType.equals(joinItem.getFromEntity())) {
            throw new IllegalSpecificationException(
                    String.format(INVALID_JOIN, joinItem.getJoinAttribute(), joinType.getSimpleName(), joinItem.getFromEntity().getSimpleName()));
        }
    }

}
