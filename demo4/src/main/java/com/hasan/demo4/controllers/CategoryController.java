package com.hasan.demo4.controllers;

import com.hasan.demo4.dto.CategoryDto;
import com.hasan.demo4.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Categories", description = "Kitap kategorisi işlemleri")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Tüm kategorileri listele")
    @GetMapping
    public List<CategoryDto> getAll() {
        return categoryService.getAll();
    }

    @Operation(summary = "Yeni kategori oluştur")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody String name) {
        return categoryService.create(name);
    }

    @Operation(summary = "Kategori güncelle")
    @PutMapping("/{id}")
    public CategoryDto update(@PathVariable Long id, @RequestBody String name) {
        return categoryService.update(id, name);
    }

    @Operation(summary = "Kategori sil")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }

    @Operation(summary = "Kitaba kategori ekle")
    @PostMapping("/{categoryId}/books/{bookId}")
    public String addBookToCategory(@PathVariable Long categoryId, @PathVariable Long bookId) {
        return categoryService.addBookToCategory(categoryId, bookId);
    }

    @Operation(summary = "Kategoriye göre kitapları listele")
    @GetMapping("/{categoryId}/books")
    public List<String> getBooksByCategory(@PathVariable Long categoryId) {
        return categoryService.getBooksByCategory(categoryId);
    }
}
