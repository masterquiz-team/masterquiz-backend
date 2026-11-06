package quizz.service.interfaces;

import quizz.model.User;

public interface AchievementEvaluator {
    void didUserFinishedFirstQuiz(User user);

    void didUserGotPerfectScore(User user);

    void didUserPlayed5QuizzesWithDifferentCategories(User user);

    void didUserGot8OutOf10Points(User user);
}
