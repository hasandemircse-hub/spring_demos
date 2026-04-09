package com.hasan.authorservice.controllers;

import com.hasan.authorservice.dto.AuthorRequestDto;
import com.hasan.authorservice.dto.AuthorResponseDto;
import com.hasan.authorservice.services.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public List<AuthorResponseDto> getAll() {
        return authorService.getAll();
    }

    @GetMapping("/{id}")
    public AuthorResponseDto getById(@PathVariable Long id) {
        return authorService.getById(id);
    }

    @GetMapping("/search")
    public AuthorResponseDto getByName(@RequestParam String name) {
        return authorService.getByName(name)
                .orElseThrow(() -> new RuntimeException("Yazar bulunamadı: " + name));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponseDto create(@RequestBody AuthorRequestDto request) {
        return authorService.create(request);
    }

    @PutMapping("/{id}")
    public AuthorResponseDto update(@PathVariable Long id, @RequestBody AuthorRequestDto request) {
        return authorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        authorService.delete(id);
    }
}
