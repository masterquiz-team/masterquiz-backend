package quizz.dto.user;

import java.time.LocalDate;
import lombok.Data;
import quizz.model.Level;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String nickname;
    private String avatarId;
    private int streak;
    private Level level;
    private String levelTitle;
    private int levelPoints;
    private LocalDate lastTimePlayed;
    private int score;
    private int totalGamesPlayed;
}
