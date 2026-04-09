package com.hasan.demo4.dto;

import java.util.Set;

public class BookResponseDto {

    private Long id;
    private String title;
    private Long authorId;
    private String authorName;
    private String ownerUsername;
    private AuthorDto authorDetail;
    private Set<String> categories;

    public BookResponseDto(Long id, String title, Long authorId, String authorName,
                           String ownerUsername, Set<String> categories) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.authorName = authorName;
        this.ownerUsername = ownerUsername;
        this.categories = categories;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Long getAuthorId() { return authorId; }
    public String getAuthorName() { return authorName; }
    public String getOwnerUsername() { return ownerUsername; }
    public AuthorDto getAuthorDetail() { return authorDetail; }
    public void setAuthorDetail(AuthorDto authorDetail) { this.authorDetail = authorDetail; }
    public Set<String> getCategories() { return categories; }
}
