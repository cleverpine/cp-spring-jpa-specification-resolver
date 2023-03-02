package com.cleverpine.specification.integration.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "movies")
@Data
public class Movie {

    @Id
    private Long id;

    @Column
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    private Genre genre;

    @ManyToMany
    @JoinTable(name = "movies_actors", joinColumns = {
            @JoinColumn(name = "movie_id", referencedColumnName = "id")
    }, inverseJoinColumns =
            @JoinColumn(name = "actor_id", referencedColumnName = "id"))
    private Set<Actor> actors;

}
