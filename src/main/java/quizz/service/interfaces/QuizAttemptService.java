package quizz.service.interfaces;

import quizz.dto.quizattempt.QuizAttemptDto;
import quizz.dto.quizattempt.QuizAttemptRandomRequestDto;

public interface QuizAttemptService {
    QuizAttemptDto createQuizAttempt(Long quizId, String email);

    QuizAttemptDto createQuizAttemptWithRandomQuestions(
            QuizAttemptRandomRequestDto quizAttemptRandomRequestDto,
            String email);

    QuizAttemptDto getQuizAttempt(Long id, String email);

    QuizAttemptDto finishQuizAttempt(Long id, String email);
}
