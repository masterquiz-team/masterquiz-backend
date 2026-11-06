package quizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quizz.model.UserAchievement;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    boolean existsByUserId(Long id);
}
