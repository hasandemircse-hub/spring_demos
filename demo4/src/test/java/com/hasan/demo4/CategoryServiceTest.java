package com.hasan.demo4;

import com.hasan.demo4.dto.CategoryDto;
import com.hasan.demo4.entities.Category;
import com.hasan.demo4.exception.ResourceNotFoundException;
import com.hasan.demo4.repositories.BookRepository;
import com.hasan.demo4.repositories.CategoryRepository;
import com.hasan.demo4.services.CategoryService;
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
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    private CategoryService categoryService;
    private Category category;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryRepository, bookRepository);
        category = new Category("Roman");
        category.setId(1L);
    }

    @Nested
    @DisplayName("getAll()")
    class GetAll {

        @Test
        @DisplayName("tüm kategorileri listeler")
        void shouldReturnAllCategories() {
            when(categoryRepository.findAll()).thenReturn(List.of(category));

            List<CategoryDto> result = categoryService.getAll();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Roman");
            assertThat(result.get(0).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("kategori yoksa boş liste döner")
        void shouldReturnEmptyList() {
            when(categoryRepository.findAll()).thenReturn(List.of());

            List<CategoryDto> result = categoryService.getAll();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("kategori kaydedilir ve DTO döner")
        void shouldSaveAndReturnCategory() {
            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            CategoryDto result = categoryService.create("Roman");

            assertThat(result.getName()).isEqualTo("Roman");
            verify(categoryRepository, times(1)).save(any(Category.class));
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("kategori adı güncellenir")
        void shouldUpdateCategoryName() {
            Category updated = new Category("Şiir");
            updated.setId(1L);
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.save(any(Category.class))).thenReturn(updated);

            CategoryDto result = categoryService.update(1L, "Şiir");

            assertThat(result.getName()).isEqualTo("Şiir");
        }

        @Test
        @DisplayName("kategori yoksa exception fırlatır")
        void shouldThrowWhenNotFound() {
            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.update(99L, "X"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("kategori varsa silinir")
        void shouldDelete() {
            when(categoryRepository.existsById(1L)).thenReturn(true);

            categoryService.delete(1L);

            verify(categoryRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("kategori yoksa exception, deleteById çağrılmaz")
        void shouldThrowWhenNotFound() {
            when(categoryRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> categoryService.delete(99L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(categoryRepository, never()).deleteById(any());
        }
    }
}
