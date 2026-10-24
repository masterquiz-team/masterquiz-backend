package quizz.mapper;

import org.mapstruct.Mapper;
import quizz.dto.leaderboard.TotalScoreStatsDto;
import quizz.model.User;

@Mapper(componentModel = "spring")
public interface TotalScoreMapper {
    TotalScoreStatsDto toDto(User user);
}
