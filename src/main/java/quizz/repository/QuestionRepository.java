package quizz.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quizz.model.Category;
import quizz.model.Difficulty;
import quizz.model.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findByText(String text);

    List<Question> findByDifficultyAndCategory(Difficulty difficulty, Category category);
}
