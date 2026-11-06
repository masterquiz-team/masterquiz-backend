package quizz.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import quizz.dto.leaderboard.TotalScoreStatsDto;
import quizz.model.User;

@Mapper(componentModel = "spring")
public interface TotalScoreMapper {
    @Mapping(target = "userId", source = "user.id")
    TotalScoreStatsDto toDto(User user);
}
