package quizz.dto.guestuser;

import lombok.Getter;
import lombok.Setter;
import quizz.model.Avatar;

@Getter
@Setter
public class GuestUserDto {
    private Long id;
    private String nickname;
    private Avatar avatar;
    private int score;
    private int totalGamesPlayed;

}
