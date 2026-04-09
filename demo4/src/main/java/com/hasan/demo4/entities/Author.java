package com.hasan.demo4.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "author")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    // Bir yazar birden fazla kitap yazmış olabilir (1 taraf)
    // mappedBy → ilişkiyi Book tarafındaki "author" field'ı yönetir
    // LAZY → yazar çekilince kitaplar otomatik yüklenmez, gerekince yüklenir
    @JsonIgnore
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Book> books = new ArrayList<>();

    public Author() {}

    public Author(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }
}
