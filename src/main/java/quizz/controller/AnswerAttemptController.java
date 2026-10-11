package quizz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import quizz.dto.answerattempt.AnswerAttemptDto;
import quizz.dto.answerattempt.AnswerAttemptRequestDto;
import quizz.service.interfaces.AnswerAttemptService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz-attempts/answers")
@Tag(name = "Answer Attempt management", description = "Endpoints for answer attempt")
public class AnswerAttemptController {
    private final AnswerAttemptService answerAttemptService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create answer attempt", description = "Endpoint to create answer attempt")
    @PreAuthorize("hasRole('USER')")
    public AnswerAttemptDto createAnswerAttempt(
            @Valid @RequestBody AnswerAttemptRequestDto answerAttemptRequestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        return answerAttemptService.createAnswerAttempt(answerAttemptRequestDto,
                userDetails.getUsername());
    }

    @GetMapping("/{quizAttemptId}")
    @Operation(summary = "Get list of answer attempts",
            description = "Endpoint to get list of answer attempts")
    @PreAuthorize("hasRole('USER')")
    public List<AnswerAttemptDto> getListOfAnswerAttempts(
            @PathVariable Long quizAttemptId, @AuthenticationPrincipal UserDetails userDetails) {
        return answerAttemptService.getAnswerAttemptsByQuizAttemptId(quizAttemptId,
                userDetails.getUsername());
    }
}
