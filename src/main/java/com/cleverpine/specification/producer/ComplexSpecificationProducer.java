package com.cleverpine.specification.producer;

import com.cleverpine.specification.core.OrderBySpecification;
import com.cleverpine.specification.item.FilterItem;
import com.cleverpine.specification.item.OrderByItem;
import com.cleverpine.specification.parser.FilterParamParser;
import com.cleverpine.specification.parser.SortParamParser;
import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.SpecificationQueryConfig;
import com.cleverpine.specification.util.ValueConverter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ComplexSpecificationProducer<T> {

    private final SimpleSpecificationProducer simpleSpecificationProducer;

    private final FilterParamParser filterParamParser;

    private final SortParamParser sortParamParser;

    private final ValueConverter valueConverter;

    private final Class<?> filterType;

    private final SpecificationQueryConfig<T> specificationQueryConfig;

    public ComplexSpecificationProducer(SimpleSpecificationProducer simpleSpecificationProducer,
                                        FilterParamParser filterParamParser,
                                        SortParamParser sortParamParser,
                                        ValueConverter valueConverter,
                                        Class<?> filterType) {
        this(simpleSpecificationProducer, filterParamParser, sortParamParser, valueConverter, filterType, SpecificationQueryConfig.<T>builder().build());
    }

    public ComplexSpecificationProducer(SimpleSpecificationProducer simpleSpecificationProducer,
                                        FilterParamParser filterParamParser,
                                        SortParamParser sortParamParser,
                                        ValueConverter valueConverter,
                                        Class<?> filterType,
                                        SpecificationQueryConfig<T> specificationQueryConfig) {
        this.simpleSpecificationProducer = simpleSpecificationProducer;
        this.filterParamParser = filterParamParser;
        this.sortParamParser = sortParamParser;
        this.valueConverter = valueConverter;
        this.filterType = filterType;
        this.specificationQueryConfig = specificationQueryConfig;
    }

    public Specification<T> createSpecification() {
        return createSpecification(new ArrayList<>());
    }

    public Specification<T> createSpecification(String filterParam) {
        List<FilterItem<T>> filterItems = filterParamParser.parseFilterParam(filterParam);
        return createSpecification(filterItems, new ArrayList<>());
    }
    public Specification<T> createSpecification(String filterParam, List<FilterItem<T>> additionalFilterItems) {
        List<FilterItem<T>> filterItems = new ArrayList<>();
        filterItems.addAll(additionalFilterItems);
        filterItems.addAll(filterParamParser.parseFilterParam(filterParam));

        return createSpecification(filterItems, new ArrayList<>());
    }

    public Specification<T> createSpecification(String filterParam, String sortParam) {
        List<FilterItem<T>> filterItems = filterParamParser.parseFilterParam(filterParam);
        List<OrderByItem<T>> orderItems = sortParamParser.parseSortParam(sortParam);
        return createSpecification(filterItems, orderItems);
    }

    public Specification<T> createSpecification(String filterParam, List<FilterItem<T>> additionalFilterItems, String sortParam) {
        List<FilterItem<T>> filterItems = new ArrayList<>();
        filterItems.addAll(additionalFilterItems);
        filterItems.addAll(filterParamParser.parseFilterParam(filterParam));
        List<OrderByItem<T>> orderItems = new ArrayList<>();
        orderItems.addAll(sortParamParser.parseSortParam(sortParam));
        return createSpecification(filterItems, orderItems);
    }


    @SafeVarargs
    public final Specification<T> createSpecification(OrderByItem<T>... orderItems) {
        return createSpecification(new ArrayList<>(), List.of(orderItems));
    }

    public Specification<T> createSpecification(List<FilterItem<T>> filterItems) {
        return createSpecification(filterItems, new ArrayList<>());
    }

    public Specification<T> createSpecification(List<FilterItem<T>> filterItems, List<OrderByItem<T>> orderItems) {
        QueryContext<T> queryContext = new QueryContext<>(
                specificationQueryConfig.getJoinConfig(),
                specificationQueryConfig.getAttributePathConfig());

        List<FilterItem<T>> specificationFilterItems = getAllSpecificationFilterItems(filterItems);
        List<OrderByItem<T>> specificationOrderItems = getAllSpecificationOrderItems(orderItems);

        List<Specification<T>> specifications = new ArrayList<>();
        specifications.addAll(simpleSpecificationProducer
                .produceFilterSpecifications(filterType, specificationFilterItems, queryContext, valueConverter));
        specifications.addAll(simpleSpecificationProducer
                .produceOrderBySpecifications(filterType, specificationOrderItems, queryContext));

        return conjugate(specifications, queryContext);
    }

    private Specification<T> conjugate(List<Specification<T>> specifications, QueryContext<T> queryContext) {
        return (root, query, criteriaBuilder) -> {
            queryContext.clearState();
            queryContext.setEntityDistinctRequired(specificationQueryConfig.isEntityDistinctRequired());
            if (queryContext.isEntityDistinctRequired()) {
                query.distinct(true);
            }

            processOrderBySpecifications(specifications, root, query, criteriaBuilder);
            Predicate[] predicates = specifications.stream()
                    .filter(spec -> !isOrderBySpecification(spec))
                    .map(spec -> spec.toPredicate(root, query, criteriaBuilder))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList())
                    .toArray(new Predicate[]{});

            return criteriaBuilder.and(predicates);
        };
    }

    private List<FilterItem<T>> getAllSpecificationFilterItems(List<FilterItem<T>> filterItems) {
        ArrayList<FilterItem<T>> specificationFilterItems = new ArrayList<>();
        specificationFilterItems.addAll(filterItems);
        specificationFilterItems.addAll(specificationQueryConfig.getFilterConfig().getFilterItems());
        return specificationFilterItems;
    }

    private List<OrderByItem<T>> getAllSpecificationOrderItems(List<OrderByItem<T>> orderItems) {
        List<OrderByItem<T>> specificationOrderItems = new ArrayList<>();
        specificationOrderItems.addAll(orderItems);
        specificationOrderItems.addAll(specificationQueryConfig.getOrderByConfig().getOrderByItems());
        return specificationOrderItems;
    }

    private void processOrderBySpecifications(List<Specification<T>> specifications, Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        specifications.stream()
                .filter(this::isOrderBySpecification)
                .forEach(spec -> spec.toPredicate(root, query, cb));
    }

    private boolean isOrderBySpecification(Specification<T> specification) {
        return specification instanceof OrderBySpecification;
    }

}
