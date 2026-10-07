package quizz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import quizz.dto.category.CategoryDto;
import quizz.dto.category.CategoryRequestDto;
import quizz.dto.category.CategoryUpdateDto;
import quizz.service.interfaces.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Tag(name = "Category Management", description = "Category management for categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create category", description = "Endpoint to create categories")
    @PreAuthorize("hasRole('User')")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {
       return categoryService.createCategory(categoryRequestDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category", description = "Endpoint to get single category")
    @PreAuthorize("hasRole('User')")
    public CategoryDto getCategory(@PathVariable Long id) {
        return categoryService.getCategory(id);
    }

    @GetMapping
    @Operation(summary = "Get page of categories",
            description = "Endpoint to get page of categories")
    @PreAuthorize("hasRole('User')")
    public Page<CategoryDto> getPageOfCategories(Pageable pageable) {
        return categoryService.getPageOfCategories(pageable);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Endpoint to update a category")
    @PreAuthorize("hasRole('User')")
    public CategoryDto updateCategory(@Valid @RequestBody CategoryUpdateDto categoryUpdateDto) {
        return categoryService.updateCategory(categoryUpdateDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Endpoint to delete a category")
    @PreAuthorize("hasRole('User')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CategoryDto deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }
}
