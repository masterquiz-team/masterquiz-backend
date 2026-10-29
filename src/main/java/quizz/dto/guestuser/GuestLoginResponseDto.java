package quizz.dto.guestuser;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestLoginResponseDto {
    private String token;
    private GuestUserDto guestUserDto;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GuestLoginResponseDto that = (GuestLoginResponseDto) o;
        return Objects.equals(token, that.token) && Objects.equals(guestUserDto, that.guestUserDto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, guestUserDto);
    }
}
