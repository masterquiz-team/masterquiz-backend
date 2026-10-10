package quizz.dto.answer;

import lombok.Data;

@Data
public class UpdateRequestDto {
    private Long id;
    private String text;
    private boolean correct;
}
