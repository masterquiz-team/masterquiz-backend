package quizz.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quizz.dto.achievement.AchievementDto;
import quizz.dto.achievement.AchievementRequestDto;
import quizz.exception.EntityFoundException;
import quizz.exception.EntityNotFoundException;
import quizz.exception.UnauthorizedAccessException;
import quizz.mapper.AchievementMapper;
import quizz.model.Achievement;
import quizz.model.User;
import quizz.repository.AchievementRepository;
import quizz.repository.UserRepository;
import quizz.service.interfaces.AchievementService;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {
    private final AchievementRepository achievementRepository;
    private final AchievementMapper achievementMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AchievementDto createAchievement(AchievementRequestDto requestDto, String username) {
        hasPermission(username);

        if (achievementRepository.existsByTitle(requestDto.getTitle())) {
            throw new EntityFoundException("Achievement with title: " + requestDto.getTitle()
                    + " already exists");
        }

        Achievement achievement = achievementMapper.toModel(requestDto);
        Achievement saved = achievementRepository.save(achievement);

        return achievementMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AchievementDto getAchievementById(Long id, String username) {
        userExists(username);

        Achievement achievement = findAchievement(id);

        return achievementMapper.toDto(achievement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AchievementDto> getAllAchievements(String username) {
        userExists(username);

        return achievementRepository.findAll().stream()
                .map(achievementMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteAchievementById(Long id, String username) {
        hasPermission(username);
        Achievement achievement = achievementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Achievement with id: " + id
                        + " doesn't exist"));
        achievementRepository.delete(achievement);
    }

    private void hasPermission(String username) {
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new EntityNotFoundException("User with name: "
                        + username + " doesn't exist"));
        if (user.getRoles().stream()
                .noneMatch(role -> role.name().equals("ADMIN"))) {
            throw new UnauthorizedAccessException("You have no permission");
        }
    }

    private void userExists(String username) {
        userRepository.findByEmail(username).orElseThrow(
                () -> new EntityNotFoundException("User with username: " + username
                        + " doesn't exist"));
    }

    private Achievement findAchievement(Long id) {
        return achievementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Achievement with id: "
                        + id + " doesnt exist"));
    }
}
