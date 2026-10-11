package quizz.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quizz.model.AnswerAttempt;

@Repository
public interface AnswerAttemptRepository extends JpaRepository<AnswerAttempt, Long> {
    boolean existsByQuizAttemptIdAndAnswerQuestionId(
            Long quizAttemptId, Long questionId);

    List<AnswerAttempt> findByQuizAttemptId(Long id);
}
