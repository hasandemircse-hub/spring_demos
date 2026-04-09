package com.hasan.demo4.repositories;

import com.hasan.demo4.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByNameIgnoreCase(String name);
}
