package quizz.dto.leaderboard;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyStatsDto {
    private String nickname;
    private int totalScore;
    private int totalGamesPlayed;
    private int rank;
    private Map<Long, Integer> scoreByCategoryId;
    private boolean flag;
}
