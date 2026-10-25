package quizz.service;

import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quizz.dto.guestuser.GuestUserDto;
import quizz.mapper.GuestUserMapper;
import quizz.model.Avatar;
import quizz.model.GuestUser;
import quizz.model.Role;
import quizz.repository.GuestUserRepository;
import quizz.service.interfaces.GuestUserService;

@RequiredArgsConstructor
@Service
public class GuestUserServiceImpl implements GuestUserService {
    private final GuestUserRepository userRepository;
    private final GuestUserMapper guestUserMapper;
    private final Random random = new Random();

    @Override
    public GuestUserDto createGuestUser() {
        String nickname;
        do {
            nickname = "guest_" + random.nextInt(1000000);
        } while (userRepository.doesGuestExists(nickname));

        GuestUser guestUser = new GuestUser();
        guestUser.setAvatar(Avatar.AVATAR_1);
        guestUser.setRole(Role.GUEST);
        guestUser.setNickname(nickname);
        guestUser.setScore(0);
        guestUser.setTotalGamesPlayed(0);
        GuestUser savedUser = userRepository.save(guestUser);
        return guestUserMapper.toDto(savedUser);
    }
}
