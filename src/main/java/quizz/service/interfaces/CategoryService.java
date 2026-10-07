package quizz.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import quizz.dto.category.CategoryDto;
import quizz.dto.category.CategoryRequestDto;
import quizz.dto.category.CategoryUpdateDto;

public interface CategoryService {

    CategoryDto createCategory(CategoryRequestDto categoryRequestDto);

    CategoryDto getCategory(Long id);

    Page<CategoryDto> getPageOfCategories(Pageable pageable);

    CategoryDto updateCategory(CategoryUpdateDto categoryUpdateDto);

    void deleteCategory(Long id);

}
