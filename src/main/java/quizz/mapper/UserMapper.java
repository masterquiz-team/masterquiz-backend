package quizz.mapper;

import java.util.Arrays;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import quizz.dto.user.UserDto;
import quizz.dto.user.UserUpdateDto;
import quizz.model.Avatar;
import quizz.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "avatarId", source = "avatar")
    @Mapping(target = "levelTitle", source = "level.title")
    @Mapping(target = "levelPoints", source = "level.points")
    @Mapping(target = "iconPath", source = "level.iconPath")
    UserDto toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "avatar", source = "avatarId")
    void update(UserUpdateDto userUpdateDto, @MappingTarget User user);

    default Avatar map(String avatarId) {
        if (avatarId == null) {
            return null;
        }

        return Arrays.stream(Avatar.values())
                .filter(avatar -> avatar.getId().equals(avatarId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid avatar id: " + avatarId));
    }
}
