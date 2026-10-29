package quizz.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import quizz.dto.register.UserRequestDto;
import quizz.dto.register.UserResponseDto;
import quizz.exception.RegisterException;
import quizz.mapper.UserRegistrationMapper;
import quizz.model.Avatar;
import quizz.model.GuestUser;
import quizz.model.Level;
import quizz.model.Role;
import quizz.model.User;
import quizz.repository.GuestUserRepository;
import quizz.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceImplTest {
    private static final Long ID = 1L;
    private static final String USERNAME = "email@email.com";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRegistrationMapper userRegistrationMapper;

    @Mock
    private GuestUserRepository guestUserRepository;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    @Test
    void register_ValidRequestDto_ReturnsUserResponseDto() {
        //given
        Optional<GuestUser> guestUser = createGuestUser();
        GuestUser unwrappedGuestUser = guestUser.get();
        Optional<User> user = createUser();
        User unwrappedUser = user.get();
        UserRequestDto userRequestDto = createUserRequestDto();
        UserResponseDto expected = createuserResponseDto();

        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findById(ID)).thenReturn(guestUser);
        when(userRegistrationMapper.toModel(userRequestDto)).thenReturn(user.get());
        when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn("strongpassword");
        when(userRepository.save(any(User.class))).thenReturn(user.get());
        when(userRegistrationMapper.toDto(user.get())).thenReturn(expected);

        //when
        UserResponseDto actual = registrationService.register(userRequestDto);

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertSame(unwrappedGuestUser, unwrappedUser.getGuestUser());
        verify(userRepository).save(user.get());
    }

    @Test
    void register_ValidRequestDtoButEmptyGuest_ReturnsUserResponseDto() {
        //given
        Optional<User> user = createUser();
        User unwrappedUser = user.get();
        UserRequestDto userRequestDto = createUserRequestDto();
        UserResponseDto expected = createuserResponseDto();

        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findById(ID)).thenReturn(Optional.empty());
        when(userRegistrationMapper.toModel(userRequestDto)).thenReturn(user.get());
        when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn("strongpassword");
        when(userRepository.save(any(User.class))).thenReturn(user.get());
        when(userRegistrationMapper.toDto(user.get())).thenReturn(expected);

        //when
        UserResponseDto actual = registrationService.register(userRequestDto);

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertNull(unwrappedUser.getGuestUser());
        verify(userRepository).save(user.get());
    }

    @Test
    void register_UserAlreadyExist_Throws409() {
        //given
        Optional<User> user = createUser();

        when(userRepository.findByEmail(USERNAME)).thenReturn(user);

        //when & then
        Assertions.assertThrows(RegisterException.class,
                () -> registrationService.register(createUserRequestDto()));
    }

    private UserResponseDto createuserResponseDto() {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setEmail(USERNAME);
        userResponseDto.setId(ID);
        userResponseDto.setNickname("user1");
        return userResponseDto;
    }

    private UserRequestDto createUserRequestDto() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail(USERNAME);
        userRequestDto.setNickname("user1");
        userRequestDto.setPassword("password");
        userRequestDto.setRepeatPassword("password");
        userRequestDto.setGuestUserId(ID);
        return userRequestDto;
    }

    private Optional<User> createUser() {
        User user = new User();
        user.setId(ID);
        user.setLevel(Level.LEVEL_1);
        user.setStreak(0);
        user.setGuestUser(null);
        user.setScore(490);
        user.setPassword("password");
        user.setNickname("nickname");
        user.setEmail(USERNAME);
        return Optional.of(user);
    }

    private Optional<GuestUser> createGuestUser() {
        GuestUser guestUser = new GuestUser();
        guestUser.setRole(Role.GUEST);
        guestUser.setCreatedAt(LocalDateTime.now());
        guestUser.setAvatar(Avatar.AVATAR_1);
        guestUser.setNickname("guest");
        guestUser.setId(ID);
        guestUser.setScore(0);
        guestUser.setTotalGamesPlayed(0);
        return Optional.of(guestUser);
    }
}
