package quizz.service.interfaces;

import quizz.dto.register.UserRequestDto;
import quizz.dto.register.UserResponseDto;

public interface RegistrationService {
    UserResponseDto register(UserRequestDto userRequestDto);
}
