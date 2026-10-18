package quizz.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequestDto(@NotBlank @Size(min = 2, max = 20) @Pattern(
        regexp = "^\\p{IsAlphabetic}+$",
        message = "Field can contain only letters"
)String name) {
}
