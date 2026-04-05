package com.hasan.demo4.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.hasan.demo4.clients.AuthorClient;
import com.hasan.demo4.dto.AuthorDto;
import com.hasan.demo4.dto.BookRequestDto;
import com.hasan.demo4.dto.BookResponseDto;
import com.hasan.demo4.dto.PageResponseDto;
import com.hasan.demo4.entities.Book;
import com.hasan.demo4.exception.ResourceNotFoundException;
import com.hasan.demo4.repositories.BookRepository;

@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository repository;

    // --- Feign Client: interface tanımla, Spring implement eder ---
    private final AuthorClient authorClient;

    // --- RestTemplate: klasik HTTP istemcisi, elle URL + çağrı yazılır ---
    private final RestTemplate restTemplate;

    @Value("${author.service.url:http://localhost:8081}")
    private String authorServiceUrl;

    public BookService(BookRepository repository, AuthorClient authorClient, RestTemplate restTemplate) {
        this.repository = repository;
        this.authorClient = authorClient;
        this.restTemplate = restTemplate;
    }

    @Cacheable("books")
    public List<BookResponseDto> getAll() {
        log.info("[CACHE MISS] getAll → DB'ye gidiliyor");
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Cacheable(value = "books-paged", key = "#page + '-' + #size + '-' + #sortBy")
    public PageResponseDto<BookResponseDto> getAllPaged(int page, int size, String sortBy) {
        log.info("[CACHE MISS] getAllPaged → DB'ye gidiliyor (page={}, size={}, sortBy={})", page, size, sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Book> bookPage = repository.findAll(pageable);
        List<BookResponseDto> content = bookPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new PageResponseDto<>(
                content,
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages(),
                bookPage.isLast()
        );
    }

    @Cacheable(value = "book", key = "#id")
    public BookResponseDto getById(Long id) {
        log.info("[CACHE MISS] getById({}) → DB'ye gidiliyor", id);
        Book book = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kitap bulunamadı: " + id));
        BookResponseDto response = toResponse(book);

        // Feign Client ile Author Service'den yazar detayını çek
        // RestTemplate'e göre fark: URL, HTTP kodu yok — sadece metod çağrısı
        try {
            var authorDetail = authorClient.findByName(book.getAuthor());
            response.setAuthorDetail(authorDetail);
            log.info("Author Service'den yazar detayı alındı: {}", book.getAuthor());
        } catch (Exception e) {
            log.warn("Author Service'e ulaşılamadı: {}", e.getMessage());
        }

        return response;
    }

    // Yeni kitap eklenince cache'i temizle — eski liste artık geçersiz
    @CacheEvict(value = {"books", "books-paged"}, allEntries = true)
    public BookResponseDto create(BookRequestDto request) {
        Book book = toEntity(request);  // DTO → Entity
        Book saved = repository.save(book);
        return toResponse(saved);       // Entity → DTO
    }

    // Kitap silinince: o id'nin cache'ini + liste cache'lerini temizle
    @Caching(evict = {
        @CacheEvict(value = "book", key = "#id"),
        @CacheEvict(value = {"books", "books-paged"}, allEntries = true)
    })
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Kitap bulunamadı: " + id);
        }
        repository.deleteById(id);
    }

    // -------------------------------------------------------------------------
    // REST TEMPLATE ÖRNEĞİ
    // Feign ile getById() aynı işi yapar — fark sadece nasıl HTTP çağrısı yapıldığı
    //
    // Feign   → @FeignClient + interface metodu   → Spring kodu yazar
    // RestTemplate → URL'i elle yaz + getForObject() → sen kodu yazarsın
    // -------------------------------------------------------------------------
    public AuthorDto getAuthorByRestTemplate(String authorName) {
        // 1. URL'i elle oluştururuz (Feign'de bu yoktu, @GetMapping yeterliydi)
        String url = authorServiceUrl + "/api/authors/search?name=" + authorName;

        // 2. HTTP GET isteği at, yanıtı AuthorDto'ya dönüştür
        // getForObject → yanıt body'sini direkt nesneye parse eder
        // Alternatif: getForEntity → ResponseEntity döner (status code + headers da gelir)
        try {
            AuthorDto author = restTemplate.getForObject(url, AuthorDto.class);
            log.info("[RestTemplate] Author Service'den yazar alındı: {}", authorName);
            return author;
        } catch (RestClientException e) {
            // Feign'de de aynı şekilde try-catch kullandık
            log.warn("[RestTemplate] Author Service'e ulaşılamadı: {}", e.getMessage());
            return null;
        }
    }

    public List<BookResponseDto> getByAuthor(String name) {
        return repository.findByAuthorIgnoreCase(name)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // DTO → Entity (istemciden gelen veriyi DB'ye yazmak için)
    private Book toEntity(BookRequestDto dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        return book;
    }

    // Entity → DTO (DB'den gelen veriyi istemciye göndermek için)
    private BookResponseDto toResponse(Book book) {
        // owner null olabilir (eski kayıtlar veya sahipsiz kitaplar)
        String ownerUsername = book.getOwner() != null ? book.getOwner().getUsername() : null;
        return new BookResponseDto(book.getId(), book.getTitle(), book.getAuthor(), ownerUsername);
    }
}
