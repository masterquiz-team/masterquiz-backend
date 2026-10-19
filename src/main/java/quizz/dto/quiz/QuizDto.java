package quizz.dto.quiz;

import java.util.List;
import lombok.Data;
import quizz.dto.question.QuestionDto;

@Data
public class QuizDto {
    private Long id;
    private String title;
    private String difficulty;
    private Long categoryId;
    private List<QuestionDto> listOfQuestions;
}
