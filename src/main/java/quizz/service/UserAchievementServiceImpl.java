package quizz.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quizz.dto.userachievement.UserAchievementDto;
import quizz.exception.EntityNotFoundException;
import quizz.exception.UnauthorizedAccessException;
import quizz.mapper.UserAchievementMapper;
import quizz.model.Achievement;
import quizz.model.User;
import quizz.model.UserAchievement;
import quizz.repository.AchievementRepository;
import quizz.repository.UserAchievementRepository;
import quizz.repository.UserRepository;
import quizz.service.interfaces.UserAchievementService;

@Service
@RequiredArgsConstructor
public class UserAchievementServiceImpl implements UserAchievementService {
    private final UserAchievementRepository userAchievementRepository;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final UserAchievementMapper userAchievementMapper;

    @Override
    @Transactional(readOnly = true)
    public UserAchievementDto getUserAchievement(Long achievementId, String username) {
        achievementExists(achievementId);
        User user = userExists(username);
        hasPermission(user);

        UserAchievement achievement = userAchievementRepository
                .findByUserIdAndAchievementId(user.getId(), achievementId).orElseThrow(
                        () -> new EntityNotFoundException(username
                                + " doesn't have this achievement yet"));

        return userAchievementMapper.toDto(achievement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAchievementDto> getListOfUserAchievements(String username) {
        User user = userExists(username);
        hasPermission(user);

        return userAchievementRepository.findAllByUserId(user.getId()).stream()
                .map(userAchievementMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void unlockAchievement(Long achievementId, String username) {
        Achievement achievement = achievementExists(achievementId);
        User user = userExists(username);

        if (userAchievementRepository.findByUserIdAndAchievementId(user.getId(), achievementId)
                .isPresent()) {
            return;
        }
        UserAchievement userAchievement = new UserAchievement();
        userAchievement.setAchievement(achievement);
        userAchievement.setUser(user);
        userAchievement.setUnlockedAt(LocalDateTime.now());
        userAchievementRepository.save(userAchievement);
    }

    private Achievement achievementExists(Long achievementId) {
        return achievementRepository.findById(achievementId).orElseThrow(
                () -> new EntityNotFoundException("Achievement with id: "
                        + achievementId + " doesn't exist"));
    }

    private User userExists(String username) {
        return userRepository.findByEmail(username).orElseThrow(
                () -> new EntityNotFoundException("User with: " + username + " doesn't exist"));
    }

    private void hasPermission(User user) {
        if (user.getRoles().stream().noneMatch(role -> role.name().equals("USER"))) {
            throw new UnauthorizedAccessException("You have no access");
        }
    }
}
