package quizz.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import quizz.model.UserAchievement;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    @Query("""
            SELECT ua FROM UserAchievement ua
            JOIN FETCH ua.achievement
            JOIN FETCH ua.user
            WHERE ua.user.id = :userId
            AND ua.achievement.id = :achievementId
            """)
    Optional<UserAchievement> findByUserIdAndAchievementId(Long userId, Long achievementId);

    @Query("""
            SELECT ua FROM UserAchievement ua
            JOIN FETCH ua.achievement
            JOIN FETCH ua.user
            WHERE ua.user.id = :userId
            """)
    List<UserAchievement> findAllByUserId(Long id);
}
