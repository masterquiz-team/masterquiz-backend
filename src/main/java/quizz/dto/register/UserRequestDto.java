package quizz.dto.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import quizz.annotation.FieldMatch;

@Data
@FieldMatch(first = "password", second = "repeatPassword")
public class UserRequestDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String nickname;
    @NotBlank
    @Size(min = 8)
    private String password;
    @NotBlank
    @Size(min = 8)
    private String repeatPassword;
}
