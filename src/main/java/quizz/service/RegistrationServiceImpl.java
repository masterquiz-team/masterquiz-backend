package quizz.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quizz.dto.register.UserRequestDto;
import quizz.dto.register.UserResponseDto;
import quizz.exception.RegisterException;
import quizz.mapper.UserRegistrationMapper;
import quizz.model.Avatar;
import quizz.model.GuestUser;
import quizz.model.Role;
import quizz.model.User;
import quizz.repository.GuestUserRepository;
import quizz.repository.UserRepository;
import quizz.service.interfaces.RegistrationService;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRegistrationMapper userMapper;
    private final GuestUserRepository guestUserRepository;

    @Transactional
    public UserResponseDto register(UserRequestDto userRequestDto) {
        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new RegisterException("User with this email already exist.");
        }

        Optional<GuestUser> guestUser = guestUserRepository
                .findById(userRequestDto.getGuestUserId());

        User user = userMapper.toModel(userRequestDto);
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setRoles(new ArrayList<>());

        if (guestUser.isPresent()) {
            user.setGuestUser(guestUser.get());
            user.setScore(guestUser.get().getScore());
            user.setTotalGamesPlayed(guestUser.get().getTotalGamesPlayed());
            user.setAvatar(guestUser.get().getAvatar());
        } else {
            user.setGuestUser(null);
        }

        user.getRoles().add(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
