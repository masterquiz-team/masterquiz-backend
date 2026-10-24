package quizz.dto.user;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String nickname;
    private int score;
    private int totalGamesPlayed;
}
