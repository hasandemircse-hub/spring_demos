package com.hasan.authorservice.entities;

import jakarta.persistence.*;

@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String nationality;
    private int birthYear;

    public Author() {}

    public Author(Long id, String name, String nationality, int birthYear) {
        this.id = id;
        this.name = name;
        this.nationality = nationality;
        this.birthYear = birthYear;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public int getBirthYear() { return birthYear; }
    public void setBirthYear(int birthYear) { this.birthYear = birthYear; }
}
