package quizz.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quizz.model.GamesByCategoryResult;

@Repository
public interface GamesByCategoryResultRepository extends JpaRepository<
        GamesByCategoryResult, Long> {
    Optional<GamesByCategoryResult> findByCategoryIdAndUserId(Long categoryId, Long userId);
}
