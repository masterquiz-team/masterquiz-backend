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
import quizz.repository.RoleRepository;
import quizz.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRegistrationMapper userMapper;

    public UserResponseDto register(UserRequestDto userRequestDto) {
        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new RegisterException("User with this email already exist.");
        }
        User user = userMapper.toModel(userRequestDto);
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setRole(new ArrayList<>());
        Role role = new Role();
        role.setRoleName("User");
        Role savedRole = new Role();
        if (roleRepository.findByRoleName(role.getRoleName()).isEmpty()) {
            savedRole = roleRepository.save(role);
        }
        user.getRole().add(savedRole);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
