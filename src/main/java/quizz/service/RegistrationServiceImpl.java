package quizz.service;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import quizz.dto.register.UserRequestDto;
import quizz.dto.register.UserResponseDto;
import quizz.exception.RegisterException;
import quizz.mapper.UserRegistrationMapper;
import quizz.model.Role;
import quizz.model.User;
import quizz.repository.UserRepository;
import quizz.service.interfaces.RegistrationService;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRegistrationMapper userMapper;

    public UserResponseDto register(UserRequestDto userRequestDto) {
        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new RegisterException("User with this email already exist.");
        }
        User user = userMapper.toModel(userRequestDto);
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setRoles(new ArrayList<>());
        user.getRoles().add(Role.USER);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
