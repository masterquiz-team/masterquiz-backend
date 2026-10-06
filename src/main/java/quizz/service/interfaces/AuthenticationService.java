package quizz.service.interfaces;

import quizz.dto.auth.LoginDto;
import quizz.dto.auth.LoginRequestDto;

public interface AuthenticationService {
    LoginDto authenticate(LoginRequestDto loginRequestDto);
}
