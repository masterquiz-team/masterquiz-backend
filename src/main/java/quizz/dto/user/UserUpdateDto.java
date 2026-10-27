package quizz.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserUpdateDto {
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$",
            message = "Special characters are not allowed")
    private String nickname;
    @Pattern(regexp = "^avatar-\\d+$",
            message = "Avatar ID must start with 'avatar-' and end with a number")
    @NotBlank
    private String avatarId;
}
