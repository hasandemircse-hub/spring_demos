package com.hasan.demo4;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.hasan.demo4.entities.Author;
import com.hasan.demo4.entities.Book;
import com.hasan.demo4.repositories.AuthorRepository;
import com.hasan.demo4.repositories.BookRepository;

@DataJpaTest // Sadece JPA katmanını yükler, H2 kullanır
class BookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void findByAuthor_shouldReturnBooks() {
        Author dostoyevski = authorRepository.save(new Author("Dostoyevski"));

        Book b1 = new Book(); b1.setTitle("Suç ve Ceza"); b1.setAuthor(dostoyevski);
        Book b2 = new Book(); b2.setTitle("Budala");      b2.setAuthor(dostoyevski);
        repository.save(b1);
        repository.save(b2);

        List<Book> result = repository.findByAuthor(dostoyevski);

        assertThat(result).hasSize(2);
    }

    @Test
    void findByTitleContaining_shouldReturnMatchingBooks() {
        Author tolstoy = authorRepository.save(new Author("Tolstoy"));
        Author dostoyevski = authorRepository.save(new Author("Dostoyevski"));

        Book b1 = new Book(); b1.setTitle("Suç ve Ceza");    b1.setAuthor(dostoyevski);
        Book b2 = new Book(); b2.setTitle("Savaş ve Barış"); b2.setAuthor(tolstoy);
        repository.save(b1);
        repository.save(b2);

        List<Book> result = repository.findByTitleContaining("Suç");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Suç ve Ceza");
    }

    @Test
    void save_shouldPersistBook() {
        Author proust = authorRepository.save(new Author("Proust"));

        Book book = new Book();
        book.setTitle("Kayıp Zaman");
        book.setAuthor(proust);
        Book saved = repository.save(book);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Kayıp Zaman");
    }
}
