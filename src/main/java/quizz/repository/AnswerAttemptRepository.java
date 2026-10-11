package quizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quizz.model.AnswerAttempt;

@Repository
public interface AnswerAttemptRepository extends JpaRepository<AnswerAttempt, Long> {
}
