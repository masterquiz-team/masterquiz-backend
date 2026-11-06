package quizz.dto.category;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CategoryRequestDto(@NotEmpty @Size(min = 2, max = 20) String name) {
}
