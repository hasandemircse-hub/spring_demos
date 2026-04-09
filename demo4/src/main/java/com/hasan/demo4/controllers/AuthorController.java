package com.hasan.demo4.controllers;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hasan.demo4.entities.Author;
import com.hasan.demo4.exception.ResourceNotFoundException;
import com.hasan.demo4.repositories.AuthorRepository;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @GetMapping
    public List<Author> getAll() {
        return authorRepository.findAll();
    }

    @PostMapping
    public Author create(@RequestBody Author author) {
        return authorRepository.save(author);
    }

    @CacheEvict(value = {"books", "books-paged", "book"}, allEntries = true)
    @PutMapping("/{id}")
    public Author update(@PathVariable Long id, @RequestBody Author request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yazar bulunamadı: " + id));
        author.setName(request.getName());
        return authorRepository.save(author);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Yazar bulunamadı: " + id);
        }
        authorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
