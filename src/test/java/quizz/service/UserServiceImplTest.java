package quizz.service;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import quizz.dto.question.UpdateQuestionDto;
import quizz.dto.user.UserDto;
import quizz.dto.user.UserUpdateDto;
import quizz.exception.EntityNotFoundException;
import quizz.exception.UnauthorizedAccessException;
import quizz.mapper.UserMapper;
import quizz.model.Avatar;
import quizz.model.Level;
import quizz.model.Role;
import quizz.model.User;
import quizz.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private static final Long ID = 1L;
    private static final String USERNAME = "email@email.com";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getProfile_ValidUsername_ReturnsUserDto() {
        //given
        Optional<User> user = createUser();
        UserDto expected = createUserDto();

        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(userMapper.toDto(user.get())).thenReturn(expected);

        //when
        UserDto actual = userService.getProfile(USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getProfile_InvalidUsername_Throws404() {
        //given
        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.getProfile(USERNAME));
    }

    @Test
    void updateProfile_ValidUsernameAndUpdateDto_ReturnsUserDto() {
        //given
        Optional<User> user = createUser();
        User unwrappedUser = user.get();
        UserUpdateDto userUpdateDto = createUserUpdateDto();
        UserDto expected = createUserDto();
        expected.setNickname("Test123");

        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        doNothing().when(userMapper).update(userUpdateDto, unwrappedUser);
        when(userMapper.toDto(unwrappedUser)).thenReturn(expected);

        //when
        UserDto actual = userService.updateProfile(USERNAME, userUpdateDto);

        //then
        Assertions.assertEquals(expected.getNickname(), actual.getNickname());
    }

    @Test
    void updateProfile_UsernameAndUpdateUsernameAreDifferent_Throws403() {
        //given
        UserUpdateDto userUpdateDto = createUserUpdateDto();
        userUpdateDto.setEmail("test@test.com");
        Optional<User> user = createUser();


        when(userRepository.findByEmail(userUpdateDto.getEmail())).thenReturn(user);

        //when & then
        Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> userService.updateProfile(USERNAME, userUpdateDto));
    }

    @Test
    void deleteProfile_ValidUsername_Ok() {
        //given
        Optional<User> user = createUser();

        when(userRepository.findByEmail(USERNAME)).thenReturn(user);

        //when
        userService.deleteProfile(USERNAME);

        //then
        verify(userRepository).delete(user.get());
    }

    @Test
    void deleteProfile_InvalidUsername_Throws404() {
        //given
        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.deleteProfile(USERNAME));
    }

    private UserUpdateDto createUserUpdateDto() {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setEmail(USERNAME);
        userUpdateDto.setNickname("Test123");
        return userUpdateDto;
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
        user.setEmail(USERNAME);
        user.setRoles(Collections.singletonList(Role.USER));
        return Optional.of(user);
    }

    private UserDto createUserDto() {
        UserDto user = new UserDto();
        user.setId(ID);
        user.setLevel(Level.LEVEL_1);
        user.setStreak(0);
        user.setScore(490);
        user.setAvatarId("avatar-1");
        user.setNickname("nickname");
        user.setEmail(USERNAME);
        user.setIconPath("level-1");
        user.setLastTimePlayed(LocalDate.now());
        user.setLevelPoints(200);
        user.setLevelTitle("level-title");
        user.setTotalGamesPlayed(2);
        return user;
    }
}
