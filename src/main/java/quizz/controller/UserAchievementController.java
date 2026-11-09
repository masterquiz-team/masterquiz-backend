package quizz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import quizz.dto.userachievement.UserAchievementDto;
import quizz.service.interfaces.UserAchievementService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-achievements")
@Tag(name = "User achievements management", description = "Endpoints for user achievements")
public class UserAchievementController {
    private final UserAchievementService userAchievementService;

    @GetMapping("/{achievementId}")
    @Operation(summary = "Get achievement", description = "Get achievement by id")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public UserAchievementDto getUserAchievement(
            @PathVariable Long achievementId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return userAchievementService.getUserAchievement(achievementId, userDetails.getUsername());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get list of user achievements",
            description = "Get list of user achievements")
    public List<UserAchievementDto> getListOfUserAchievements(
            @AuthenticationPrincipal UserDetails userDetails) {
        return userAchievementService.getListOfUserAchievements(userDetails.getUsername());
    }
}
