package com.hasan.demo4.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BookRequestDto {

    @NotBlank(message = "Kitap adı boş olamaz")
    @Size(min = 2, message = "Kitap adı en az 2 karakter olmalıdır")
    private String title;

    @NotNull(message = "Yazar ID boş olamaz")
    private Long authorId;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
}
