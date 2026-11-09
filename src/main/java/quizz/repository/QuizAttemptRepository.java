package quizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quizz.model.Difficulty;
import quizz.model.QuizAttempt;
import quizz.model.Status;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    QuizAttempt findTopByUserIdAndStatusOrderByFinishedAtDesc(Long id, Status status);

    int countByUserIdAndStatus(Long id, Status status);

    int countByUserIdAndQuizDifficultyAndStatus(Long userId, Difficulty difficulty, Status status);
}
