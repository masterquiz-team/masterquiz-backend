package quizz.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import quizz.dto.question.QuestionDto;
import quizz.dto.quiz.QuizDto;
import quizz.dto.quiz.QuizRequestDto;
import quizz.dto.quiz.UpdateQuizDto;
import quizz.exception.EntityNotFoundException;
import quizz.exception.InvalidQuizQuestionCountException;
import quizz.mapper.QuizMapper;
import quizz.model.Category;
import quizz.model.Difficulty;
import quizz.model.Question;
import quizz.model.Quiz;
import quizz.repository.CategoryRepository;
import quizz.repository.QuestionRepository;
import quizz.repository.QuizRepository;

@ExtendWith(MockitoExtension.class)
public class QuizServiceImplTest {
    private static final Long ID = 1L;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuizMapper quizMapper;

    @InjectMocks
    private QuizServiceImpl quizService;

    @Test
    void createQuiz_ValidRequestDto_ReturnsQuizDto() {
        //given
        QuizRequestDto quizRequestDto = createQuizRequestDto();
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            questions.add(question);
        }

        QuestionDto questionDto = createQuestionDto(category);

        List<QuestionDto> dtos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            dtos.add(questionDto);
        }

        Quiz quiz = createQuiz(category, questions);
        QuizDto expected = createQuizDto(quiz, dtos);

        when(questionRepository.findById(ID)).thenReturn(Optional.of(question));
        when(questionRepository.findAllById(quizRequestDto.getQuestionIds()))
                .thenReturn(questions);
        when(categoryRepository.findById(ID)).thenReturn(Optional.of(category));
        when(quizMapper.toModel(quizRequestDto)).thenReturn(quiz);
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);
        when(quizMapper.toDto(quiz)).thenReturn(expected);

        //when
        QuizDto actual = quizService.createQuiz(quizRequestDto);

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(category, quiz.getCategory());
        Assertions.assertEquals(questions, quiz.getListOfQuestions());
    }

    @Test
    void createQuiz_InvalidId_Throws404() {
        //given
        QuizRequestDto quizRequestDto = createQuizRequestDto();
        when(questionRepository.findById(ID))
                .thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> quizService.createQuiz(quizRequestDto));
    }

    @Test
    void createQuiz_InvalidSizeOfTheList_Throws400() {
        //given
        QuizRequestDto quizRequestDto = createQuizRequestDto();
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> questions = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            questions.add(question);
        }

        when(questionRepository.findById(ID)).thenReturn(Optional.of(question));
        when(questionRepository.findAllById(List.of(ID))).thenReturn(questions);

        //when & then
        Assertions.assertThrows(InvalidQuizQuestionCountException.class,
                () -> quizService.createQuiz(quizRequestDto));
    }

    @Test
    void createQuiz_InvalidEnum_Throws404() {
        //given
        QuizRequestDto quizRequestDto = createQuizRequestDto();
        quizRequestDto.setDifficulty("NotValidDifficulty");
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> questions = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            questions.add(question);
        }

        when(questionRepository.findById(ID)).thenReturn(Optional.of(question));
        when(questionRepository.findAllById(List.of(ID))).thenReturn(questions);

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> quizService.createQuiz(quizRequestDto));
    }

    @Test
    void createQuiz_InvalidCategoryId_Throws404() {
        //given
        QuizRequestDto quizRequestDto = createQuizRequestDto();
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> questions = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            questions.add(question);
        }

        when(questionRepository.findById(ID)).thenReturn(Optional.of(question));
        when(questionRepository.findAllById(List.of(ID))).thenReturn(questions);
        when(categoryRepository.findById(ID)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> quizService.createQuiz(quizRequestDto));
    }

    @Test
    void getQuiz_ValidId_ReturnsQuizDto() {
        //given
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> questions = List.of(question);
        Quiz quiz = createQuiz(category, questions);
        QuestionDto questionDto = createQuestionDto(category);

        List<QuestionDto> dtos = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            dtos.add(questionDto);
        }

        QuizDto expected = createQuizDto(quiz, dtos);

        when(quizRepository.findById(ID)).thenReturn(Optional.of(quiz));
        when(quizMapper.toDto(quiz)).thenReturn(expected);

        //when
        QuizDto actual = quizService.getQuiz(ID);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getQuiz_InvalidId_Throws404() {
        //given
        when(quizRepository.findById(ID)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> quizService.getQuiz(ID));
    }

    @Test
    void getPageOfQuizByCategoryId_ValidId_ReturnsPageOfQuizDto() {
        //given
        Pageable pageable = Pageable.ofSize(10);
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> questions = List.of(question);
        Quiz quiz = createQuiz(category, questions);
        Page<Quiz> quizPage = new PageImpl<>(List.of(quiz));
        QuestionDto questionDto = createQuestionDto(category);

        List<QuestionDto> dtos = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            dtos.add(questionDto);
        }

        QuizDto quizDto = createQuizDto(quiz, dtos);
        Page<QuizDto> expected = new PageImpl<>(List.of(quizDto));

        when(quizRepository.findByCategoryId(ID, pageable)).thenReturn(quizPage);
        when(quizMapper.toDto(quiz)).thenReturn(quizDto);

        //when
        Page<QuizDto> actual = quizService.getPageOfQuizByCategoryId(ID, pageable);

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(1, actual.getTotalElements());
    }

    @Test
    void getPageOfQuizByCategoryId_InvalidId_ReturnsEmptyPage() {
        //given
        Pageable pageable = Pageable.ofSize(10);
        Page<QuizDto> expected = new PageImpl<>(List.of());

        when(quizRepository.findByCategoryId(ID, pageable)).thenReturn(Page.empty());

        //when
        Page<QuizDto> actual = quizService.getPageOfQuizByCategoryId(ID, pageable);

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(0, actual.getTotalElements());
    }

    @Test
    void updateQuiz_ValidUpdateDto_ReturnsQuizDto() {
        //given
        Category category = createCategory();
        Question question = createQuestion(category);
        QuestionDto questionDto = createQuestionDto(category);
        Quiz quiz = createQuiz(category, List.of(question));
        UpdateQuizDto updateQuizDto = createUpdateQuizDto();
        List<Question> questions = new ArrayList<>();

        List<QuestionDto> dtos = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            dtos.add(questionDto);
        }

        QuizDto expected = createQuizDtoByUpdatedQuiz(updateQuizDto, dtos);

        for (int i = 0; i < 10; i++) {
            questions.add(question);
        }

        when(quizRepository.findById(ID)).thenReturn(Optional.of(quiz));
        when(categoryRepository.findById(ID)).thenReturn(Optional.of(category));
        when(questionRepository.findById(ID)).thenReturn(Optional.of(question));
        when(questionRepository.findAllById(updateQuizDto.getQuestionIds()))
                .thenReturn(questions);
        doNothing().when(quizMapper).update(quiz, updateQuizDto);
        when(quizMapper.toDto(quiz)).thenReturn(expected);

        //when
        QuizDto actual = quizService.updateQuiz(updateQuizDto);

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(10, actual.getListOfQuestions().size());
        Assertions.assertEquals("Not basic title", actual.getTitle());
    }

    @Test
    void updateQuiz_InvalidId_Throws404() {
        //given
        UpdateQuizDto updateQuizDto = createUpdateQuizDto();

        when(quizRepository.findById(ID)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> quizService.updateQuiz(updateQuizDto));
    }

    @Test
    void updateQuiz_InvalidCategoryId_Throws404() {
        //given
        UpdateQuizDto updateQuizDto = createUpdateQuizDto();
        Category category = createCategory();
        Question question = createQuestion(category);
        Quiz quiz = createQuiz(category, List.of(question));

        when(quizRepository.findById(ID)).thenReturn(Optional.of(quiz));
        when(categoryRepository.findById(ID)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> quizService.updateQuiz(updateQuizDto));
    }

    @Test
    void updateQuiz_InvalidQuestionId_Throws404() {
        //given
        UpdateQuizDto updateQuizDto = createUpdateQuizDto();
        Category category = createCategory();
        Question question = createQuestion(category);
        Quiz quiz = createQuiz(category, List.of(question));

        when(quizRepository.findById(ID)).thenReturn(Optional.of(quiz));
        when(categoryRepository.findById(ID)).thenReturn(Optional.of(category));
        when(questionRepository.findById(ID)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> quizService.updateQuiz(updateQuizDto));
    }

    @Test
    void updateQuiz_InvalidSizeOfList_Throws400() {
        //given
        UpdateQuizDto updateQuizDto = createUpdateQuizDto();
        Category category = createCategory();
        Question question = createQuestion(category);
        Quiz quiz = createQuiz(category, List.of(question));
        List<Question> questions = List.of(question);

        when(quizRepository.findById(ID)).thenReturn(Optional.of(quiz));
        when(categoryRepository.findById(ID)).thenReturn(Optional.of(category));
        when(questionRepository.findById(ID)).thenReturn(Optional.of(question));
        when(questionRepository.findAllById(List.of(ID))).thenReturn(questions);

        //when & then
        Assertions.assertThrows(InvalidQuizQuestionCountException.class,
                () -> quizService.updateQuiz(updateQuizDto));
    }

    @Test
    void deleteQuiz_ValidId_Ok() {
        //given
        Category category = createCategory();
        Question question = createQuestion(category);
        Quiz quiz = createQuiz(category, List.of(question));

        when(quizRepository.findById(ID)).thenReturn(Optional.of(quiz));

        //when
        quizService.deleteQuiz(ID);

        //then
        verify(quizRepository).delete(quiz);
    }

    @Test
    void deleteQuiz_InvalidId_Throws404() {
        //given
        when(quizRepository.findById(ID)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> quizService.deleteQuiz(ID));
    }

    private QuizDto createQuizDtoByUpdatedQuiz(UpdateQuizDto quiz, List<QuestionDto> dtos) {
        QuizDto quizDto = new QuizDto();
        quizDto.setId(ID);
        quizDto.setCategoryId(quiz.getCategoryId());
        quizDto.setDifficulty(quiz.getDifficulty());
        quizDto.setTitle(quiz.getTitle());
        quizDto.setListOfQuestions(dtos);
        return quizDto;
    }

    private UpdateQuizDto createUpdateQuizDto() {
        UpdateQuizDto updateQuizDto = new UpdateQuizDto();
        updateQuizDto.setQuizId(ID);
        updateQuizDto.setTitle("Not basic title");
        updateQuizDto.setDifficulty("Easy");
        updateQuizDto.setCategoryId(ID);
        updateQuizDto.setQuestionIds(List.of(ID));
        return updateQuizDto;
    }

    private QuestionDto createQuestionDto(Category category) {
        QuestionDto questionDto = new QuestionDto();
        questionDto.setText("Basic text");
        questionDto.setDifficultyName("Easy");
        questionDto.setId(ID);
        questionDto.setCategory(category);
        questionDto.setFeedback("Basic feedback");
        questionDto.setAnswerCounter(4);
        questionDto.setCorrectAnswer(true);
        return questionDto;
    }

    private Category createCategory() {
        Category category = new Category();
        category.setId(ID);
        category.setName("Geography");
        return category;
    }

    private Question createQuestion(Category category) {
        Question question = new Question();
        question.setText("Basic text");
        question.setCategory(category);
        question.setAnswerCounter(4);
        question.setFeedback("Basic feedback");
        question.setDifficulty(Difficulty.EASY);
        question.setCorrectAnswer(true);
        question.setId(ID);
        return question;
    }

    private QuizRequestDto createQuizRequestDto() {
        QuizRequestDto quizRequestDto = new QuizRequestDto();
        quizRequestDto.setDifficulty("Easy");
        quizRequestDto.setCategoryId(ID);
        quizRequestDto.setTitle("Basic title");
        quizRequestDto.setQuestionIds(List.of(ID));
        return quizRequestDto;
    }

    private Quiz createQuiz(Category category, List<Question> questions) {
        Quiz quiz = new Quiz();
        quiz.setCategory(category);
        quiz.setListOfQuestions(questions);
        quiz.setId(ID);
        quiz.setTitle("Basic title");
        quiz.setDifficulty(Difficulty.EASY);
        return quiz;
    }

    private QuizDto createQuizDto(Quiz quiz, List<QuestionDto> list) {
        QuizDto quizDto = new QuizDto();
        quizDto.setListOfQuestions(list);
        quizDto.setTitle(quiz.getTitle());
        quizDto.setId(ID);
        quizDto.setDifficulty("Easy");
        quizDto.setCategoryId(ID);
        return quizDto;
    }
}
