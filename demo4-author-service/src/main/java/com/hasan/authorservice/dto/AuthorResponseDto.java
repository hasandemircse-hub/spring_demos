package com.hasan.authorservice.dto;

public class AuthorResponseDto {

    private Long id;
    private String name;
    private String nationality;
    private int birthYear;

    public AuthorResponseDto(Long id, String name, String nationality, int birthYear) {
        this.id = id;
        this.name = name;
        this.nationality = nationality;
        this.birthYear = birthYear;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getNationality() { return nationality; }
    public int getBirthYear() { return birthYear; }
}
