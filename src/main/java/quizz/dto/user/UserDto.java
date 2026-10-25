package quizz.dto.user;

import java.time.LocalDate;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String nickname;
    private String avatarId;
    private int streak;
    private LocalDate lastTimePlayed;
    private int score;
    private int totalGamesPlayed;
}
