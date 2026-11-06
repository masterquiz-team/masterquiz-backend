package quizz.dto.register;

import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String email;
    private String nickname;

}
