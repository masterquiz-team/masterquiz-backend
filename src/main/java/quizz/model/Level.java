package quizz.model;

import lombok.Getter;

@Getter
public enum Level {

    LEVEL_1("/levelicons/level-1.png", "Curious mind", 0),
    LEVEL_2("/levelicons/level-2.png","Quiz Explorer", 500),
    LEVEL_3("/levelicons/level-3.png","Knowledge Seeker", 1200),
    LEVEL_4("/levelicons/level-4.png","Quick Thinker", 2500),
    LEVEL_5("/levelicons/level-5.png","Brain Trainer", 4500),
    LEVEL_6("/levelicons/level-6.png","Trivia Enthusiast", 7500),
    LEVEL_7("/levelicons/level-7.png","Quiz Master", 12000),
    LEVEL_8("/levelicons/level-8.png","Knowledge Expert", 18000),
    LEVEL_9("/levelicons/level-9.png","Genius", 27000),
    LEVEL_10("/levelicons/level-10.png","Legendary Mind", 40000);

    private final String iconPath;
    private final String title;
    private final int points;

    Level(String iconPath, String title, int points) {
        this.iconPath = iconPath;
        this.title = title;
        this.points = points;
    }

    public static Level fromScore(int score) {
        Level result = LEVEL_1;
        for (Level l : Level.values()) {
            if (score >= l.getPoints()) {
                result = l;
            }
        }
        return result;
    }
}

