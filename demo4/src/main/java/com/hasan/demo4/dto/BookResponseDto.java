package com.hasan.demo4.dto;

// İstemciye gönderilecek veriyi taşır (GET/POST yanıtı)
// Sadece dışarı çıkmasını istediğin alanlar burada olur
public class BookResponseDto {

    private Long id;
    private String title;
    private String author;
    private String ownerUsername;
    private AuthorDto authorDetail; // Author Service'den gelen detay — null olabilir

    public BookResponseDto(Long id, String title, String author, String ownerUsername) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.ownerUsername = ownerUsername;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getOwnerUsername() { return ownerUsername; }
    public AuthorDto getAuthorDetail() { return authorDetail; }
    public void setAuthorDetail(AuthorDto authorDetail) { this.authorDetail = authorDetail; }
}
