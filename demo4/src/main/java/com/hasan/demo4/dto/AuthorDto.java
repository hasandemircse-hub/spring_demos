package com.hasan.demo4.dto;

// Author Service'den gelen JSON'u bu sınıfa parse ederiz
// Author Service'deki AuthorResponseDto ile aynı alanlar olmalı
public class AuthorDto {

    private Long id;
    private String name;
    private String nationality;
    private int birthYear;

    public AuthorDto() {} // JSON deserialize için boş constructor şart

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public int getBirthYear() { return birthYear; }
    public void setBirthYear(int birthYear) { this.birthYear = birthYear; }
}
