package quizz.dto.quizattempt;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuizAttemptRequestDto {
    @NotNull
    private Long quizId;
}
