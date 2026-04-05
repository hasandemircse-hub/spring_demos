package com.hasan.authorservice.config;

import com.hasan.authorservice.entities.Author;
import com.hasan.authorservice.repositories.AuthorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AuthorRepository repository;

    public DataInitializer(AuthorRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;

        repository.save(new Author(null, "Orhan Pamuk", "Türk", 1952));
        repository.save(new Author(null, "Yaşar Kemal", "Türk", 1923));
        repository.save(new Author(null, "Sabahattin Ali", "Türk", 1907));
        repository.save(new Author(null, "Dostoyevski", "Rus", 1821));
        repository.save(new Author(null, "Tolstoy", "Rus", 1828));
        repository.save(new Author(null, "Kafka", "Avusturya", 1883));

        System.out.println(">>> 6 yazar eklendi.");
    }
}
