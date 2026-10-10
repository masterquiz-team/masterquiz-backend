package quizz.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryUpdateDto(@NotNull Long id, @NotBlank @Size(min = 2, max = 20) String name) {
}
