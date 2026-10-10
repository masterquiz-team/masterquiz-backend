package quizz.service.interfaces;

import java.util.List;
import quizz.dto.question.QuestionDto;
import quizz.dto.question.QuestionRequestDto;
import quizz.dto.question.UpdateQuestionDto;

public interface QuestionService {
    QuestionDto createQuestion(QuestionRequestDto dto);

    QuestionDto getQuestion(Long id);

    List<QuestionDto> getAllQuestionsByCategory(String categoryName);

    QuestionDto updateQuestion(UpdateQuestionDto dto);

    void deleteQuestion(Long id);
}
