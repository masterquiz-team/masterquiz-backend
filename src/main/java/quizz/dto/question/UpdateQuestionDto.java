package quizz.dto.question;

import lombok.Data;

@Data
public class UpdateQuestionDto {
    private Long id;
    private String text;
    private String feedback;
    private Long categoryId;
}
