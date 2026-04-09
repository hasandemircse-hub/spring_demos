package com.hasan.authorservice.services;

import com.hasan.authorservice.dto.AuthorRequestDto;
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

    public AuthorResponseDto create(AuthorRequestDto request) {
        Author author = new Author(null, request.getName(), request.getNationality(), request.getBirthYear());
        return toResponse(repository.save(author));
    }

    public AuthorResponseDto update(Long id, AuthorRequestDto request) {
        Author author = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Yazar bulunamadı: " + id));
        author.setName(request.getName());
        author.setNationality(request.getNationality());
        author.setBirthYear(request.getBirthYear());
        return toResponse(repository.save(author));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Yazar bulunamadı: " + id);
        }
        repository.deleteById(id);
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
