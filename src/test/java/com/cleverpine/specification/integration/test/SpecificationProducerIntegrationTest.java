package com.cleverpine.specification.integration.test;

import com.cleverpine.specification.parser.SpecificationParserManager;
import com.cleverpine.specification.util.ValueConverter;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;

public class SpecificationProducerIntegrationTest {

    private static final String PERSISTENCE_UNIT_TEST_NAME = "cp-spring-specification-resolver-test";

    protected static EntityManager entityManager;

    protected final SpecificationParserManager specificationParserManager;

    protected final ValueConverter valueConverter;

    protected SpecificationProducerIntegrationTest(SpecificationParserManager specificationParserManager,
                                                   ValueConverter valueConverter) {
        this.specificationParserManager = specificationParserManager;
        this.valueConverter = valueConverter;
    }

    @BeforeAll
    public static void init() {
        entityManager = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_TEST_NAME)
                .createEntityManager();
    }

    protected <T> List<T> findAll(Specification<T> specification, Class<T> entityType) {
        return entityManager.createQuery(getQuery(specification, entityType))
                .getResultList();
    }

    protected <T> T findOne(Specification<T> specification, Class<T> entityType) {
        return entityManager.createQuery(getQuery(specification, entityType))
                .getSingleResult();
    }

    protected String createJsonArrayFilterParam(List<List<String>> filterParams) {
        String collect = filterParams.stream()
                .map(filterParamItems ->
                        String.format("[\"%s\",\"%s\",\"%s\"]",
                                filterParamItems.get(0),
                                filterParamItems.get(1),
                                filterParamItems.get(2)))
                .collect(Collectors.joining(","));
        return String.format("[%s]", collect); }


    protected String createJsonArraySortParam(List<List<String>> sortParams) {
        String collect = sortParams.stream()
                .map(sortParamItems ->
                        String.format("[\"%s\",\"%s\"]",
                                sortParamItems.get(0),
                                sortParamItems.get(1)))
                .collect(Collectors.joining(","));
        return collect; }


        private <T > CriteriaQuery < T > getQuery(Specification < T > specification, Class < T > entityType) {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<T> query = criteriaBuilder.createQuery(entityType);
            Root<T> root = query.from(entityType);
            Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
            query.where(predicate);
            return query;
        }
    }
