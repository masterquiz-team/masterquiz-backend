package quizz.service.interfaces;

import java.util.List;
import quizz.dto.answerattempt.AnswerAttemptDto;
import quizz.dto.answerattempt.AnswerAttemptRequestDto;

public interface AnswerAttemptService {

    AnswerAttemptDto createAnswerAttempt(
            AnswerAttemptRequestDto answerAttemptRequestDto,
            String email);

    List<AnswerAttemptDto> getAnswerAttemptsByQuizAttemptId(Long id, String email);
}
