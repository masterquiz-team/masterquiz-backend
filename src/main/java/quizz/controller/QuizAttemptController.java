package quizz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import quizz.dto.quizattempt.QuizAttemptDto;
import quizz.dto.quizattempt.QuizAttemptRequestDto;
import quizz.service.interfaces.QuizAttemptService;

@RestController
@RequestMapping("/quiz-attempts")
@Tag(name = "Quiz Attempt Management", description = "Endpoints for quiz attempt")
@RequiredArgsConstructor
public class QuizAttemptController {
    private final QuizAttemptService quizAttemptService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create quiz attempt", description = "Endpoint to create quiz attempt")
    @PreAuthorize("hasRole('USER')")
    public QuizAttemptDto createQuizAttempt(
            @Valid @RequestBody QuizAttemptRequestDto quizAttemptRequestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return quizAttemptService.createQuizAttempt(quizAttemptRequestDto,
                userDetails.getUsername());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quiz attempt", description = "Endpoint to get quiz attempt by id")
    @PreAuthorize("hasRole('USER')")
    public QuizAttemptDto getQuizAttempt(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return quizAttemptService.getQuizAttempt(id, userDetails.getUsername());
    }

    @PutMapping("/{id}/finish")
    @Operation(summary = "Change quiz attempt status",
            description = "Endpoint to change quiz attempt status")
    @PreAuthorize("hasRole('USER')")
    public QuizAttemptDto finishQuizAttempt(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return quizAttemptService.finishQuizAttempt(id, userDetails.getUsername());
    }
}
