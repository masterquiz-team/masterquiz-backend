package quizz.service.interfaces;

public interface AchievementEvaluator {
    void checkFirstQuizAchievement(String email);

    void checkPerfectScoreAchievement(String email, int score);

    void checkFiveCategoriesAchievement(String email);

    void checkEightOutOfTenAchievement(String email, int score);

    void checkFiveQuizzesOnHardLevelAchievement(String email);

    void checkFiftyQuizzesAchievement(String email);

    void checkPlayThreeDaysInARow(String email);

    void checkAnswerFiveQuestionsCorrectlyInARow(String email);
}
