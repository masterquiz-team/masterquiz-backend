package quizz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quizz.dto.user.UserDto;
import quizz.dto.user.UserUpdateDto;
import quizz.exception.EntityNotFoundException;
import quizz.exception.UnauthorizedAccessException;
import quizz.mapper.UserMapper;
import quizz.model.User;
import quizz.repository.UserRepository;
import quizz.service.interfaces.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserDto getProfile(String username) {
        User user = findUser(username);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto updateProfile(String username, UserUpdateDto userUpdateDto) {
        User user = findUser(userUpdateDto.getEmail());
        if (!username.equals(userUpdateDto.getEmail())) {
            throw new UnauthorizedAccessException("You have no permission");
        }
        userMapper.update(userUpdateDto, user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public void deleteProfile(String username) {
        User user = findUser(username);
        userRepository.delete(user);
    }

    private User findUser(String username) {
        return userRepository.findByEmail(username).orElseThrow(
                () -> new EntityNotFoundException("User with email: "
                        + username + " doesn't exist"));
    }
}
