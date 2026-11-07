package quizz.dto.achievement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AchievementRequestDto {
    @NotBlank
    @Size(min = 5, max = 50)
    private String title;
    @NotBlank
    @Size(max = 100)
    private String description;
    @NotBlank
    private String achievementImage;
    @NotBlank
    private String rarity;
    @NotBlank
    @Pattern(regexp = "^First-Steps\\d+$",
            message = "Achievement type has to be same as title with "
                    + "- as a space. Example: First-Steps")
    private String achievementType;
}
