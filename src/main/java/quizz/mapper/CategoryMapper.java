package quizz.mapper;

import org.mapstruct.Mapper;
import quizz.dto.category.CategoryDto;
import quizz.dto.category.CategoryRequestDto;
import quizz.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    Category toModel(CategoryRequestDto categoryRequestDto);
}
