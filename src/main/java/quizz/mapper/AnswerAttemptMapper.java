package quizz.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import quizz.dto.answerattempt.AnswerAttemptDto;
import quizz.model.AnswerAttempt;

@Mapper(componentModel = "spring")
public interface AnswerAttemptMapper {
    @Mapping(target = "quizAttemptId", source = "quizAttempt.id")
    @Mapping(target = "answerId", source = "answer.id")
    AnswerAttemptDto toDto(AnswerAttempt answerAttempt);
}
