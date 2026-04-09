package com.hasan.demo4.config;

import com.hasan.demo4.entities.Author;
import com.hasan.demo4.entities.Book;
import com.hasan.demo4.entities.Category;
import com.hasan.demo4.entities.User;
import com.hasan.demo4.repositories.AuthorRepository;
import com.hasan.demo4.repositories.BookRepository;
import com.hasan.demo4.repositories.CategoryRepository;
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
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(BookRepository bookRepository, UserRepository userRepository,
                           CategoryRepository categoryRepository, AuthorRepository authorRepository,
                           PasswordEncoder passwordEncoder) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Kullanıcı yoksa oluştur
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("hasan");
            admin.setPassword(passwordEncoder.encode("1"));
            admin.setRole("ADMIN");
            userRepository.save(admin);

            User user = new User();
            user.setUsername("demo");
            user.setPassword(passwordEncoder.encode("1"));
            user.setRole("USER");
            userRepository.save(user);
            System.out.println(">>> Kullanıcılar oluşturuldu: hasan(ADMIN) / demo(USER)");
        }

        // Kategoriler yoksa oluştur
        if (categoryRepository.count() == 0) {
            String[] categoryNames = {"Roman", "Tarih", "Şiir", "Biyografi", "Bilim Kurgu", "Felsefe", "Deneme"};
            for (String name : categoryNames) {
                categoryRepository.save(new Category(name));
            }
            System.out.println(">>> 7 kategori oluşturuldu.");
        }

        // Yazarlar yoksa oluştur
        if (authorRepository.count() == 0) {
            String[] authorNames = {
                "Orhan Pamuk", "Yaşar Kemal", "Sabahattin Ali", "Halide Edib Adıvar",
                "Ahmet Hamdi Tanpınar", "Peyami Safa", "Reşat Nuri Güntekin", "İhsan Oktay Anar"
            };
            for (String name : authorNames) {
                authorRepository.save(new Author(name));
            }
            System.out.println(">>> 8 yazar oluşturuldu.");
        }

        // Kitap yoksa ekle
        if (bookRepository.count() == 0) {
            List<Author> authors = authorRepository.findAll();

            String[] titles = {
                "Kar", "Roman", "Gece", "Şehir", "Deniz", "Dağ", "Işık", "Rüzgar",
                "Yol", "Köy", "Çarşı", "Bahçe", "Nehir", "Orman"
            };

            List<Category> categories = categoryRepository.findAll();

            List<Book> books = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                Book book = new Book();
                book.setTitle(titles[i % titles.length] + " " + i);
                book.setAuthor(authors.get(i % authors.size()));
                // Her kitaba 1-2 kategori ata (döngüsel)
                book.getCategories().add(categories.get(i % categories.size()));
                if (i % 3 == 0) {
                    book.getCategories().add(categories.get((i + 1) % categories.size()));
                }
                books.add(book);
            }

            bookRepository.saveAll(books);
            System.out.println(">>> 100 test kitabı kategorilerle eklendi.");
        }
    }
}
