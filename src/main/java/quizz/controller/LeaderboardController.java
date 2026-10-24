package quizz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import quizz.dto.leaderboard.MyStatsDto;
import quizz.dto.leaderboard.TotalScoreByCategoryDto;
import quizz.dto.leaderboard.TotalScoreStatsDto;
import quizz.service.interfaces.LeaderboardService;

@RestController
@RequestMapping("/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Leaderboard Management", description = "Endpoints for leaderboard")
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get my stats", description = "Get my stats")
    public MyStatsDto getMyStats(
            @RequestParam(required = false) Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return leaderboardService.showMyStats(id, userDetails.getUsername());
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Show page of stats", description = "Show page of stats by total score")
    public Page<TotalScoreStatsDto> getPageOfTotalScoreStats(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        return leaderboardService.showPageOfStats(userDetails.getUsername(), pageable);
    }

    @GetMapping("/{categoryId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Show page of stats", description = "Show page of stats by category score")
    public Page<TotalScoreByCategoryDto> getPageOfTotalCategoryScoreStats(
            @PathVariable Long categoryId,
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        return leaderboardService.showStatsOfCategory(
                categoryId, userDetails.getUsername(), pageable);
    }
}
