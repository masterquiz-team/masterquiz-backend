package quizz.dto.answerattempt;

import java.util.List;
import lombok.Data;

@Data
public class AnswerAttemptDto {
    private Long id;
    private Long quizAttemptId;
    private Long answerId;
    private Long questionId;
    private List<Long> correctAnswerIds;
    private String feedback;
    private boolean correctAnswer;
}
