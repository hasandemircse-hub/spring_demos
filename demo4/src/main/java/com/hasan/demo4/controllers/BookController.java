package com.hasan.demo4.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookResponseDto> getAll() {
        log.info("-->book getAll() invoked");
        return bookService.getAll();
    }

    // GET /api/books/paged?page=0&size=10&sortBy=title&sortDir=asc
    @GetMapping("/paged")
    public PageResponseDto<BookResponseDto> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("-->book getAllPaged() page={} size={} sortBy={} sortDir={}", page, size, sortBy, sortDir);
        return bookService.getAllPaged(page, size, sortBy, sortDir);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponseDto create(@Valid @RequestBody BookRequestDto request) {
        log.info("-->book create invoked");
        return bookService.create(request);
    }

    @GetMapping("/{id}")
    public BookResponseDto getById(@PathVariable Long id) {
        log.info("-->book getById: {}", id);
        return bookService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("-->book deleted: {}", id);
        bookService.delete(id);
    }

    @GetMapping("/search/author")
    public List<BookResponseDto> getByAuthor(@RequestParam String name) {
        log.info("--->>>Searching for books by author: {}", name);
        return bookService.getByAuthor(name);
    }

    // RestTemplate örneği — Feign ile aynı işi farklı yöntemle yapar
    // GET /api/books/author-detail?name=Orhan+Pamuk
    @GetMapping("/author-detail")
    public AuthorDto getAuthorDetail(@RequestParam String name) {
        log.info("--->>> [RestTemplate] getAuthorDetail: {}", name);
        return bookService.getAuthorByRestTemplate(name);
    }
}
