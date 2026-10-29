package quizz.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import quizz.dto.category.CategoryDto;
import quizz.dto.category.CategoryRequestDto;
import quizz.dto.category.CategoryUpdateDto;
import quizz.exception.EntityFoundException;
import quizz.exception.EntityNotFoundException;
import quizz.mapper.CategoryMapper;
import quizz.model.Category;
import quizz.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    private static final Long ID = 1L;
    private static final String CATEGORY_NAME = "Geography";
    private static final String UPDATED_NAME = "Math";

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void createCategory_ValidRequestDto_ReturnsCategoryDto() {
        //given
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto(CATEGORY_NAME);
        Category category = createCategory();
        CategoryDto expected = new CategoryDto(ID, CATEGORY_NAME);

        when(categoryRepository.findByName(CATEGORY_NAME)).thenReturn(Optional.empty());
        when(categoryMapper.toModel(categoryRequestDto)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        //when
        CategoryDto actual = categoryService.createCategory(categoryRequestDto);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createCategory_InvalidRequestDto_Throws400() {
        //given
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto(CATEGORY_NAME);
        Category category = createCategory();

        when(categoryRepository.findByName(CATEGORY_NAME)).thenReturn(Optional.of(category));

        //when & then
        Assertions.assertThrows(EntityFoundException.class,
                () -> categoryService.createCategory(categoryRequestDto));
    }

    @Test
    void getCategory_ValidId_ReturnsCategoryDto() {
        //given
        Category category = createCategory();
        CategoryDto expected = new CategoryDto(ID, CATEGORY_NAME);
        when(categoryRepository.findById(ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expected);

        //when
        CategoryDto actual = categoryService.getCategory(ID);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getCategory_InvalidId_Throws404() {
        //given
        when(categoryRepository.findById(ID)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.getCategory(ID));
    }

    @Test
    void getPageOfCategories_Valid_ReturnsPageOfCategoryDto() {
        //given
        Category category = createCategory();
        Pageable pageable = Pageable.ofSize(10);
        CategoryDto categoryDto = new CategoryDto(ID, CATEGORY_NAME);
        Page<Category> pages = new PageImpl<>(List.of(category));
        Page<CategoryDto> expected = new PageImpl<>(List.of(categoryDto));
        when(categoryRepository.findAll(pageable)).thenReturn(pages);
        when(categoryMapper.toDto(category)).thenReturn(new CategoryDto(ID, CATEGORY_NAME));

        //when
        Page<CategoryDto> actual = categoryService.getPageOfCategories(pageable);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getPageOfCategories_emptyPage_ReturnsEmptyPage() {
        //given
        Page<Category> pages = new PageImpl<>(List.of());
        Pageable pageable = Pageable.ofSize(10);
        Page<CategoryDto> expected = new PageImpl<>(List.of());
        when(categoryRepository.findAll(pageable)).thenReturn(pages);

        //when
        Page<CategoryDto> actual = categoryService.getPageOfCategories(pageable);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void updateCategory_ValidUpdateCategoryDto_ReturnsCategoryDto() {
        //given
        CategoryUpdateDto categoryUpdateDto = createCategoryUpdateDto();
        Category category = createCategory();
        CategoryDto expected = new CategoryDto(ID, UPDATED_NAME);

        when(categoryRepository.findById(ID)).thenReturn(Optional.of(category));
        when(categoryRepository.findByNameAndIdNot(UPDATED_NAME, ID))
                .thenReturn(Optional.empty());
        when(categoryMapper.toDto(category)).thenReturn(expected);

        //when
        CategoryDto actual = categoryService.updateCategory(categoryUpdateDto);

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(expected.name(), actual.name());
    }

    @Test
    void updateCategory_InvalidId_Throws404() {
        //given
        CategoryUpdateDto categoryUpdateDto = createCategoryUpdateDto();
        when(categoryRepository.findById(ID)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.updateCategory(categoryUpdateDto));
    }

    @Test
    void updateCategory_InvalidUpdatedName_Throws409() {
        //given
        CategoryUpdateDto categoryUpdateDto = createCategoryUpdateDto();
        Category category = createCategory();
        when(categoryRepository.findById(ID)).thenReturn(Optional.of(category));
        when(categoryRepository.findByNameAndIdNot(UPDATED_NAME, ID))
                .thenReturn(Optional.of(category));

        //when & then
        Assertions.assertThrows(EntityFoundException.class,
                () -> categoryService.updateCategory(categoryUpdateDto));
    }

    @Test
    void deleteCategory_ValidId_Ok() {
        //given
        Category category = createCategory();

        //when
        categoryRepository.delete(category);

        //then
        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_InvalidId_Throws404() {
        //given
        when(categoryRepository.findById(ID)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.deleteCategory(ID));
    }

    private CategoryUpdateDto createCategoryUpdateDto() {
        return new CategoryUpdateDto(ID, UPDATED_NAME);
    }

    private Category createCategory() {
        Category category = new Category();
        category.setId(ID);
        category.setName("Geography");
        return category;
    }
}
