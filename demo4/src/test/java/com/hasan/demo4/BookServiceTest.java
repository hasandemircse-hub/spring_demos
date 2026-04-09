package com.hasan.demo4;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.hasan.demo4.clients.AuthorClient;
import com.hasan.demo4.dto.BookRequestDto;
import com.hasan.demo4.dto.BookResponseDto;
import com.hasan.demo4.dto.PageResponseDto;
import com.hasan.demo4.entities.Author;
import com.hasan.demo4.entities.Book;
import com.hasan.demo4.exception.ResourceNotFoundException;
import com.hasan.demo4.repositories.AuthorRepository;
import com.hasan.demo4.repositories.BookRepository;
import com.hasan.demo4.services.BookService;
import com.hasan.demo4.strategy.AuthorSortStrategy;
import com.hasan.demo4.strategy.IdSortStrategy;
import com.hasan.demo4.strategy.TitleSortStrategy;

// @ExtendWith(MockitoExtension.class) → JUnit 5'e "Mockito kullanacağım" der
// Bu olmadan @Mock ve @InjectMocks anotasyonları çalışmaz
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    // @Mock → BookRepository'nin sahte (fake) versiyonunu oluşturur
    // Gerçek veritabanına bağlanmaz — sadece "davranışını taklit eder"
    // Biz when(...).thenReturn(...) ile ne döndüreceğini söyleriz
    @Mock
    private BookRepository repository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorClient authorClient;

    // @InjectMocks yerine manuel oluşturuyoruz:
    // @InjectMocks List<BookSortStrategy> gibi generic tipler için mock inject edemez.
    // Bu yüzden gerçek strategy nesnelerini geçirerek BookService'i elle kuruyoruz.
    private BookService bookService;

    // Test verilerini burada tanımlıyoruz — her testten önce @BeforeEach ile sıfırlanır
    private Book book;
    private Author author;
    private BookRequestDto requestDto;

    // @BeforeEach → her @Test metodundan ÖNCE çalışır
    // Her test temiz, bağımsız bir başlangıç noktasından başlar
    @BeforeEach
    void setUp() {
        bookService = new BookService(
            repository,
            authorRepository,
            authorClient,
            null, // RestTemplate — Mockito mock'layamıyor, bu testlerde kullanılmıyor
            List.of(new IdSortStrategy(), new TitleSortStrategy(), new AuthorSortStrategy())
        );

        author = new Author("Dostoyevski");
        author.setId(1L);

        book = new Book();
        book.setId(1L);
        book.setTitle("Suç ve Ceza");
        book.setAuthor(author);

        requestDto = new BookRequestDto();
        requestDto.setTitle("Suç ve Ceza");
        requestDto.setAuthorId(1L);
    }

    // @Nested → ilgili testleri bir sınıf altında gruplar
    // Test çıktısında "BookServiceTest > GetAll > kitap varsa liste döner" şeklinde görünür
    // Okunabilirliği ve organizasyonu artırır
    @Nested
    @DisplayName("getAll()")
    class GetAll {

        @Test
        @DisplayName("kitap varsa liste döner")
        void shouldReturnAllBooks() {
            // ARRANGE: repository.findAll() çağrılınca 1 kitap dönsün
            when(repository.findAll()).thenReturn(List.of(book));

            // ACT: test etmek istediğimiz metodu çağır
            List<BookResponseDto> result =bookService.getAll();

            // ASSERT: sonucu doğrula
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Suç ve Ceza");

            // verify → repository.findAll() tam 1 kez çağrıldı mı?
            // 2 kez çağrılsaydı test başarısız olurdu
            verify(repository, times(1)).findAll();
        }

        @Test
        @DisplayName("kitap yoksa boş liste döner")
        void shouldReturnEmptyList_whenNoBooksExist() {
            // DB boş dönsün
            when(repository.findAll()).thenReturn(Collections.emptyList());

            List<BookResponseDto> result = bookService.getAll();

            // null değil, boş liste dönmeli — ikisi farklı davranış
            // null olsaydı frontend'de NullPointerException olurdu
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getById()")
    class GetById {

        @Test
        @DisplayName("kitap varsa DTO döner")
        void shouldReturnBook_whenExists() {
            // Optional.of(book) → kitap bulundu senaryosu
            when(repository.findById(1L)).thenReturn(Optional.of(book));

            BookResponseDto result = bookService.getById(1L);

            // sadece author değil, tüm alanları kontrol et
            assertThat(result.getTitle()).isEqualTo("Suç ve Ceza");
            assertThat(result.getAuthorName()).isEqualTo("Dostoyevski");
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("kitap yoksa exception fırlatır")
        void shouldThrowException_whenNotFound() {
            // Optional.empty() → kitap bulunamadı senaryosu
            when(repository.findById(99L)).thenReturn(Optional.empty());

            // assertThatThrownBy → lambda içindeki kod exception fırlatmalı
            // isInstanceOf → doğru exception türü mü?
            // hasMessageContaining → hata mesajında "99" geçiyor mu?
            assertThatThrownBy(() -> bookService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("kitap kaydedilir ve DTO döner")
        void shouldSaveAndReturnBook() {
            when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
            // any(Book.class) → repository.save() hangi Book nesnesiyle çağrılırsa çağrılsın
            // book dönsün — nesnenin tam eşitliğini kontrol etmiyoruz
            when(repository.save(any(Book.class))).thenReturn(book);

            BookResponseDto result = bookService.create(requestDto);

            assertThat(result.getTitle()).isEqualTo("Suç ve Ceza");
            assertThat(result.getAuthorName()).isEqualTo("Dostoyevski");
            assertThat(result.getId()).isEqualTo(1L);

            // save() tam 1 kez çağrıldı mı?
            verify(repository, times(1)).save(any(Book.class));
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("kitap varsa silinir")
        void shouldCallDeleteById_whenExists() {
            when(repository.existsById(1L)).thenReturn(true);

            bookService.delete(1L);

            // deleteById çağrıldı mı?
            verify(repository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("kitap yoksa exception fırlatır, deleteById çağrılmaz")
        void shouldThrowException_whenNotFound() {
            when(repository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> bookService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

            // never() → deleteById hiç çağrılmamalı
            // exception fırlatıldığında deleteById'ye kadar ulaşılmaması gerekir
            // bu olmadan kod yanlış çalışsa bile test geçebilir
            verify(repository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("getByAuthor()")
    class GetByAuthor {

        @Test
        @DisplayName("yazara göre kitap listesi döner")
        void shouldReturnBooks_byAuthor() {
            when(authorRepository.findByNameIgnoreCase("Dostoyevski"))
                .thenReturn(Optional.of(author));
            when(repository.findByAuthor(author))
                .thenReturn(List.of(book));

            List<BookResponseDto> result = bookService.getByAuthor("Dostoyevski");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAuthorName()).isEqualTo("Dostoyevski");
        }

        @Test
        @DisplayName("yazar bulunamazsa exception fırlatır")
        void shouldThrowException_whenAuthorNotFound() {
            when(authorRepository.findByNameIgnoreCase("Bilinmiyor"))
                .thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookService.getByAuthor("Bilinmiyor"))
                .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getAllPaged()")
    class GetAllPaged {

        @Test
        @DisplayName("sayfalı liste döner, metadata doğru")
        void shouldReturnPagedBooks() {
            // PageImpl → Page interface'inin test için kullanılan gerçek implementasyonu
            // 1 kitap içeriyor, sayfa boyutu 10, toplam 50 kayıt var
            // Spring bunu alıp totalPages = 50/10 = 5 hesaplar
            Page<Book> mockPage = new PageImpl<>(
                List.of(book),      // bu sayfadaki içerik
                Pageable.ofSize(10), // sayfa boyutu
                50L                 // toplam kayıt sayısı
            );

            // any(Pageable.class) → hangi Pageable nesnesi gelirse gelsin mockPage dönsün
            when(repository.findAll(any(Pageable.class))).thenReturn(mockPage);

            PageResponseDto<BookResponseDto> result = bookService.getAllPaged(0, 10, "title", "asc");

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(50);
            assertThat(result.getTotalPages()).isEqualTo(5);
            // 50 kayıt / 10 boyut = 5 sayfa, sayfa 0'dayız → son sayfa değil
            assertThat(result.isLast()).isFalse();
        }

        @Test
        @DisplayName("boş sayfa döner")
        void shouldReturnEmptyPage_whenNoBooks() {
            Page<Book> emptyPage = new PageImpl<>(
                Collections.emptyList(),
                Pageable.ofSize(10),
                0L                  // toplam 0 kayıt
            );
            when(repository.findAll(any(Pageable.class))).thenReturn(emptyPage);

            PageResponseDto<BookResponseDto> result = bookService.getAllPaged(0, 10, "title", "asc");

            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
            // 0 kayıt = 0 sayfa, ilk sayfa aynı zamanda son sayfa
            assertThat(result.isLast()).isTrue();
        }
    }
}
