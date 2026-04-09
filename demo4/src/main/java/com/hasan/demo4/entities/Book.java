package com.hasan.demo4.entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // ManyToOne → N kitap → 1 yazar
    // DB'de book tablosuna "author_id" FK kolonu eklenir
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    // Çok kitap → bir kullanıcıya ait
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    // Bir kitap birden fazla kategoriye ait olabilir → ManyToMany
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "book_category",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    public Book() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public Set<Category> getCategories() { return categories; }
    public void setCategories(Set<Category> categories) { this.categories = categories; }
}
