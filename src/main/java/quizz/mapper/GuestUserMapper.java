package quizz.mapper;

import org.mapstruct.Mapper;
import quizz.dto.guestuser.GuestUserDto;
import quizz.model.GuestUser;

@Mapper(componentModel = "spring")
public interface GuestUserMapper {
    GuestUserDto toDto(GuestUser guestUser);
}
