package com.hasan.authorservice.services;

import com.hasan.authorservice.dto.AuthorResponseDto;
import com.hasan.authorservice.entities.Author;
import com.hasan.authorservice.repositories.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {

    private final AuthorRepository repository;

    public AuthorService(AuthorRepository repository) {
        this.repository = repository;
    }

    public List<AuthorResponseDto> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<AuthorResponseDto> getByName(String name) {
        return repository.findByNameIgnoreCase(name)
                .map(this::toResponse);
    }

    public AuthorResponseDto getById(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Yazar bulunamadı: " + id));
    }

    private AuthorResponseDto toResponse(Author author) {
        return new AuthorResponseDto(
                author.getId(),
                author.getName(),
                author.getNationality(),
                author.getBirthYear()
        );
    }
}
