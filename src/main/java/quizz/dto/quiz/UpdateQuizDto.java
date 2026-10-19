package quizz.dto.quiz;

import java.util.List;
import lombok.Data;

@Data
public class UpdateQuizDto {
    private String title;
    private Long quizId;
    private String difficulty;
    private Long categoryId;
    private List<Long> questionIds;
}
