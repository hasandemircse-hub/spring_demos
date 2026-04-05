package com.hasan.demo4;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.hasan.demo4.entities.Book;
import com.hasan.demo4.repositories.BookRepository;

@DataJpaTest // Sadece JPA katmanını yükler, H2 kullanır
class BookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Test
    void findByAuthorIgnoreCase_shouldReturnBooks() {
        repository.save(new Book(null, "Suç ve Ceza", "Dostoyevski"));
        repository.save(new Book(null, "Budala", "dostoyevski")); // küçük harf

        List<Book> result = repository.findByAuthorIgnoreCase("dostoyevski");

        assertThat(result).hasSize(2);
    }

    @Test
    void findByTitleContaining_shouldReturnMatchingBooks() {
        repository.save(new Book(null, "Suç ve Ceza", "Dostoyevski"));
        repository.save(new Book(null, "Savaş ve Barış", "Tolstoy"));

        List<Book> result = repository.findByTitleContaining("Suç");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Suç ve Ceza");
    }

    @Test
    void save_shouldPersistBook() {
        Book saved = repository.save(new Book(null, "Kayıp Zaman", "Proust"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Kayıp Zaman");
    }
}