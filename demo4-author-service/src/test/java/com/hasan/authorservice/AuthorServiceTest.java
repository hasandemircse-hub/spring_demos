package com.hasan.authorservice;

import com.hasan.authorservice.dto.AuthorRequestDto;
import com.hasan.authorservice.dto.AuthorResponseDto;
import com.hasan.authorservice.entities.Author;
import com.hasan.authorservice.repositories.AuthorRepository;
import com.hasan.authorservice.services.AuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository repository;

    private AuthorService authorService;
    private Author author;

    @BeforeEach
    void setUp() {
        authorService = new AuthorService(repository);
        author = new Author(1L, "Orhan Pamuk", "Türk", 1952);
    }

    @Nested
    @DisplayName("getAll()")
    class GetAll {

        @Test
        @DisplayName("tüm yazarları listeler")
        void shouldReturnAllAuthors() {
            when(repository.findAll()).thenReturn(List.of(author));

            List<AuthorResponseDto> result = authorService.getAll();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Orhan Pamuk");
            assertThat(result.get(0).getNationality()).isEqualTo("Türk");
        }

        @Test
        @DisplayName("yazar yoksa boş liste döner")
        void shouldReturnEmptyList() {
            when(repository.findAll()).thenReturn(List.of());

            List<AuthorResponseDto> result = authorService.getAll();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getById()")
    class GetById {

        @Test
        @DisplayName("yazar varsa DTO döner")
        void shouldReturnAuthor() {
            when(repository.findById(1L)).thenReturn(Optional.of(author));

            AuthorResponseDto result = authorService.getById(1L);

            assertThat(result.getName()).isEqualTo("Orhan Pamuk");
            assertThat(result.getBirthYear()).isEqualTo(1952);
        }

        @Test
        @DisplayName("yazar yoksa exception fırlatır")
        void shouldThrowWhenNotFound() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authorService.getById(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("yazar kaydedilir ve DTO döner")
        void shouldSaveAndReturnAuthor() {
            AuthorRequestDto request = new AuthorRequestDto();
            request.setName("Yaşar Kemal");
            request.setNationality("Türk");
            request.setBirthYear(1923);

            when(repository.save(any(Author.class))).thenReturn(
                    new Author(2L, "Yaşar Kemal", "Türk", 1923));

            AuthorResponseDto result = authorService.create(request);

            assertThat(result.getName()).isEqualTo("Yaşar Kemal");
            assertThat(result.getId()).isEqualTo(2L);
            verify(repository, times(1)).save(any(Author.class));
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("yazar bilgileri güncellenir")
        void shouldUpdateAuthor() {
            AuthorRequestDto request = new AuthorRequestDto();
            request.setName("Orhan Pamuk");
            request.setNationality("Türk");
            request.setBirthYear(1952);

            when(repository.findById(1L)).thenReturn(Optional.of(author));
            when(repository.save(any(Author.class))).thenReturn(author);

            AuthorResponseDto result = authorService.update(1L, request);

            assertThat(result.getName()).isEqualTo("Orhan Pamuk");
        }

        @Test
        @DisplayName("yazar yoksa exception fırlatır")
        void shouldThrowWhenNotFound() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            AuthorRequestDto request = new AuthorRequestDto();
            assertThatThrownBy(() -> authorService.update(99L, request))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("yazar varsa silinir")
        void shouldDelete() {
            when(repository.existsById(1L)).thenReturn(true);

            authorService.delete(1L);

            verify(repository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("yazar yoksa exception, deleteById çağrılmaz")
        void shouldThrowWhenNotFound() {
            when(repository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> authorService.delete(99L))
                    .isInstanceOf(RuntimeException.class);

            verify(repository, never()).deleteById(any());
        }
    }
}
