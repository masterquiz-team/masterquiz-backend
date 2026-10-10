package quizz.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import quizz.model.Category;

@Data
public class QuestionRequestDto {
    @NotBlank
    @Size(min = 2, max = 30)
    private String text;
    @NotBlank
    @Size(min = 2, max = 30)
    private String feedback;
    @NotBlank
    private Category category;
}
