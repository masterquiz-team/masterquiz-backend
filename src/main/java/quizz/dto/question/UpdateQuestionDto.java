package quizz.dto.question;

import lombok.Data;
import quizz.model.Category;

@Data
public class UpdateQuestionDto {
    private Long id;
    private String text;
    private String feedback;
    private Category category;
}
