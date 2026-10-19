package quizz.dto.quizattempt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QuizAttemptRandomRequestDto {
    @NotBlank
    @Size(min = 2, max = 20)
    private String quizTitle;
    @NotBlank
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^\\p{IsAlphabetic}+$",
            message = "Field can contain only letters")
    private String category;
    @NotBlank
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^\\p{IsAlphabetic}+$",
            message = "Field can contain only letters")
    private String difficulty;
}
