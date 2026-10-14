package quizz.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import quizz.dto.user.UserDto;
import quizz.dto.user.UserUpdateDto;
import quizz.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    void update(UserUpdateDto userUpdateDto, @MappingTarget User user);
}
