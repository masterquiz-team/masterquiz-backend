package quizz.dto.answer;

import lombok.Data;

@Data
public class UpdateAnswerDto {
    private Long id;
    private String text;
    private boolean correct;
}
