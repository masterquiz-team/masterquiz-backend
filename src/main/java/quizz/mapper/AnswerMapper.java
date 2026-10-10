package quizz.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import quizz.dto.answer.AnswerDto;
import quizz.dto.answer.AnswerRequestDto;
import quizz.dto.answer.UpdateAnswerDto;
import quizz.model.Answer;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    Answer toModel(AnswerRequestDto answerRequestDto);

    AnswerDto toDto(Answer answer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAnswer(UpdateAnswerDto updateAnswerDto, @MappingTarget Answer answer);
}
