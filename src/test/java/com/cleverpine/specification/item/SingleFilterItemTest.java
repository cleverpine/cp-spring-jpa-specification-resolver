package com.cleverpine.specification.item;

import com.cleverpine.specification.core.GreaterThan;
import com.cleverpine.specification.exception.IllegalSpecificationException;
import com.cleverpine.specification.util.FilterOperator;
import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.SpecificationQueryConfig;
import com.cleverpine.specification.util.ValueConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SingleFilterItemTest {

    @Mock
    private ValueConverter valueConverter;

    private static final String ATTRIBUTE = "attribute";

    private static final FilterOperator FILTER_OPERATOR = FilterOperator.EQUAL;

    private static final String VALUE = "14";

    @Test
    void constructor_onNullAttribute_shouldThrow() {
        assertThrows(
                NullPointerException.class,
                () -> new SingleFilterItem<>(null, FILTER_OPERATOR, VALUE)
        );
    }

    @Test
    void constructor_onNullOperator_shouldThrow() {
        assertThrows(
                NullPointerException.class,
                () -> new SingleFilterItem<>(ATTRIBUTE, null, VALUE)
        );
    }

    @Test
    void constructor_onNullValue_shouldThrow() {
        assertThrows(
                NullPointerException.class,
                () -> new SingleFilterItem<>(ATTRIBUTE, FILTER_OPERATOR, null)
        );
    }

    @Test
    void constructor_onValidArgs_shouldCreateFilterItem() {
        SingleFilterItem<Object> actual = assertDoesNotThrow(() ->
                new SingleFilterItem<>(ATTRIBUTE, FILTER_OPERATOR, VALUE));
        assertNotNull(actual);
    }

    @Test
    void createSpecification_whenFilterOperatorIsNotCompatibleWithSingleFilterItem_shouldThrow() {
        SingleFilterItem<Object> notCompatibleFilterItem =
                new SingleFilterItem<>(ATTRIBUTE, FilterOperator.IN, VALUE);

        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder().build();
        QueryContext<Object> queryContext = new QueryContext<>(queryConfig.getJoinConfig(), queryConfig.getAttributePathConfig());

        assertThrows(
                IllegalSpecificationException.class,
                () -> notCompatibleFilterItem.createSpecification(queryContext, valueConverter)
        );
    }

    @Test
    void createSpecification_onValidFilterOperatorForSingleFilterItem_shouldCreateAppropriateSpecificationType() {
        SingleFilterItem<Object> greaterThanFilterItem =
                new SingleFilterItem<>(ATTRIBUTE, FilterOperator.GREATER_THAN, VALUE);

        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder().build();
        QueryContext<Object> queryContext = new QueryContext<>(queryConfig.getJoinConfig(), queryConfig.getAttributePathConfig());

        Specification<Object> specification =
                greaterThanFilterItem.createSpecification(queryContext, valueConverter);

        assertEquals(GreaterThan.class, specification.getClass());
    }

    @Test
    void createSpecification_whenPathToEntityAttributeIsNotPresent_shouldTakeTheAttributeFromTheFilterItemAsPath() {
        SingleFilterItem<Object> greaterThanFilterItem =
                new SingleFilterItem<>(ATTRIBUTE, FilterOperator.GREATER_THAN, VALUE);

        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder().build();
        QueryContext<Object> queryContext = new QueryContext<>(queryConfig.getJoinConfig(), queryConfig.getAttributePathConfig());

        GreaterThan<Object> specification =
                (GreaterThan<Object>) greaterThanFilterItem
                        .createSpecification(queryContext, valueConverter);

        assertEquals(ATTRIBUTE, specification.getPath());
    }

    @Test
    void createSpecification_whenPathToEntityAttributeIsPresent_shouldOverrideTheAttributeFromTheFilterItemAsPath() {
        SingleFilterItem<Object> greaterThanFilterItem =
                new SingleFilterItem<>(ATTRIBUTE, FilterOperator.GREATER_THAN, VALUE);

        String expectedPath = "first.second";

        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder()
                .attributePathConfig()
                    .addAttributePathMapping(ATTRIBUTE, expectedPath)
                    .end()
                .build();

        QueryContext<Object> queryContext = new QueryContext<>(queryConfig.getJoinConfig(), queryConfig.getAttributePathConfig());

        GreaterThan<Object> specification =
                (GreaterThan<Object>) greaterThanFilterItem
                        .createSpecification(queryContext, valueConverter);

        assertEquals(expectedPath, specification.getPath());
    }

}
