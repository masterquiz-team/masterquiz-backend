package quizz.dto.answer;

import lombok.Data;

@Data
public class AnswerDto {
    private Long id;
    private String text;
    private boolean correct;
    private Long questionId;
}
