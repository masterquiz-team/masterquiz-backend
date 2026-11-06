package quizz.model;

import lombok.Getter;

@Getter
public enum Difficulty {
    EASY(10),
    MEDIUM(20),
    HARD(30);

    private final int points;

    Difficulty(int points) {
        this.points = points;
    }

}
