package quizz.service.interfaces;

import quizz.dto.quizattempt.QuizAttemptDto;
import quizz.dto.quizattempt.QuizAttemptRequestDto;

public interface QuizAttemptService {
    QuizAttemptDto createQuizAttempt(QuizAttemptRequestDto quizAttemptRequestDto, String email);

    QuizAttemptDto getQuizAttempt(Long id, String email);

    QuizAttemptDto finishQuizAttempt(Long id, String email);
}
