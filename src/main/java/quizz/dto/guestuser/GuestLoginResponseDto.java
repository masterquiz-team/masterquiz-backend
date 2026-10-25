package quizz.dto.guestuser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestLoginResponseDto {
    private String token;
    private GuestUserDto guestUserDto;
}
