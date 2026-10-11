package quizz.dto.quiz;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class UpdateQuizDto {
    @NotBlank
    private String title;
    @NotNull
    private Long quizId;
    @NotNull
    private Long categoryId;
    @NotEmpty
    @Size(min = 10, max = 10)
    private List<Long> questionIds;
}
