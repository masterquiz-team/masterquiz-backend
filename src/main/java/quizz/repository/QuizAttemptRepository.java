package quizz.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quizz.model.Difficulty;
import quizz.model.QuizAttempt;
import quizz.model.Status;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUserIdAndStatus(Long id, Status status);

    List<QuizAttempt> findByUserIdAndQuizDifficultyAndStatus(
            Long userId, Difficulty difficulty, Status status);
}
