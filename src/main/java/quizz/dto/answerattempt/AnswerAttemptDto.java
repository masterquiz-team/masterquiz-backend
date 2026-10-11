package quizz.dto.answerattempt;

import lombok.Data;

@Data
public class AnswerAttemptDto {
    private Long id;
    private Long quizAttemptId;
    private Long answerId;
    private boolean correctAnswer;
}
