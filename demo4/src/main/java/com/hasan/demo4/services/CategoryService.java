package com.hasan.demo4.services;

import com.hasan.demo4.dto.CategoryDto;
import com.hasan.demo4.entities.Book;
import com.hasan.demo4.entities.Category;
import com.hasan.demo4.exception.ResourceNotFoundException;
import com.hasan.demo4.repositories.BookRepository;
import com.hasan.demo4.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    public CategoryService(CategoryRepository categoryRepository, BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }

    public List<CategoryDto> getAll() {
        return categoryRepository.findAll().stream()
                .map(c -> new CategoryDto(c.getId(), c.getName()))
                .toList();
    }

    public CategoryDto create(String name) {
        Category category = new Category(name.trim());
        category = categoryRepository.save(category);
        return new CategoryDto(category.getId(), category.getName());
    }

    public CategoryDto update(Long id, String name) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı: " + id));
        category.setName(name.trim());
        category = categoryRepository.save(category);
        return new CategoryDto(category.getId(), category.getName());
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kategori bulunamadı: " + id);
        }
        categoryRepository.deleteById(id);
    }

    public String addBookToCategory(Long categoryId, Long bookId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı: " + categoryId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Kitap bulunamadı: " + bookId));
        book.getCategories().add(category);
        bookRepository.save(book);
        return "'" + book.getTitle() + "' kitabına '" + category.getName() + "' kategorisi eklendi";
    }

    public List<String> getBooksByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı: " + categoryId));
        return category.getBooks().stream()
                .map(Book::getTitle)
                .toList();
    }
}
