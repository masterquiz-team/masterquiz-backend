package quizz.service.interfaces;

import quizz.dto.quizattempt.QuizAttemptDto;

public interface QuizAttemptService {
    QuizAttemptDto createQuizAttempt(Long quizId, String email);

    QuizAttemptDto getQuizAttempt(Long id, String email);

    QuizAttemptDto finishQuizAttempt(Long id, String email);
}
