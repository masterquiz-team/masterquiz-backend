package quizz.mapper;

import org.mapstruct.Mapper;
import quizz.dto.register.UserRequestDto;
import quizz.dto.register.UserResponseDto;
import quizz.model.User;

@Mapper(componentModel = "spring")
public interface UserRegistrationMapper {

    UserResponseDto toDto(User user);

    User toModel(UserRequestDto userRequestDto);
}
