package quizz.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import quizz.config.JwtUtil;
import quizz.dto.auth.LoginDto;
import quizz.dto.auth.LoginRequestDto;
import quizz.exception.LoginException;
import quizz.model.Avatar;
import quizz.model.Level;
import quizz.model.Role;
import quizz.model.User;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {
    private static final Long ID = 1L;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;


    @Test
    void authenticate_ValidRequestDto_ReturnsLoginDto() {
        //given
        LoginRequestDto loginRequestDto = new LoginRequestDto("email@email.com", "password");
        LoginDto expected = new LoginDto("jwt-token");
        Optional<User> user = createUser();
        User unwrappedUser = user.get();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(unwrappedUser);
        when(jwtUtil.createToken(user.get())).thenReturn("jwt-token");

        //when
        LoginDto actual = authenticationService.authenticate(loginRequestDto);

        //then
        Assertions.assertEquals(expected, actual);

    }

    @Test
    void authenticate_InvalidRequestDto_Throws401() {
        //given
        LoginRequestDto loginRequestDto = new LoginRequestDto("email@email.com", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new LoginException(""));

        //when & then
        Assertions.assertThrows(LoginException.class,
                () -> authenticationService.authenticate(loginRequestDto));
    }

    private Optional<User> createUser() {
        User user = new User();
        user.setId(ID);
        user.setLevel(Level.LEVEL_1);
        user.setStreak(0);
        user.setGuestUser(null);
        user.setCreatedAt(LocalDateTime.now());
        user.setScore(490);
        user.setAvatar(Avatar.AVATAR_1);
        user.setPassword("password");
        user.setNickname("nickname");
        user.setEmail("email@email.com");
        user.setRoles(Collections.singletonList(Role.USER));
        return Optional.of(user);
    }
}
