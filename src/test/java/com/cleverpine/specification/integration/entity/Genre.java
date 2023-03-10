package com.cleverpine.specification.integration.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "genres")
@Data
public class Genre {

    @Id
    private Long id;

    @Column
    private String name;


}
