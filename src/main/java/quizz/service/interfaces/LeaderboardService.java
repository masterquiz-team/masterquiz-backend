package quizz.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import quizz.dto.leaderboard.MyStatsDto;
import quizz.dto.leaderboard.TotalScoreByCategoryDto;
import quizz.dto.leaderboard.TotalScoreStatsDto;

public interface LeaderboardService {
    MyStatsDto showMyStats(Long id, String name);

    Page<TotalScoreStatsDto> showPageOfStats(String email, Pageable pageable);

    Page<TotalScoreByCategoryDto> showStatsOfCategory(
            Long categoryId,
            String email,
            Pageable pageable);
}
