package quizz.dto.leaderboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalScoreStatsDto {
    private Long userId;
    private String nickname;
    private int score;
    private int rank;
    private int totalGamesPlayed;
    private boolean flag;
}
