package quizz.dto.user;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import quizz.model.Level;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String email;
    private String nickname;
    private String avatarId;
    private int streak;
    private Level level;
    private String iconPath;
    private String levelTitle;
    private int levelPoints;
    private LocalDate lastTimePlayed;
    private int score;
    private int totalGamesPlayed;
}
