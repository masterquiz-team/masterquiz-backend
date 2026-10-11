package quizz.dto.answer;

import lombok.Data;

@Data
public class AnswerDtoWithoutCorrectFieldDto {
    private Long id;
    private String text;
    private Long questionId;
}
