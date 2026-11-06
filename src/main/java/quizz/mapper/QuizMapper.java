package quizz.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import quizz.dto.quiz.QuizDto;
import quizz.dto.quiz.QuizRequestDto;
import quizz.dto.quiz.UpdateQuizDto;
import quizz.model.Quiz;

@Mapper(componentModel = "spring", uses = QuestionMapper.class)
public interface QuizMapper {
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "listOfQuestions", source = "listOfQuestions")
    @Mapping(target = "difficulty", source = "difficulty")
    QuizDto toDto(Quiz quiz);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "listOfQuestions", ignore = true)
    @Mapping(target = "difficulty", source = "difficulty")
    Quiz toModel(QuizRequestDto quizRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "listOfQuestions", ignore = true)
    @Mapping(target = "difficulty", source = "difficulty")
    void update(@MappingTarget Quiz quiz, UpdateQuizDto updateQuizDto);
}
