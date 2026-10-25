package quizz.model;

import lombok.Getter;

@Getter
public enum Level {

    LEVEL_1("Curious mind", 0),
    LEVEL_2("Quiz Explorer", 500),
    LEVEL_3("Knowledge Seeker", 1200),
    LEVEL_4("Quick Thinker", 2500),
    LEVEL_5("Brain Trainer", 4500),
    LEVEL_6("Trivia Enthusiast", 7500),
    LEVEL_7("Quiz Master", 12000),
    LEVEL_8("Knowledge Expert", 18000),
    LEVEL_9("Genius", 27000),
    LEVEL_10("Legendary Mind", 40000);

    private final String title;
    private final int points;

    Level(String title, int points) {
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

