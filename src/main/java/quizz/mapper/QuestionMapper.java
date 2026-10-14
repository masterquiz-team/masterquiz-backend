package quizz.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import quizz.dto.question.QuestionDto;
import quizz.dto.question.QuestionRequestDto;
import quizz.dto.question.UpdateQuestionDto;
import quizz.model.Question;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    @Mapping(source = "difficulty", target = "difficultyName")
    QuestionDto toDto(Question question);

    @Mapping(source = "difficultyName", target = "difficulty")
    Question toModel(QuestionRequestDto questionRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "difficultyName", target = "difficulty")
    void update(UpdateQuestionDto updateQuestionDto, @MappingTarget Question question);
}
