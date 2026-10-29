package quizz.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import quizz.config.JwtUtil;
import quizz.dto.guestuser.GuestLoginResponseDto;
import quizz.dto.guestuser.GuestUserDto;
import quizz.mapper.GuestUserMapper;
import quizz.model.Avatar;
import quizz.model.GuestUser;
import quizz.model.Role;
import quizz.repository.GuestUserRepository;

@ExtendWith(MockitoExtension.class)
public class GuestUserServiceImplTest {
    private static final Long ID = 1L;
    private static final String NICKNAME = "guest_1234";

    @Mock
    private GuestUserRepository guestUserRepository;

    @Mock
    private GuestUserMapper guestUserMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Random random;

    @InjectMocks
    private GuestUserServiceImpl guestUserService;

    @Test
    void createGuestUser_ValidGuestLoginResponseDto_ReturnsGuestLoginResponseDto() {
        //given
        GuestLoginResponseDto expected = new GuestLoginResponseDto();
        GuestUser guestUser = createGuestUser();
        GuestUserDto guestUserDto = createGuestUserDto();
        String token = "jwt-token";
        expected.setToken(token);
        expected.setGuestUserDto(guestUserDto);

        when(random.nextInt(1000000)).thenReturn(1234);
        when(guestUserRepository.existsByNickname(NICKNAME)).thenReturn(false);
        when(guestUserRepository.save(any(GuestUser.class))).thenReturn(guestUser);
        when(guestUserMapper.toDto(guestUser)).thenReturn(guestUserDto);
        when(jwtUtil.createToken(guestUser)).thenReturn(token);

        //when
        GuestLoginResponseDto actual = guestUserService.createGuestUser();

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createGuestUser_GuestNicknameExist_ReturnsGuestLoginResponseDto() {
        //given
        GuestLoginResponseDto expected = new GuestLoginResponseDto();
        GuestUser guestUser = createGuestUser();
        guestUser.setNickname("guest_5678");
        GuestUserDto guestUserDto = createGuestUserDto();
        guestUserDto.setNickname("guest_5678");
        String token = "jwt-token";
        expected.setToken(token);
        expected.setGuestUserDto(guestUserDto);

        when(random.nextInt(1000000)).thenReturn(1234).thenReturn(5678);
        when(guestUserRepository.existsByNickname(NICKNAME)).thenReturn(true);
        when(guestUserRepository.existsByNickname("guest_5678")).thenReturn(false);
        when(guestUserRepository.save(any(GuestUser.class))).thenReturn(guestUser);
        when(guestUserMapper.toDto(guestUser)).thenReturn(guestUserDto);
        when(jwtUtil.createToken(guestUser)).thenReturn(token);

        //when
        GuestLoginResponseDto actual = guestUserService.createGuestUser();

        //then
        verify(random, times(2)).nextInt(1000000);
        Assertions.assertEquals("guest_5678", actual.getGuestUserDto().getNickname());
        Assertions.assertEquals(expected, actual);
    }

    private GuestUserDto createGuestUserDto() {
        GuestUserDto guestUserDto = new GuestUserDto();
        guestUserDto.setId(ID);
        guestUserDto.setAvatar(Avatar.AVATAR_1);
        guestUserDto.setScore(0);
        guestUserDto.setNickname(NICKNAME);
        guestUserDto.setTotalGamesPlayed(0);
        return guestUserDto;
    }

    private GuestUser createGuestUser() {
        GuestUser guestUser = new GuestUser();
        guestUser.setAvatar(Avatar.AVATAR_1);
        guestUser.setRole(Role.GUEST);
        guestUser.setNickname(NICKNAME);
        guestUser.setScore(0);
        guestUser.setTotalGamesPlayed(0);
        guestUser.setCreatedAt(LocalDateTime.now());
        return guestUser;
    }
}
