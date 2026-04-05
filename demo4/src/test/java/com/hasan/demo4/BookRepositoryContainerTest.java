package com.hasan.demo4;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.hasan.demo4.entities.Book;
import com.hasan.demo4.repositories.BookRepository;

@SpringBootTest
@Testcontainers
@Disabled("Docker socket sorunu — CI/CD ortamında çalıştır")
class BookRepositoryContainerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private BookRepository repository;

    @Test
    void shouldSaveAndFindBook_withRealPostgres() {
        repository.save(new Book(null, "Gerçek DB Kitabı", "Yazar"));

        List<Book> books = repository.findAll();

        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Gerçek DB Kitabı");
    }

    @Test
    void shouldFindByAuthor_withRealPostgres() {
        repository.save(new Book(null, "Suç ve Ceza", "Dostoyevski"));
        repository.save(new Book(null, "Budala", "Dostoyevski"));

        List<Book> result = repository.findByAuthorIgnoreCase("dostoyevski");

        assertThat(result).hasSize(2);
    }
}