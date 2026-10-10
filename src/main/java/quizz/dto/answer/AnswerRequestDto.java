package quizz.dto.answer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AnswerRequestDto {
    @NotBlank
    @Size(min = 2, max = 10)
    private String text;
    @NotNull
    private boolean correct;
    @NotNull
    private Long questionId;
}
