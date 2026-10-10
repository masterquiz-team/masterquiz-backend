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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import quizz.dto.question.QuestionDto;
import quizz.dto.question.QuestionRequestDto;
import quizz.dto.question.UpdateQuestionDto;
import quizz.service.interfaces.QuestionService;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
@Tag(name = "Question management", description = "Endpoints for question management")
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create new question", description = "Endpoint for creating a question")
    QuestionDto createQuestion(@Valid @RequestBody QuestionRequestDto questionRequestDto) {
        return questionService.createQuestion(questionRequestDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER)")
    @Operation(summary = "Get a question", description = "Get a question by id")
    QuestionDto getQuestion(@PathVariable Long id) {
        return questionService.getQuestion(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get list of questions", description = "Get list of questions")
    List<QuestionDto> getListOfQuestions(@RequestParam String categoryName) {
        return questionService.getAllQuestionsByCategory(categoryName);
    }

    @PutMapping
    @PreAuthorize("hasRole('USER)")
    @Operation(summary = "Update a question", description = "Update a question by values")
    QuestionDto updateAQuestion(@RequestBody UpdateQuestionDto updateQuestionDto) {
        return questionService.updateQuestion(updateQuestionDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a question", description = "Delete a question")
    void deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
    }
}
