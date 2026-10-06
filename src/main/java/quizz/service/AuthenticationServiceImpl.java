package quizz.service;

import quizz.config.JwtUtil;
import quizz.dto.auth.LoginDto;
import quizz.dto.auth.LoginRequestDto;
import quizz.exception.LoginException;
import quizz.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import quizz.service.interfaces.AuthenticationService;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public LoginDto authenticate(LoginRequestDto loginRequestDto) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(), loginRequestDto.getPassword()));
            User user = (User) authentication.getPrincipal();
            String jwtToken = jwtUtil.createToken(user);
            return new LoginDto(jwtToken);
        } catch (AuthenticationException ex) {
            throw new LoginException("Invalid login or password");
        }
    }
}
