package quizz.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quizz.model.Achievement;
import quizz.model.AchievementType;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    boolean existsByTitle(String title);

    Optional<Achievement> findByAchievementType(AchievementType achievementType);
}
