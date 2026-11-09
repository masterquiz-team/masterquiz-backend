package quizz.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quizz.exception.EntityNotFoundException;
import quizz.model.Achievement;
import quizz.model.AchievementType;
import quizz.model.AnswerAttempt;
import quizz.model.Difficulty;
import quizz.model.QuizAttempt;
import quizz.model.Status;
import quizz.model.User;
import quizz.repository.AchievementRepository;
import quizz.repository.AnswerAttemptRepository;
import quizz.repository.GamesByCategoryResultRepository;
import quizz.repository.QuizAttemptRepository;
import quizz.repository.UserRepository;
import quizz.service.interfaces.AchievementEvaluator;
import quizz.service.interfaces.UserAchievementService;

@RequiredArgsConstructor
@Service
public class AchievementEvaluatorImpl implements AchievementEvaluator {
    private final AchievementRepository achievementRepository;
    private final UserAchievementService userAchievementService;
    private final UserRepository userRepository;
    private final GamesByCategoryResultRepository gamesByCategoryResultRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final AnswerAttemptRepository answerAttemptRepository;

    @Override
    @Transactional
    public void checkFirstQuizAchievement(String email) {
        User user = findByEmail(email);
        if (user.getTotalGamesPlayed() == 1) {
            Achievement achievement = findByAchievementType(AchievementType.FIRST_STEPS);
            userAchievementService.unlockAchievement(achievement.getId(), email);
        }
    }

    @Override
    @Transactional
    public void checkPerfectScoreAchievement(String email, int score) {
        if (score == 10) {
            Achievement achievement = findByAchievementType(AchievementType.PERFECT_SCORE);
            userAchievementService.unlockAchievement(achievement.getId(), email);
        }
    }

    @Override
    @Transactional
    public void checkFiveCategoriesAchievement(String email) {
        User user = findByEmail(email);
        Achievement achievement = findByAchievementType(AchievementType.CURIOUS_MIND);

        long sum = gamesByCategoryResultRepository
                .countDistinctCategoriesByUserId(user.getId());

        if (sum >= 5) {
            userAchievementService.unlockAchievement(achievement.getId(), email);
        }
    }

    @Override
    @Transactional
    public void checkEightOutOfTenAchievement(String email, int score) {
        if (score >= 8) {
            Achievement achievement = findByAchievementType(AchievementType.BRAIN_POWER);
            userAchievementService.unlockAchievement(achievement.getId(), email);
        }
    }

    @Override
    @Transactional
    public void checkFiveQuizzesOnHardLevelAchievement(String email) {
        User user = findByEmail(email);
        Achievement achievement = findByAchievementType(AchievementType.HARD_MODE_HERO);
        int sum = quizAttemptRepository
                .countByUserIdAndQuizDifficultyAndStatus(
                        user.getId(), Difficulty.HARD, Status.FINISHED);

        if (sum >= 5) {
            userAchievementService.unlockAchievement(achievement.getId(), email);
        }
    }

    @Override
    @Transactional
    public void checkFiftyQuizzesAchievement(String email) {
        User user = findByEmail(email);
        Achievement achievement = findByAchievementType(AchievementType.QUIZ_VETERAN);

        int sum = quizAttemptRepository.countByUserIdAndStatus(
                user.getId(), Status.FINISHED);

        if (sum >= 50) {
            userAchievementService.unlockAchievement(achievement.getId(), email);
        }
    }

    @Override
    @Transactional
    public void checkPlayThreeDaysInARow(String email) {
        User user = findByEmail(email);
        Achievement achievement = findByAchievementType(AchievementType.ON_FIRE);

        if (user.getStreak() >= 3) {
            userAchievementService.unlockAchievement(achievement.getId(), email);
        }
    }

    @Override
    @Transactional
    public void checkAnswerFiveQuestionsCorrectlyInARow(String email) {
        User user = findByEmail(email);

        QuizAttempt lastQuiz = quizAttemptRepository
                .findTopByUserIdAndStatusOrderByFinishedAtDesc(user.getId(), Status.FINISHED);

        if (lastQuiz == null) {
            return;
        }

        Achievement achievement = findByAchievementType(AchievementType.SHARPSHOOTER);

        List<AnswerAttempt> last5 = answerAttemptRepository
                .findTop5ByQuizAttemptIdOrderByAnsweredAtAsc(lastQuiz.getId());

        if (last5.size() == 5 && last5.stream()
                .allMatch(AnswerAttempt::isCorrectAnswer)) {
            userAchievementService.unlockAchievement(achievement.getId(), email);
        }
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with: " + email + " doesn't exist"));
    }

    private Achievement findByAchievementType(AchievementType achievementType) {
        return achievementRepository.findByAchievementType(achievementType).orElseThrow(
                () -> new EntityNotFoundException("Achievement type: "
                        + achievementType + " doesn't exist"));
    }
}
