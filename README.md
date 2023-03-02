# CleverPine Spring JPA Specification Resolver

## Description

The filtering of resources by various dynamic and complex criteria is an essential part in many RESTful APIs. Unfortunately, there is no established standard for sending the filtering arguments to a RESTful API, on another hand, the filtering database queries are unique for each scenario, which requires some configurations.

```
GET http://my-back-end/api/movies?filter=[["title","=","Fast and Furious"]]
```

The following library consists of two independent main features:

* Building a single JPA Specification based on multiple complex filter criteria.
* Parsing a single query parameter in order to extract all filter items from it.

## Table of contents

* [Requirements](#requirements)
* [Library API](#library-api)
* [Basic usage](#basic-usage)
* [Usage with Spring](#usage-with-spring)
* [Features](#features)
* [Contribution](#contribution)

## Requirements

In order to add the library to the classpath of a Maven project, you need the following dependency.

```xml
<dependency>
    <groupId>com.cleverpine</groupId>
    <artifactId>cp-spring-jpa-specification-resolver</artifactId>
    <version>${cp-spring-jpa-specification-resolver.version}</version>
</dependency>
```

Do not forget to pick an appropriate version.

## Library API

Familiarize yourself with the main public classes and interfaces in order to take advantage of the library's full potential.

* #### ValueConverter

When parsing the filter value from the query string, the given value will always be а string, but the JPA Specification works under the hood with statically typed values. This class can convert the string value to the target attribute's type. It is possible to add additional custom mappings for special use cases. See how to configure the ValueConverter here.

* #### FilterParamParser

This is an interface for parsing the input query parameter and preparing it for a JPA Specification creation (producing filter items). As above already mentioned there is no established standard for interpreting the filter parameter. That is why this interface can have many implementations based on your use case. For example the FilterJsonArrayParser is an implementation of the interface, which prepares a json array string for a JPA Specification creation.

* #### SimpleSpecificationProducer

This class produces the separate simple JPA Specifications based on each filter item and validates its attribute.

* #### ComplexSpecificationProducer

This is the main class that creates the target JPA Specification for a single entity.

## Basic usage

The library can be used very easily in a simple Maven project. Just initialize the classes mentioned above. See the example:

```java
public void initialize() {
    ValueConverter valueConverter = new ValueConverter();
    // FilterJsonArrayParser can be replaced by any other implementation of FilterParamParser interface
    FilterParamParser filterParamParser = new FilterJsonArrayParser(new ObjectMapper(), valueConverter);
    SimpleSpecificationProducer simpleSpecificationProducer = new SimpleSpecificationProducer();
    ComplexSpecificationProducer<Movie> movieSpecificationProducer =
            new ComplexSpecificationProducer<>(simpleSpecificationProducer, filterParamParser, valueConverter, MovieCriteria.class);
    Specification<Movie> specification = movieSpecificationProducer.createSpecification("here goes the filter query parameter");
}
```

The MovieCriteria class contains all the attributes that can be part of the filtering. This class is used for validating the filtering attributes.

A single ComplexSpecificationProducer object instance can be used for the production of JPA Specifications only for a single entity. Taking this fact into account the ComplexSpecificationProducer can be extended for each entity if it is needed. This means that a new class can be created.

The library gives the ability to configure each entity specification producer - adding mappings from the criteria attribute to the entity attribute path, defining joins etc.... More about the configuration of a single complex specification producer - see the section below.

The example below is a more concrete for producing Movie entity JPA specifications.

* Entities / Data model

```java
@Entity
class Movie {

    @Id
    private Long id;

    @Column
    private String title;

    @ManyToOne
    private Genre genre;

    @ManyToMany
    private Set<Actor> actors;
}

@Entity
class Genre {

    @Id
    private Long id;

    @Column
    private String name;
}

@Entity
class Actor {

    @Id
    private Long id;

    @Column
    private String fullName;
}
```

* Spring Data JPA Repository

Note! If you want to use JPA Specification, extend the repository interface with JpaSpecificationExecutor

```java
@Repository
interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {
}
```

* Movie criteria

This class contains the filtering attributes. It is required to filter the movies only by the following criteria attributes.

```java
class MovieCriteria {

    private Long id;

    private String title;

    private String genreName;

    private String actorName;

    private Long movieTitle;
    
}
```

* Movie specification producer

This is the class that produces JPA specification for the movie entity. The JPA Specification producer can be configured with a SpecificationQueryConfig class that is based on a builder pattern. See the example below.

```java
import com.cleverpine.specification.parser.FilterParamParser;
import com.cleverpine.specification.producer.ComplexSpecificationProducer;
import com.cleverpine.specification.util.SpecificationQueryConfig;
import com.cleverpine.specification.util.SpecificationUtil;

class MovieSpecificationProducer extends ComplexSpecificationProducer<Movie> {

    private static final String MOVIE_GENRE_JOIN_ALIAS = "g";

    private static final String MOVIE_ACTORS_JOIN_ALIAS = "a";

    private static final SpecificationQueryConfig<Movie> SPECIFICATION_QUERY_CONFIG;

    static {
        SPECIFICATION_QUERY_CONFIG = SpecificationQueryConfig.builder()
                .joinConfig()
                    .defineJoinClause(Movie.class, "genre", MOVIE_GENRE_JOIN_ALIAS, JoinType.INNER)
                    .defineJoinClause(Movie.class, "actors", MOVIE_ACTORS_JOIN_ALIAS, JoinType.INNER)
                    .end()
                .attributePathConfig()
                    // add paths to entity attributes only if a criteria attribute does not match the entity attribute
                    .addAttributePathMapping("movieTitle", "title")
                    // add the join alias if an attribute from a relationship is required
                    .addAttributePathMapping("genreName", SpecificationUtil.buildFullPathToEntityAttribute(MOVIE_GENRE_JOIN_ALIAS, "name"))
                    .addAttributePathMapping("actorName", SpecificationUtil.buildFullPathToEntityAttribute(MOVIE_ACTORS_JOIN_ALIAS, "fullName"))
                    .end()
                .build();
    }

    public MovieSpecificationProducer(SimpleSpecificationProducer simpleSpecificationProducer, FilterParamParser filterParamParser, ValueConverter valueConverter) {
        super(simpleSpecificationProducer, filterParamParser, valueConverter, MovieCriteria.class, SPECIFICATION_QUERY_CONFIG);
    }
}
```

_When the criteria attribute name is the same as the entity attribute, it is not necessary to add a mapping for this filter attribute. That is why there is no mapping in the specification producer above for the title of the movie._

_Note! The defineJoinClause method in the joinConfig contains the join definitions. The join is created on-demand. The above specification producer will join the movies with actors only if a filter item with the actorName attribute is present._

## Usage with Spring

Each entity specification producer can be managed by the Spring in order to take advantage of its inversion of control principle. Create a configuration class and define the producers as beans.

```java
@Configuration
class JpaSpecificationConfig {

    @Bean
    public ValueConverter valueConverter() {
        return new ValueConverter();
    }

    @Bean
    public FilterParamParser filterJsonArrayParser() {
        return new FilterJsonArrayParser(new ObjectMapper(), valueConverter());
    }

    @Bean
    public SimpleSpecificationProducer simpleSpecificationProducer() {
        return new SimpleSpecificationProducer();
    }

    @Bean
    public MovieSpecificationProducer movieSpecificationProducer() {
        return new MovieSpecificationProducer(simpleSpecificationProducer(), filterJsonArrayParser(), valueConverter());
    }
}
```

The specification producers can be injected in each managed class by Spring.

Example:

```java
@Service
class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieSpecificationProducer movieSpecificationProducer;

    public List<Movie> getAllMovies(String filterParameter) {
        Specification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(filterParameter);
        return movieRepository.findAll(movieSpecification);
    }
}
```

## Features

#### Supported Filter Operators

Each filter item consists of three units - criteria attribute, filter operator and value/s. The supported filter operators are:

* Equal / =
* Not Equal / !=
* Greater Than / >
* Less Than / <
* Greater Than or Equal / >=
* Less Than or Equal / <=
* Like / like
* Between / between
* In / in

#### Joins on-demand

For each entity specification producer you can define several joins in order to filter the result based on some entity relationship attributes. The JoinItem class instances are joins on-demand. The producer will join the entities only if an appropriate filter attribute is present. Each JoinItem has an alias declaration. The aliases are present to make complex queries more manageable. If you want to access some nested entity properties, you should use the alias in order to do that. See this example.

#### Additional filter request

When creating the specification, you can pass additional filter requests parameters. This makes your queries much more flexible and manageable. The attribute in the filter request should be part of the criteria class.

```java
FilterRequest titleFilterRequest = new FilterRequest("title", FilterOperator.EQUAL, "Fast and Furious");
Specification<Movie> specification = movieSpecificationProducer.createSpecification(filterParamater, titleFilterRequest);
```

#### Customize value converter

You can add additional custom value converter based on your use case.

```java
Map<Class<?>, Function<String, Object>> customValueConverters = new HashMap<>();
customValueConverters.put(MovieStatus.class, (value) -> MovieStatus.valueOf(value));
ValueConverter valueConverter = new ValueConverter();
valueConverter.addCustomValueConverters(customValueConverters);
```

#### Specification producer configuration

The class _SpecificationQueryConfig_ is based on the builder pattern. You can configure the joins, paths to entity attributes, sorting attributes and adding additional filter criteria.

```java
SpecificationQueryConfig<Movie> queryConfig = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                    .defineJoinClause(Movie.class, "genre", MOVIE_GENRE_JOIN_ALIAS, JoinType.INNER)
                    .defineJoinClause(Movie.class, "actors", MOVIE_ACTORS_JOIN_ALIAS, JoinType.INNER)
                    .end()
                .attributePathConfig()
                    .addAttributePathMapping("movieTitle", "title")
                    .addAttributePathMapping("genreName", SpecificationUtil.buildFullPathToEntityAttribute(MOVIE_GENRE_JOIN_ALIAS, "name"))
                    .addAttributePathMapping("actorName", SpecificationUtil.buildFullPathToEntityAttribute(MOVIE_ACTORS_JOIN_ALIAS, "fullName"))
                    .end()
                .orderByConfig()
                    .addOrderBy("genreName", SortDirection.DESC)
                    .end()
                .entityDistinctRequired(true)
                .build();
```

#### Sorting

You can sort by several attributes. This is configurable in the _SpecificationQueryConfig_ class or you can pass soring parameter when creating the specification.

#### Remove duplicates

You are able to do entity distinction. During the configuration of a specification producer in the _SpecificationQueryConfigBuilder_ class you can pass a value to the _entityDistinctRequired_ method. Keep in mind that, if you use the distinction in combination with sorting by an attribute, the attribute is fetched.

## Contribution

The library is not fully-featured. If you need something else, that is not present in the library. Do not hesitate to contribute to it. Create a pull request and add some tests. It is open for extension. :)