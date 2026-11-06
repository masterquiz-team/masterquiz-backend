package quizz.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import quizz.dto.userachievement.UserAchievementDto;
import quizz.model.UserAchievement;

@Mapper(componentModel = "spring")
public interface UserAchievementMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "achievement.id", target = "achievementId")
    @Mapping(source = "achievement.title", target = "title")
    @Mapping(source = "achievement.description", target = "description")
    @Mapping(source = "achievement.rarity", target = "rarity")
    @Mapping(source = "achievement.achievementImage", target = "achievementImage")
    UserAchievementDto toDto(UserAchievement userAchievement);
}
