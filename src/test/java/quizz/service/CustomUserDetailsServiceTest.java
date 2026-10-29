package quizz.service;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import quizz.model.Avatar;
import quizz.model.GuestUser;
import quizz.model.Level;
import quizz.model.Role;
import quizz.model.User;
import quizz.repository.GuestUserRepository;
import quizz.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
    private static final Long ID = 1L;
    private static final String GUEST_USERNAME = "guest";
    private static final String USERNAME = "email@email.com";
    @Mock
    private UserRepository userRepository;
    @Mock
    private GuestUserRepository guestUserRepository;
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_ValidGuestUsername_ReturnsUserDetails() {
        //given
        Optional<GuestUser> guestUser = createGuestUser();
        GuestUser expected = guestUser.get();

        when(userRepository.findByEmail(GUEST_USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(GUEST_USERNAME)).thenReturn(guestUser);

        //when
        UserDetails actual = customUserDetailsService.loadUserByUsername(GUEST_USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void loadUserByUsername_ValidUsername_ReturnsUserDetails() {
        //given
        Optional<User> user = createUser();
        User expected = user.get();

        when(userRepository.findByEmail(USERNAME)).thenReturn(user);

        //when
        UserDetails actual = customUserDetailsService.loadUserByUsername(USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void loadUserByUsername_InvalidUsername_Throws404() {
        //given
        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(USERNAME));
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
