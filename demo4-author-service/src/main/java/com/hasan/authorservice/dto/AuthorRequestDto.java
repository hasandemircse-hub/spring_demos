package com.hasan.authorservice.dto;

public class AuthorRequestDto {
    private String name;
    private String nationality;
    private int birthYear;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public int getBirthYear() { return birthYear; }
    public void setBirthYear(int birthYear) { this.birthYear = birthYear; }
}
