package quizz.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QuestionRequestDto {
    @NotBlank
    @Size(min = 2, max = 30)
    private String text;
    @NotBlank
    @Size(min = 2, max = 30)
    private String feedback;
    @NotNull
    private Long categoryId;
}
