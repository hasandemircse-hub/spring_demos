package com.hasan.demo4.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// İstemciden gelen veriyi taşır (POST/PUT isteği body'si)
// Validasyon anotasyonları burada olur — Entity'de değil
public class BookRequestDto {

    @NotBlank(message = "Kitap adı boş olamaz")
    @Size(min = 2, message = "Kitap adı en az 2 karakter olmalıdır")
    private String title;

    @NotBlank(message = "Yazar adı boş olamaz")
    private String author;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
}
