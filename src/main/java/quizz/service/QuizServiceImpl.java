package quizz.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quizz.dto.quiz.QuizDto;
import quizz.dto.quiz.QuizRequestDto;
import quizz.dto.quiz.UpdateQuizDto;
import quizz.exception.EntityNotFoundException;
import quizz.exception.InvalidQuizQuestionCountException;
import quizz.mapper.QuizMapper;
import quizz.model.Category;
import quizz.model.Question;
import quizz.model.Quiz;
import quizz.repository.CategoryRepository;
import quizz.repository.QuestionRepository;
import quizz.repository.QuizRepository;
import quizz.service.interfaces.QuizService;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final QuizMapper mapper;

    @Override
    @Transactional
    public QuizDto createQuiz(QuizRequestDto quizRequestDto) {
        Category category = findCategoryById(quizRequestDto.getCategoryId());

        findQuestionsById(quizRequestDto.getQuestionIds());

        List<Question> listOfQuestions = questionRepository
                .findAllById(quizRequestDto.getQuestionIds());

        sizeValidation(listOfQuestions.size());

        Quiz quiz = mapper.toModel(quizRequestDto);

        quiz.setCategory(category);
        quiz.setListOfQuestions(listOfQuestions);

        Quiz savedQuiz = quizRepository.save(quiz);

        return mapper.toDto(savedQuiz);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizDto getQuiz(Long id) {
        Quiz quiz = findById(id);
        return mapper.toDto(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuizDto> getPageOfQuizByCategoryId(Long categoryId, Pageable pageable) {
        return quizRepository.findByCategoryId(categoryId, pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional
    public QuizDto updateQuiz(UpdateQuizDto updateQuizDto) {
        Quiz quiz = findById(updateQuizDto.getQuizId());
        Category category = findCategoryById(updateQuizDto.getCategoryId());
        quiz.setCategory(category);

        findQuestionsById(updateQuizDto.getQuestionIds());

        List<Question> questions = questionRepository.findAllById(updateQuizDto.getQuestionIds());

        sizeValidation(questions.size());

        mapper.update(quiz, updateQuizDto);
        quiz.setListOfQuestions(questions);

        return mapper.toDto(quiz);
    }

    @Override
    @Transactional
    public void deleteQuiz(Long id) {
        Quiz quiz = findById(id);
        quizRepository.delete(quiz);
    }

    private Quiz findById(Long id) {
        return quizRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Quiz with id: " + id + " doesn't exist"));
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category with id: "
                        + id + " doesn't exist"));
    }

    private void findQuestionsById(List<Long> ids) {
        for (Long id : ids) {
            if (questionRepository.findById(id).isEmpty()) {
                throw new EntityNotFoundException("Question with id: "
                        + id + " doesn't exist");
            }
        }
    }

    private void sizeValidation(int size) {
        if (size != 10) {
            throw new InvalidQuizQuestionCountException(
                    "Each quiz requires exactly 10 questions, provided: "
                            + size);
        }
    }
}
