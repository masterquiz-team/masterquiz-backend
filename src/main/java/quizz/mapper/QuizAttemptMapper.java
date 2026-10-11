package quizz.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import quizz.dto.quizattempt.QuizAttemptDto;
import quizz.model.QuizAttempt;

@Mapper(componentModel = "spring")
public interface QuizAttemptMapper {

    @Mapping(target = "quizId", source = "quiz.id")
    @Mapping(target = "userId", source = "user.id")
    QuizAttemptDto toDto(QuizAttempt quizAttempt);
}
