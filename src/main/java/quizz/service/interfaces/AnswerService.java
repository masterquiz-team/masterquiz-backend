package quizz.service.interfaces;

import java.util.List;
import quizz.dto.answer.AnswerDto;
import quizz.dto.answer.AnswerRequestDto;
import quizz.dto.answer.UpdateAnswerDto;

public interface AnswerService {
    AnswerDto createAnswer(AnswerRequestDto answerRequestDto);

    AnswerDto getAnswer(Long id);

    List<AnswerDto> getAnswersByQuestionId(Long id);

    AnswerDto updateAnswer(UpdateAnswerDto updateAnswerDto);

    void deleteAnswer(Long id);
}
