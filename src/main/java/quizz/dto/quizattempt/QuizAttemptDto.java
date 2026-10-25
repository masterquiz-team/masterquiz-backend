package quizz.dto.quizattempt;

import java.time.LocalDateTime;
import lombok.Data;
import quizz.model.Status;

@Data
public class QuizAttemptDto {
    private Long id;
    private Long quizId;
    private Long userId;
    private Long guestUserId;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Status status;
}
