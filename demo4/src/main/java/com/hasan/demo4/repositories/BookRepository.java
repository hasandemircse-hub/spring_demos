package com.hasan.demo4.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hasan.demo4.entities.Author;
import com.hasan.demo4.entities.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByAuthor(Author author);

    List<Book> findByTitleContaining(String keyword);

    // --- PostgreSQL Türkçe sıralama (ICU collation) ---
    // author artık FK — sıralama için JOIN gerekli

    @Query(value = "SELECT b.* FROM book b JOIN author a ON b.author_id = a.id ORDER BY b.title COLLATE \"tr-TR-x-icu\" ASC",
           countQuery = "SELECT COUNT(*) FROM book", nativeQuery = true)
    Page<Book> findAllOrderByTitleAsc(Pageable pageable);

    @Query(value = "SELECT b.* FROM book b JOIN author a ON b.author_id = a.id ORDER BY b.title COLLATE \"tr-TR-x-icu\" DESC",
           countQuery = "SELECT COUNT(*) FROM book", nativeQuery = true)
    Page<Book> findAllOrderByTitleDesc(Pageable pageable);

    @Query(value = "SELECT b.* FROM book b JOIN author a ON b.author_id = a.id ORDER BY a.name COLLATE \"tr-TR-x-icu\" ASC",
           countQuery = "SELECT COUNT(*) FROM book", nativeQuery = true)
    Page<Book> findAllOrderByAuthorAsc(Pageable pageable);

    @Query(value = "SELECT b.* FROM book b JOIN author a ON b.author_id = a.id ORDER BY a.name COLLATE \"tr-TR-x-icu\" DESC",
           countQuery = "SELECT COUNT(*) FROM book", nativeQuery = true)
    Page<Book> findAllOrderByAuthorDesc(Pageable pageable);
}
