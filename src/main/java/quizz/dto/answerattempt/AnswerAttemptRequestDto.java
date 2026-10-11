package quizz.dto.answerattempt;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnswerAttemptRequestDto {
    @NotNull
    private Long quizAttemptId;
    @NotNull
    private Long answerId;
}
