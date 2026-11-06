package quizz.model;

public enum AchievementImage {
    FIRST_STEPS("/achievementimage/first-steps.png"),
    PERFECT_SCORE("/achievementimage/perfect-score.png"),
    CURIOUS_MIND("/achievementimage/curious-mind.ping"),
    BRAIN_POWER("/achievementimage/brain-power.png"),
    HARD_MODE_HERO("/achievementimage/hard-mode-hero.png"),
    QUIZ_VETERAN("/achievementimage/quiz-veteran.png"),
    ON_FIRE("/achievementimage/on-fire.png"),
    SHARPSHOOTER("/achievementimage/sharpshooter.png");
    private String url;

    AchievementImage(String url) {
        this.url = url;
    }
}
