package com.hasan.authorservice.controllers;

import com.hasan.authorservice.dto.AuthorResponseDto;
import com.hasan.authorservice.services.AuthorService;
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

    // Book Service bu endpoint'i çağıracak — yazar adına göre bilgi al
    @GetMapping("/search")
    public AuthorResponseDto getByName(@RequestParam String name) {
        return authorService.getByName(name)
                .orElseThrow(() -> new RuntimeException("Yazar bulunamadı: " + name));
    }
}
