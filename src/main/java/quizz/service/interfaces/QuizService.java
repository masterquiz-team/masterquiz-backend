package quizz.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import quizz.dto.quiz.QuizDto;
import quizz.dto.quiz.QuizRequestDto;
import quizz.dto.quiz.UpdateQuizDto;

public interface QuizService {
    QuizDto createQuiz(QuizRequestDto quizRequestDto);

    QuizDto getQuiz(Long id);

    Page<QuizDto> getPageOfQuizByCategoryId(Long categoryId, Pageable pageable);

    QuizDto updateQuiz(UpdateQuizDto updateQuizDto);

    void deleteQuiz(Long id);
}
