package com.cleverpine.specification.util;

import com.cleverpine.specification.item.JoinItem;
import org.junit.jupiter.api.Test;

import javax.persistence.criteria.JoinType;

import static org.junit.jupiter.api.Assertions.*;

public class QueryContextTest {

    @Test
    void constructor_shouldNotRequireDistinctEntityByDefault() {
        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder().build();
        QueryContext<Object> queryContext = new QueryContext<>(queryConfig);
        assertFalse(queryContext.isEntityDistinctRequired());
    }

    @Test
    void getPathToEntityField_whenAttributeIsNotPresent_shouldReturnNull() {
        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder().build();
        QueryContext<Object> queryContext = new QueryContext<>(queryConfig);
        String actual = queryContext.getPathToEntityAttribute("invalid");
        assertNull(actual);
    }

    @Test
    void getPathToEntityFiled_whenAttributeIsPresent_shouldReturnThePath() {
        String attribute = "genreName";
        String expectedPath = "genre";
        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder()
                .attributePathConfig()
                    .addAttributePathMapping(attribute, expectedPath)
                    .end()
                .build();

        QueryContext<Object> queryContext = new QueryContext<>(queryConfig);

        String actual = queryContext.getPathToEntityAttribute(attribute);
        assertEquals(expectedPath, actual);
    }
    @Test
    void getJoinItemByAlias_whenAliasIsNotPresent_shouldReturnNull() {
        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder().build();
        QueryContext<Object> queryContext = new QueryContext<>(queryConfig);
        JoinItem actual = queryContext.getJoinItemByAlias("invalid alias");
        assertNull(actual);
    }

    @Test
    void getJoinItemByAlias_whenAliasIsPresent_shouldReturnThePath() {
        JoinItem expectedJoinItem = new JoinItem(Class.class, "genre", "g", JoinType.INNER);
        String alias = "g";

        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder()
                .joinConfig()
                    .defineJoinClause(Class.class, "genre", "g", JoinType.INNER)
                    .end()
                .build();
        QueryContext<Object> queryContext = new QueryContext<>(queryConfig);

        JoinItem actual = queryContext.getJoinItemByAlias(alias);
        assertEquals(expectedJoinItem.getFromEntity(), actual.getFromEntity());
        assertEquals(expectedJoinItem.getJoinAttribute(), actual.getJoinAttribute());
        assertEquals(expectedJoinItem.getAlias(), actual.getAlias());
        assertEquals(expectedJoinItem.getType(), actual.getType());
    }

    @Test
    void clearState_shouldClearTheQueryJoins() {
        String alias = "g";
        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder().build();
        QueryContext<Object> queryContext = new QueryContext<>(queryConfig);
        queryContext.addJoin(alias, null);

        assertTrue(queryContext.isJoinPresent(alias));
        queryContext.clearState();
        assertFalse(queryContext.isJoinPresent(alias));
    }

    @Test
    void clearState_shouldClearTheRequiredDistinct() {
        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder().build();
        QueryContext<Object> queryContext = new QueryContext<>(queryConfig);
        queryContext.setEntityDistinctRequired(true);

        assertTrue(queryContext.isEntityDistinctRequired());
        queryContext.clearState();
        assertFalse(queryContext.isEntityDistinctRequired());
    }

}
