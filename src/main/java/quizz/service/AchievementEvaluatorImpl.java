package quizz.service;

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
    public void checkFirstQuizAchievement(String email) {
        User user = findByEmail(email);
        if (user.getTotalGamesPlayed() == 1) {
            Achievement achievement = findByAchievementType(AchievementType.FIRST_STEPS);
            userAchievementService.unlockAchievement(achievement.getId(), email);
        }
    }

    @Override
    public void checkPerfectScoreAchievement(String email, int score) {
        if (score == 10) {
            Achievement achievement = findByAchievementType(AchievementType.PERFECT_SCORE);
            userAchievementService.unlockAchievement(achievement.getId(), email);
        }
    }

    @Override
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
    public void checkEightOutOfTenAchievement(String email, int score) {
        if (score >= 8) {
            Achievement achievement = findByAchievementType(AchievementType.BRAIN_POWER);
            userAchievementService.unlockAchievement(achievement.getId(), email);
        }
    }

    @Override
    public void checkFiveQuizzesOnHardLevelAchievement(String email) {
        User user = findByEmail(email);
        Achievement achievement = findByAchievementType(AchievementType.HARD_MODE_HERO);
        List<QuizAttempt> byUserIdAndQuizDifficulty = quizAttemptRepository
                .findByUserIdAndQuizDifficultyAndStatus(
                        user.getId(), Difficulty.HARD, Status.FINISHED);

        if (byUserIdAndQuizDifficulty.size() >= 5) {
            userAchievementService.unlockAchievement(achievement.getId(), email);
        }
    }

    @Override
    public void checkFiftyQuizzesAchievement(String email) {
        User user = findByEmail(email);
        Achievement achievement = findByAchievementType(AchievementType.QUIZ_VETERAN);

        List<QuizAttempt> allQuizzes = quizAttemptRepository.findByUserIdAndStatus(
                user.getId(), Status.FINISHED);

        if (allQuizzes.size() >= 50) {
            userAchievementService.unlockAchievement(achievement.getId(), email);
        }
    }

    @Override
    public void checkPlayThreeDaysInARow(String email) {
        User user = findByEmail(email);
        Achievement achievement = findByAchievementType(AchievementType.ON_FIRE);

        if (user.getStreak() >= 3) {
            userAchievementService.unlockAchievement(achievement.getId(), email);
        }
    }

    @Override
    public void checkAnswerFiveQuestionsCorrectlyInARow(String email) {
        User user = findByEmail(email);
        List<AnswerAttempt> lastFiveAnswers = answerAttemptRepository
                .findTop5ByQuizAttempt_User_IdOrderByAnsweredAtDescIdDesc(user.getId());
        Achievement achievement = findByAchievementType(AchievementType.SHARPSHOOTER);

        if (lastFiveAnswers.size() == 5 && lastFiveAnswers.stream()
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
