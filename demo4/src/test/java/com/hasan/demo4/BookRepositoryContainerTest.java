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

import com.hasan.demo4.entities.Author;
import com.hasan.demo4.entities.Book;
import com.hasan.demo4.repositories.AuthorRepository;
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

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void shouldSaveAndFindBook_withRealPostgres() {
        Author author = authorRepository.save(new Author("Yazar"));
        Book book = new Book();
        book.setTitle("Gerçek DB Kitabı");
        book.setAuthor(author);
        repository.save(book);

        List<Book> books = repository.findAll();

        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Gerçek DB Kitabı");
    }

    @Test
    void shouldFindByAuthor_withRealPostgres() {
        Author dostoyevski = authorRepository.save(new Author("Dostoyevski"));

        Book b1 = new Book(); b1.setTitle("Suç ve Ceza"); b1.setAuthor(dostoyevski);
        Book b2 = new Book(); b2.setTitle("Budala");      b2.setAuthor(dostoyevski);
        repository.save(b1);
        repository.save(b2);

        List<Book> result = repository.findByAuthor(dostoyevski);

        assertThat(result).hasSize(2);
    }
}