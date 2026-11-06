package quizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quizz.model.Achievement;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    boolean existsByTitle(String title);
}
