package quizz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quizz.dto.category.CategoryDto;
import quizz.dto.category.CategoryRequestDto;
import quizz.dto.category.CategoryUpdateDto;
import quizz.exception.EntityFoundException;
import quizz.exception.EntityNotFoundException;
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
        doesExist(categoryRequestDto.name());
        Category category = categoryMapper.toModel(categoryRequestDto);
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
        Category category = findById(categoryUpdateDto.id());
        checkNameAvailability(categoryUpdateDto);
        category.setName(categoryUpdateDto.name());
        return categoryMapper.toDto(category);
    }

    @Transactional
    @Override
    public void deleteCategory(Long id) {
        categoryRepository.delete(findById(id));
    }

    private Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category with id: " + id + " doesn't exist"));
    }

    private void doesExist(String name) {
        if (categoryRepository.findByName(name).isPresent()) {
            throw new EntityFoundException("Category with name: " + name + " already exist");
        }
    }

    private void checkNameAvailability(CategoryUpdateDto dto) {
        if (categoryRepository.findByNameAndIdNot(dto.name(), dto.id()).isPresent()) {
            throw new EntityFoundException(
                    "Category with name: " + dto.name() + " already exists");
        }
    }
}
