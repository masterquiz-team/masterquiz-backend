package quizz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import quizz.dto.answer.AnswerDto;
import quizz.dto.answer.AnswerRequestDto;
import quizz.dto.answer.UpdateAnswerDto;
import quizz.service.interfaces.AnswerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/answers")
@Tag(name = "Answer Management", description = "Endpoints for answers")
public class AnswerController {
    private final AnswerService answerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create an answer", description = "Create an answer")
    AnswerDto createAnswer(@RequestBody @Valid AnswerRequestDto answerRequestDto) {
        return answerService.createAnswer(answerRequestDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get an answer by id", description = "Get an answer by id")
    AnswerDto getAnswer(@PathVariable Long id) {
        return answerService.getAnswer(id);
    }

    @GetMapping("/question/{questionId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get list of answers", description = "Get list of answers by question id")
    List<AnswerDto> getAnswersByQuestionId(@PathVariable Long questionId) {
        return answerService.getAnswersByQuestionId(questionId);
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update answer", description = "Update answer")
    AnswerDto updateAnswer(@RequestBody UpdateAnswerDto answerDto) {
        return answerService.updateAnswer(answerDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete answer", description = "Delete answer")
    @PreAuthorize("hasRole('USER')")
    void deleteAnswer(@PathVariable Long id) {
        answerService.deleteAnswer(id);
    }
}
