package quizz.service.interfaces;

import quizz.dto.user.UserDto;
import quizz.dto.user.UserUpdateDto;

public interface UserService {

    UserDto getProfile(String username);

    UserDto updateProfile(String username, UserUpdateDto userUpdateDto);

    void deleteProfile(String username);
}
