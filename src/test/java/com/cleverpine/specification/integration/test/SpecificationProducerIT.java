package com.cleverpine.specification.integration.test;

import com.cleverpine.specification.exception.IllegalSpecificationException;
import com.cleverpine.specification.exception.InvalidSpecificationException;
import com.cleverpine.specification.integration.criteria.MovieFilterCriteria;
import com.cleverpine.specification.integration.entity.Actor;
import com.cleverpine.specification.integration.entity.Movie;
import com.cleverpine.specification.item.*;
import com.cleverpine.specification.parser.FilterJsonArrayParser;
import com.cleverpine.specification.parser.SortJsonArrayParser;
import com.cleverpine.specification.producer.ComplexSpecificationProducer;
import com.cleverpine.specification.producer.SimpleSpecificationProducer;
import com.cleverpine.specification.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class SpecificationProducerIT extends SpecificationProducerIntegrationTest {

    private static final ValueConverter VALUE_CONVERTER = new ValueConverter();

    private static final SpecificationQueryConfig<Movie> SPECIFICATION_QUERY_CONFIG;

    static {
        // @formatter:off
        SPECIFICATION_QUERY_CONFIG = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                    .defineJoinClause(Movie.class, "genre", "g", JoinType.INNER)
                    .defineJoinClause(Movie.class, "actors", "a", JoinType.INNER)
                    .end()
                .attributePathConfig()
                    .addAttributePathMapping("movieTitle", "title")
                    .addAttributePathMapping("genreName", "g.name")
                    .addAttributePathMapping("actorFirstName", "a.filterName")
                    .addAttributePathMapping("actorLastName", "a.lastName")
                    .end()
                .build();
        // @formatter:on
    }

    private ComplexSpecificationProducer<Movie> movieSpecificationProducer;


    public SpecificationProducerIT() {
        super(new SimpleSpecificationProducer(), new FilterJsonArrayParser(new ObjectMapper()), new SortJsonArrayParser(new ObjectMapper()), VALUE_CONVERTER);
    }

    @BeforeEach
    void setUp() {
        movieSpecificationProducer = new ComplexSpecificationProducer<>(
                simpleSpecificationProducer,
                filterParamParser,
                sortParamParser,
                valueConverter,
                MovieFilterCriteria.class,
                SPECIFICATION_QUERY_CONFIG);
    }

    @Test
    void createSpecification_onInvalidFilterParam_shouldThrow() {
        String filterParam = "[[\"id\",\"=\"]]";

        assertThrows(
                InvalidSpecificationException.class,
                () -> movieSpecificationProducer.createSpecification(filterParam)
        );
    }

    @Test
    void createSpecification_onInvalidFilterAttribute_shouldThrow() {
        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("invalid", "=", "test")));

        assertThrows(
                InvalidSpecificationException.class,
                () -> movieSpecificationProducer.createSpecification(filterParam)
        );
    }

    @Test
    void findOne_onEqualForFlatEntityAttribute_shouldReturnValidResult() {
        String expectedMovieTitle = "Fast and Furious";
        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("title", "=", expectedMovieTitle)));

        Specification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(filterParam);
        Movie actual = findOne(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertEquals(expectedMovieTitle, actual.getTitle());
    }

    @Test
    void findOne_onEqualForEntityAttributeThatNotExists_shouldThrowException() {
        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("movieTitle", "=", "Fast and Furious")));

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
                simpleSpecificationProducer,
                filterParamParser,
                sortParamParser,
                valueConverter,
                MovieFilterCriteria.class);

        Specification<Movie> movieSpecification = specificationProducer.createSpecification(filterParam);

        assertThrows(
                IllegalArgumentException.class,
                () -> findOne(movieSpecification, Movie.class)
        );
    }

    @Test
    void findOne_onEqualForEntityAttributeThatNotExistsButWithValidPathMapping_shouldReturnValidResult() {
        String expectedMovieTitle = "Fast and Furious";
        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("movieTitle", "=", expectedMovieTitle)));

        Specification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(filterParam);
        Movie actual = findOne(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertEquals(expectedMovieTitle, actual.getTitle());
    }

    @Test
    void findOne_onEqualForRelationalEntityAttributeAndNoJoinAndNoMappingToRelationalEntityIsPresent_shouldThrow() {
        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("genreName", "=", "Action")));

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
                simpleSpecificationProducer,
                filterParamParser,
                sortParamParser,
                valueConverter,
                MovieFilterCriteria.class);

        Specification<Movie> movieSpecification = specificationProducer.createSpecification(filterParam);

        assertThrows(
                IllegalArgumentException.class,
                () -> findAll(movieSpecification, Movie.class)
        );
    }

    @Test
    void findOne_onEqualForRelationalEntityAttributeAndMappingToRelationalEntityIsPresentButNoJoin_shouldThrow() {
        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("genreName", "=", "Action")));

        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .attributePathConfig()
                    .addAttributePathMapping("genreName", "g.name")
                    .end()
                .build();

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
                simpleSpecificationProducer,
                filterParamParser,
                sortParamParser,
                valueConverter,
                MovieFilterCriteria.class,
                specificationQueryConfig);

        Specification<Movie> movieSpecification = specificationProducer.createSpecification(filterParam);

        assertThrows(
                IllegalSpecificationException.class,
                () -> findAll(movieSpecification, Movie.class)
        );
    }

    @Test
    void findOne_onEqualForRelationalEntityAttributeAndJoinIsPresentButNoMappingToRelationalEntityAttribute_shouldThrow() {
        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("genreName", "=", "Action")));

        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                    .defineJoinClause(Movie.class, "genre", "g", JoinType.INNER)
                    .end()
                .build();

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
                simpleSpecificationProducer,
                filterParamParser,
                sortParamParser,
                valueConverter,
                MovieFilterCriteria.class,
                specificationQueryConfig);

        Specification<Movie> movieSpecification = specificationProducer.createSpecification(filterParam);

        assertThrows(
                IllegalArgumentException.class,
                () -> findAll(movieSpecification, Movie.class)
        );
    }

    @Test
    void findAll_onEqualForRelationalEntityAttributeAndJoinAndMappingToRelationalEntityAttributeArePresent_shouldReturnValueResult() {
        String expectedGenre = "Action";
        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("genreName", "=", expectedGenre)));

        Specification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(filterParam);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(1, actual.size());

        Movie movie = actual.get(0);
        assertEquals(expectedGenre, movie.getGenre().getName());
    }

    @Test
    void findAll_onEqualForRelationalEntityAttributeButJoinDeclarationIsInvalid_shouldThrow() {
        String expectedGenre = "Action";
        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("genreName", "=", expectedGenre)));

        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                    .defineJoinClause(Actor.class, "genre", "g", JoinType.INNER)
                    .end()
                .attributePathConfig()
                    .addAttributePathMapping("genreName", "g.name")
                    .end()
                .build();

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
                simpleSpecificationProducer,
                filterParamParser,
                sortParamParser,
                valueConverter,
                MovieFilterCriteria.class,
                specificationQueryConfig);

        Specification<Movie> movieSpecification = specificationProducer.createSpecification(filterParam);

        assertThrows(
                IllegalSpecificationException.class,
                () -> findAll(movieSpecification, Movie.class)
        );
    }

    @Test
    void findAll_onNotEqualFilterOperator_shouldReturnValueResult() {
        String unexpectedGenre = "Action";
        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("genreName", "!=", unexpectedGenre)));

        Specification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(filterParam);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        actual.forEach(movie -> assertNotEquals(unexpectedGenre, movie.getGenre().getName()));
    }

    @Test
    void findAll_onGreaterThanFilterOperator_shouldReturnValueResult() {
        Long expectedIdGreaterThan = 1L;
        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("id", ">", expectedIdGreaterThan.toString())));

        Specification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(filterParam);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        actual.forEach(movie -> assertTrue(movie.getId() > expectedIdGreaterThan));
    }

    @Test
    void findAll_onLessThanFilterOperator_shouldReturnValueResult() {
        Long expectedIdLessThan = 2L;
        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("id", "<", expectedIdLessThan.toString())));

        Specification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(filterParam);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        actual.forEach(movie -> assertTrue(movie.getId() < expectedIdLessThan));
    }

    @Test
    void findAll_onGreaterThanOrEqualFilterOperator_shouldReturnValueResult() {
        Long expectedIdGreaterThanOrEqual = 2L;
        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("id", ">=", expectedIdGreaterThanOrEqual.toString())));

        Specification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(filterParam);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        actual.forEach(movie -> assertTrue(movie.getId() >= expectedIdGreaterThanOrEqual));
    }

    @Test
    void findAll_onLessThanOrEqualFilterOperator_shouldReturnValueResult() {
        Long expectedIdLessThanOrEqual = 2L;
        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("id", "<=", expectedIdLessThanOrEqual.toString())));

        Specification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(filterParam);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        actual.forEach(movie -> assertTrue(movie.getId() <= expectedIdLessThanOrEqual));
    }

    @Test
    void findAll_onLikeFilterOperator_shouldReturnValueResult() {
        String expectedMovieTitleContains = "ast";
        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("title", "like", expectedMovieTitleContains)));

        Specification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(filterParam);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        actual.forEach(movie -> assertTrue(movie.getTitle().contains(expectedMovieTitleContains)));
    }

    @Test
    void findAll_onBetweenFilterOperator_shouldReturnValueResult() {
        Long firstValue = 2L;
        Long secondValue = 3L;

        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("id", "between", String.format("[\\\"%d\\\",\\\"%d\\\"]", firstValue, secondValue))));

        Specification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(filterParam);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        actual.forEach(movie -> assertTrue(movie.getId() >= firstValue && movie.getId() <= secondValue));
    }

    @Test
    void findAll_onInFilterOperator_shouldReturnValueResult() {
        String firstMovie = "Fast and Furious";
        String secondMovie = "Deadpool";

        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("title", "in", String.format("[\\\"%s\\\",\\\"%s\\\"]", firstMovie, secondMovie))));

        Specification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(filterParam);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        actual.forEach(movie ->
                assertTrue(movie.getTitle().equals(firstMovie) || movie.getTitle().equals(secondMovie)));
    }

    @Test
    void findAll_onDoubleEqualFilterItems_shouldReturnValueResult() {
        String expectedGenre = "Comedy";
        String expectedMovie = "Deadpool";

        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("genreName", "=", expectedGenre),
                        List.of("title", "=", expectedMovie)));

        Specification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(filterParam);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(1, actual.size());

        Movie movie = actual.get(0);

        assertEquals(expectedGenre, movie.getGenre().getName());
        assertEquals(expectedMovie, movie.getTitle());
    }

    @Test
    void findAll_onSortForFlatEntityAttribute_shouldReturnValidResult() {

        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .orderByConfig()
                    .addOrderBy("title", SortDirection.DESC)
                    .end()
                .build();

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
                simpleSpecificationProducer,
                filterParamParser,
                sortParamParser,
                valueConverter,
                MovieFilterCriteria.class,
                specificationQueryConfig);

        Specification<Movie> movieSpecification = specificationProducer.createSpecification();

        List<Movie> actual = findAll(movieSpecification, Movie.class);
        List<Movie> expected = actual.stream()
                .sorted(Comparator.comparing(Movie::getTitle).reversed())
                .collect(Collectors.toList());

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    void findAll_onSortForFilterAttributeThatDoesNotExist_shouldThrow() {
        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .orderByConfig()
                    .addOrderBy("invalidAttribute", SortDirection.DESC)
                    .end()
                .build();

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
                simpleSpecificationProducer,
                filterParamParser,
                sortParamParser,
                valueConverter,
                MovieFilterCriteria.class,
                specificationQueryConfig);

        assertThrows(
                InvalidSpecificationException.class,
                specificationProducer::createSpecification
        );
    }

    @Test
    void findAll_onSortForNestedEntityAttributeAndJoinNotDefined_shouldThrow() {
        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .attributePathConfig()
                    .addAttributePathMapping("genreName", "g.name")
                    .end()
                .orderByConfig()
                    .addOrderBy("genreName", SortDirection.DESC)
                    .end()
                .build();

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
                simpleSpecificationProducer,
                filterParamParser,
                sortParamParser,
                valueConverter,
                MovieFilterCriteria.class,
                specificationQueryConfig);

        Specification<Movie> movieSpecification = specificationProducer.createSpecification();

        assertThrows(
                IllegalSpecificationException.class,
                () -> findAll(movieSpecification, Movie.class)
        );
    }

    @Test
    void findAll_onSortForNestedEntityAttributeAndJoinIsPresent_shouldReturnValidResult() {

        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                    .defineJoinClause(Movie.class, "genre", "g", JoinType.INNER)
                    .end()
                .attributePathConfig()
                    .addAttributePathMapping("genreName", "g.name")
                    .end()
                .orderByConfig()
                    .addOrderBy("genreName", SortDirection.DESC)
                    .end()
                .build();

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
                simpleSpecificationProducer,
                filterParamParser,
                sortParamParser,
                valueConverter,
                MovieFilterCriteria.class,
                specificationQueryConfig);

        Specification<Movie> movieSpecification = specificationProducer.createSpecification();

        List<Movie> actual = findAll(movieSpecification, Movie.class);
        List<Movie> expected = actual.stream()
                .sorted((first, second) -> second.getGenre().getName().compareTo(first.getGenre().getName()))
                .collect(Collectors.toList());

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    void findAll_onSortAndFilterForNestedEntityAttribute_shouldReturnValidResult() {

        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                    .defineJoinClause(Movie.class, "genre", "g", JoinType.INNER)
                    .end()
                .attributePathConfig()
                    .addAttributePathMapping("genreName", "g.name")
                    .end()
                .orderByConfig()
                    .addOrderBy("genreName", SortDirection.DESC)
                    .end()
                .build();

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
                simpleSpecificationProducer,
                filterParamParser,
                sortParamParser,
                valueConverter,
                MovieFilterCriteria.class,
                specificationQueryConfig);

        List<FilterItem<Movie>> filterItems = List.of(new MultiFilterItem<>("genreName", FilterOperator.IN, List.of("Horror", "Comedy")));
        Specification<Movie> movieSpecification = specificationProducer.createSpecification(filterItems);

        List<Movie> actual = findAll(movieSpecification, Movie.class);
        List<Movie> expected = actual.stream()
                .sorted((first, second) -> second.getGenre().getName().compareTo(first.getGenre().getName()))
                .collect(Collectors.toList());

        assertArrayEquals(expected.toArray(), actual.toArray());
        actual.forEach(movie -> assertTrue(movie.getGenre().getName().equals("Horror") || movie.getGenre().getName().equals("Comedy")));
    }

    @Test
    void findAll_onComplexFilterAndSort_shouldReturnValidResult() {

        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                    .defineJoinClause(Movie.class, "actors", "a", JoinType.INNER)
                    .end()
                .attributePathConfig()
                    .addAttributePathMapping("actorFirstName", "a.firstName")
                    .addAttributePathMapping("actorLastName", "a.lastName")
                    .end()
                .orderByConfig()
                    .addOrderBy("actorFirstName", SortDirection.DESC)
                    .end()
                .build();

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
                simpleSpecificationProducer,
                filterParamParser,
                sortParamParser,
                valueConverter,
                MovieFilterCriteria.class,
                specificationQueryConfig);

        Specification<Movie> movieSpecification =
                specificationProducer.createSpecification(List.of(
                        new SingleFilterItem<>("actorFirstName", FilterOperator.EQUAL, "Ryan"),
                        new SingleFilterItem<>("actorLastName", FilterOperator.EQUAL, "Reynolds")));

        List<Movie> actual = findAll(movieSpecification, Movie.class);
        actual.forEach(movie ->
                assertTrue(movie.getActors().stream()
                        .anyMatch(actor -> actor.getFirstName().equals("Ryan") && actor.getLastName().equals("Reynolds"))));
    }

    @Test
    void findAll_onNotDistinctEntity_shouldReturnDuplicates() {

        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                    .defineJoinClause(Movie.class, "actors", "a", JoinType.INNER)
                    .end()
                .attributePathConfig()
                    .addAttributePathMapping("actorFirstName", "a.firstName")
                    .addAttributePathMapping("actorLastName", "a.lastName")
                    .end()
                .orderByConfig()
                    .addOrderBy("actorFirstName", SortDirection.DESC)
                    .end()
                .entityDistinctRequired(false)
                .build();

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
                simpleSpecificationProducer,
                filterParamParser,
                sortParamParser,
                valueConverter,
                MovieFilterCriteria.class,
                specificationQueryConfig);

        Specification<Movie> movieSpecification =
                specificationProducer.createSpecification(List.of(
                        new MultiFilterItem<>("actorFirstName", FilterOperator.IN, List.of("Ryan", "Morena"))));

        List<Movie> actual = findAll(movieSpecification, Movie.class);
        Set<Long> movieIds = actual.stream().map(Movie::getId).collect(Collectors.toSet());

        assertTrue(actual.size() > movieIds.size());
    }

    @Test
    void findAll_onDistinctEntity_shouldReturnNoDuplicates() {

        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                .defineJoinClause(Movie.class, "actors", "a", JoinType.INNER)
                .end()
                .attributePathConfig()
                .addAttributePathMapping("actorFirstName", "a.firstName")
                .addAttributePathMapping("actorLastName", "a.lastName")
                .end()
                    .orderByConfig()
                    .addOrderBy("actorFirstName", SortDirection.DESC)
                    .end()
                .entityDistinctRequired(true)
                .build();

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
                simpleSpecificationProducer,
                filterParamParser,
                sortParamParser,
                valueConverter,
                MovieFilterCriteria.class,
                specificationQueryConfig);

        Specification<Movie> movieSpecification =
                specificationProducer.createSpecification(List.of(
                        new MultiFilterItem<>("actorFirstName", FilterOperator.IN, List.of("Ryan", "Morena"))));

        List<Movie> actual = findAll(movieSpecification, Movie.class);
        Set<Long> movieIds = actual.stream().map(Movie::getId).collect(Collectors.toSet());

        assertEquals(actual.size(), movieIds.size());
    }

    @Test
    void createSpecification_invalidFilterParam_shouldThrow() {
        String filterParam = "[[\"id\",\"=\"]]";
        String sortParam = createJsonArraySortParam(
                List.of(
                        List.of("title", "desc")));

        assertThrows(
                InvalidSpecificationException.class,
                () -> movieSpecificationProducer.createSpecification(filterParam, sortParam)
        );
    }

    @Test
    void createSpecification_invalidSortDirection_shouldThrow() {
        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("title", "=", "random")));
        String sortParam = "[[\"id\",\"=\"]]";


        assertThrows(
                InvalidSpecificationException.class,
                () -> movieSpecificationProducer.createSpecification(filterParam, sortParam)
        );
    }

    @Test
    void createSpecification_invalidSortParamNumber_shouldThrow() {
        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("invalid", "=", "test")));
        String sortParam = "[[\"id\"]]";


        assertThrows(
                InvalidSpecificationException.class,
                () -> movieSpecificationProducer.createSpecification(filterParam, sortParam)
        );
    }

    @Test
    void findAll_whenValidFilterAndSort_shouldReturnValidResult() {
        String firstGenre = "Action";
        String secondGenre = "Comedy";
        String filterParam = createJsonArrayFilterParam(
                List.of(
                        List.of("genreName", "in", String.format("[\\\"%s\\\",\\\"%s\\\"]", firstGenre, secondGenre))));

        String sortParam = createJsonArraySortParam(
                List.of(
                        List.of("id", "desc")));


        Specification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(filterParam, sortParam);
        List<Movie> actual = findAll(movieSpecification, Movie.class);
        Movie firstMovie = actual.get(0);
        Movie secondMovie = actual.get(1);

        assertTrue(firstMovie.getId() > secondMovie.getId());
        assertNotNull(actual);
        assertTrue(firstMovie.getGenre().getName().equals(firstGenre) || firstMovie.getGenre().getName().equals(secondGenre));
    }



}
