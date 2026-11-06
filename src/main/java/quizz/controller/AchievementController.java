package quizz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import quizz.dto.achievement.AchievementDto;
import quizz.dto.achievement.AchievementRequestDto;
import quizz.service.interfaces.AchievementService;

@RestController
@RequestMapping("/achievements")
@RequiredArgsConstructor
@Tag(name = "Achievement Management", description = "Endpoints for achievements")
public class AchievementController {
    private final AchievementService achievementService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an achievement", description = "Create an achievement")
    @PreAuthorize("hasRole('ADMIN')")
    public AchievementDto createAchievement(
            @RequestBody AchievementRequestDto achievementRequestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return achievementService.createAchievement(
                achievementRequestDto, userDetails.getUsername());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an achievement", description = "Get an achievement by id")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public AchievementDto getAchievement(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return achievementService.getAchievementById(id, userDetails.getUsername());
    }

    @GetMapping
    @Operation(summary = "Get list of achievements", description = "Get list of achievements")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<AchievementDto> getListOfAchievements(
            @AuthenticationPrincipal UserDetails userDetails) {
        return achievementService.getAllAchievements(userDetails.getUsername());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete achievement", description = "Delete achievement by id")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAchievement(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        achievementService.deleteAchievementById(id, userDetails.getUsername());
    }
}
