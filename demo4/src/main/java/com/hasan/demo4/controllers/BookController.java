package com.hasan.demo4.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hasan.demo4.dto.AuthorDto;
import com.hasan.demo4.dto.BookRequestDto;
import com.hasan.demo4.dto.BookResponseDto;
import com.hasan.demo4.dto.PageResponseDto;
import com.hasan.demo4.services.BookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Books", description = "Kitap yönetimi işlemleri")
@RestController
@RequestMapping("/api/books")
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Tüm kitapları listele", description = "Sayfalama olmadan tüm kitapları döner")
    @GetMapping
    public List<BookResponseDto> getAll() {
        log.info("-->book getAll() invoked");
        return bookService.getAll();
    }

    @Operation(summary = "Sayfalı kitap listesi", description = "Sayfalama, sıralama ve yön parametreleriyle kitap listesi döner")
    @GetMapping("/paged")
    public PageResponseDto<BookResponseDto> getAllPaged(
            @Parameter(description = "Sayfa numarası (0'dan başlar)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Sayfa başına kayıt sayısı") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sıralama alanı: id, title, author") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sıralama yönü: asc, desc") @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("-->book getAllPaged() page={} size={} sortBy={} sortDir={}", page, size, sortBy, sortDir);
        return bookService.getAllPaged(page, size, sortBy, sortDir);
    }

    @Operation(summary = "Yeni kitap ekle")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponseDto create(@Valid @RequestBody BookRequestDto request) {
        log.info("-->book create invoked");
        return bookService.create(request);
    }

    @Operation(summary = "ID ile kitap getir")
    @GetMapping("/{id}")
    public BookResponseDto getById(@Parameter(description = "Kitap ID") @PathVariable Long id) {
        log.info("-->book getById: {}", id);
        return bookService.getById(id);
    }

    @Operation(summary = "Kitap güncelle")
    @PutMapping("/{id}")
    public BookResponseDto update(@Parameter(description = "Kitap ID") @PathVariable Long id,
                                  @Valid @RequestBody BookRequestDto request) {
        log.info("-->book update: {}", id);
        return bookService.update(id, request);
    }

    @Operation(summary = "Kitap sil")
    @DeleteMapping("/{id}")
    public void delete(@Parameter(description = "Kitap ID") @PathVariable Long id) {
        log.info("-->book deleted: {}", id);
        bookService.delete(id);
    }

    @Operation(summary = "Yazara göre kitap ara")
    @GetMapping("/search/author")
    public List<BookResponseDto> getByAuthor(@Parameter(description = "Yazar adı") @RequestParam String name) {
        log.info("--->>>Searching for books by author: {}", name);
        return bookService.getByAuthor(name);
    }

    @Operation(summary = "Yazar detayı getir (RestTemplate)", description = "Author Service'den yazar bilgisini RestTemplate ile çeker")
    @GetMapping("/author-detail")
    public AuthorDto getAuthorDetail(@Parameter(description = "Yazar adı") @RequestParam String name) {
        log.info("--->>> [RestTemplate] getAuthorDetail: {}", name);
        return bookService.getAuthorByRestTemplate(name);
    }
}
