package com.hasan.demo4.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hasan.demo4.entities.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByAuthorIgnoreCase(String author);

    List<Book> findByTitleContaining(String keyword);

    // --- PostgreSQL Türkçe sıralama (ICU collation) ---
    // JPQL COLLATE desteklemez → native query zorunlu
    // Pageable burada sadece LIMIT/OFFSET için kullanılır, ORDER BY query'den gelir

    @Query(value = "SELECT * FROM book ORDER BY title COLLATE \"tr-TR-x-icu\" ASC",
           countQuery = "SELECT COUNT(*) FROM book", nativeQuery = true)
    Page<Book> findAllOrderByTitleAsc(Pageable pageable);

    @Query(value = "SELECT * FROM book ORDER BY title COLLATE \"tr-TR-x-icu\" DESC",
           countQuery = "SELECT COUNT(*) FROM book", nativeQuery = true)
    Page<Book> findAllOrderByTitleDesc(Pageable pageable);

    @Query(value = "SELECT * FROM book ORDER BY author COLLATE \"tr-TR-x-icu\" ASC",
           countQuery = "SELECT COUNT(*) FROM book", nativeQuery = true)
    Page<Book> findAllOrderByAuthorAsc(Pageable pageable);

    @Query(value = "SELECT * FROM book ORDER BY author COLLATE \"tr-TR-x-icu\" DESC",
           countQuery = "SELECT COUNT(*) FROM book", nativeQuery = true)
    Page<Book> findAllOrderByAuthorDesc(Pageable pageable);
}

/*
sme Göre Otomatik Sorgu (Query Methods)
JPA metod ismini okuyarak SQL üretir, hiç kod yazmazsın:
javapublic interface UserRepository extends JpaRepository<User, Long> {

    // WHERE name = ?
    List<User> findByName(String name);

    // WHERE city = ? AND age = ?
    List<User> findByCityAndAge(String city, int age);

    // WHERE city = ? OR age = ?
    List<User> findByCityOrAge(String city, int age);

    // WHERE age > ?
    List<User> findByAgeGreaterThan(int age);

    // WHERE age BETWEEN ? AND ?
    List<User> findByAgeBetween(int min, int max);

    // WHERE name LIKE '%?%'
    List<User> findByNameContaining(String keyword);

    // WHERE name LIKE '?%'
    List<User> findByNameStartingWith(String prefix);

    // WHERE active = true
    List<User> findByActiveTrue();

    // WHERE email IS NOT NULL
    List<User> findByEmailNotNull();

    // ORDER BY name ASC
    List<User> findByCityOrderByNameAsc(String city);

    // Sadece 1 kayıt
    Optional<User> findByEmail(String email);

    // Kaç tane var
    int countByCity(String city);

    // Silme
    void deleteByCity(String city);
}

@Query ile Custom Sorgu
Otomatik isim yetmezse kendin yazarsın:
java// JPQL (tablo değil class adı kullanılır)
@Query("SELECT u FROM User u WHERE u.age > :age AND u.active = true")
List<User> findActiveUsersOlderThan(@Param("age") int age);

// Native SQL
@Query(value = "SELECT * FROM users WHERE city = ?1", nativeQuery = true)
List<User> findByCityNative(String city);

// Güncelleme sorgusu
@Modifying
@Transactional
@Query("UPDATE User u SET u.active = false WHERE u.city = :city")
int deactivateByCity(@Param("city") String city);
*/
