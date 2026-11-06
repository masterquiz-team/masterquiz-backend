package quizz.mapper;

import org.mapstruct.Mapper;
import quizz.dto.achievement.AchievementDto;
import quizz.dto.achievement.AchievementRequestDto;
import quizz.model.Achievement;

@Mapper(componentModel = "spring")
public interface AchievementMapper {

    AchievementDto toDto(Achievement achievement);

    Achievement toModel(AchievementRequestDto achievementRequestDto);
}
