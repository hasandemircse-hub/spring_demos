package com.hasan.demo4.config;

import com.hasan.demo4.entities.Book;
import com.hasan.demo4.entities.User;
import com.hasan.demo4.repositories.BookRepository;
import com.hasan.demo4.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(BookRepository bookRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Kullanıcı yoksa oluştur
        if (userRepository.count() == 0) {
            User user = new User();
            user.setUsername("hasan");
            user.setPassword(passwordEncoder.encode("1")); // şifre hashlenerek kaydedilir
            user.setRole("USER");
            userRepository.save(user);
            System.out.println(">>> Varsayılan kullanıcı oluşturuldu: hasan / 1");
        }

        // Kitap yoksa ekle
        if (bookRepository.count() == 0) {
            String[] authors = {
                "Orhan Pamuk", "Yaşar Kemal", "Sabahattin Ali", "Halide Edib Adıvar",
                "Ahmet Hamdi Tanpınar", "Peyami Safa", "Reşat Nuri Güntekin", "İhsan Oktay Anar"
            };

            String[] titles = {
                "Kar", "Roman", "Gece", "Şehir", "Deniz", "Dağ", "Işık", "Rüzgar",
                "Yol", "Köy", "Çarşı", "Bahçe", "Nehir", "Orman"
            };

            List<Book> books = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                Book book = new Book();
                book.setTitle(titles[i % titles.length] + " " + i);
                book.setAuthor(authors[i % authors.length]);
                books.add(book);
            }

            bookRepository.saveAll(books);
            System.out.println(">>> 100 test kitabı eklendi.");
        }
    }
}
