package quizz.service.interfaces;

import java.util.List;
import quizz.dto.userachievement.UserAchievementDto;

public interface UserAchievementService {
    UserAchievementDto getUserAchievement(Long achievementId, String username);

    List<UserAchievementDto> getListOfUserAchievements(String username);

    void unlockAchievement(Long achievementId, String username);
}
