package quizz.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quizz.dto.category.CategoryDto;
import quizz.dto.category.CategoryRequestDto;
import quizz.dto.category.CategoryUpdateDto;
import quizz.exception.EntityFoundException;
import quizz.mapper.CategoryMapper;
import quizz.model.Category;
import quizz.repository.CategoryRepository;
import quizz.service.interfaces.CategoryService;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    @Override
    public CategoryDto createCategory(CategoryRequestDto categoryRequestDto) {
        Category category = categoryRepository.findByName(categoryRequestDto.name()).orElseThrow(
                () -> new EntityFoundException("Category with name: "
                        + categoryRequestDto.name() + " already exist"));
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getCategory(Long id) {
        return categoryMapper.toDto(findById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CategoryDto> getPageOfCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toDto);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(CategoryUpdateDto categoryUpdateDto) {
        Category category = categoryRepository.findById(categoryUpdateDto.id()).orElseThrow(
                () -> new EntityNotFoundException("Category with id: " + categoryUpdateDto.id()
                        + " doesn't exist"));
        category.setName(categoryUpdateDto.name());
        return categoryMapper.toDto(category);
    }

    @Transactional
    @Override
    public CategoryDto deleteCategory(Long id) {
        Category category = findById(id);
        CategoryDto categoryDto = categoryMapper.toDto(category);
        categoryRepository.delete(category);
        return categoryDto;
    }

    private Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category with id: " + id + " doesn't exist"));
    }
}
