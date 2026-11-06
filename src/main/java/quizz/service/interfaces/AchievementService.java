package quizz.service.interfaces;

import java.util.List;
import quizz.dto.achievement.AchievementDto;
import quizz.dto.achievement.AchievementRequestDto;

public interface AchievementService {
    AchievementDto createAchievement(AchievementRequestDto requestDto, String username);

    AchievementDto getAchievementById(Long id, String username);

    List<AchievementDto> getAllAchievements(String username);

    void deleteAchievementById(Long id, String username);
}
