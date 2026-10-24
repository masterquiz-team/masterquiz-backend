package quizz.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import quizz.dto.leaderboard.TotalScoreByCategoryDto;
import quizz.model.GamesByCategoryResult;

@Mapper(componentModel = "spring")
public interface TotalScoreByCategoryMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.nickname", target = "nickname")
    @Mapping(source = "category.name", target = "categoryName")
    TotalScoreByCategoryDto toDto(GamesByCategoryResult gamesByCategoryResult);
}
