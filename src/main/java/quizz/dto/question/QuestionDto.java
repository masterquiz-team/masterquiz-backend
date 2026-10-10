package quizz.dto.question;

import lombok.Data;
import quizz.model.Category;

@Data
public class QuestionDto {
    private Long id;
    private String text;
    private String feedback;
    private Category category;
}
