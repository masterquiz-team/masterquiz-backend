package quizz.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryUpdateDto(@NotNull Long id, @NotBlank String name) {
}
