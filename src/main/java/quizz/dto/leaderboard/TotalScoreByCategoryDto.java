package quizz.dto.leaderboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalScoreByCategoryDto {
    private Long userId;
    private String nickname;
    private String categoryName;
    private int categoryScore;
    private int rank;
    private int gamesPlayed;
    private boolean flag;
}
