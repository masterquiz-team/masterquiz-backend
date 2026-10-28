package quizz.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import quizz.dto.answer.AnswerDto;
import quizz.dto.answer.AnswerDtoWithoutCorrectFieldDto;
import quizz.dto.answer.AnswerRequestDto;
import quizz.dto.answer.UpdateAnswerDto;
import quizz.exception.AnswerLimitExceededException;
import quizz.exception.CorrectAnswerAlreadyExistException;
import quizz.exception.EntityNotFoundException;
import quizz.exception.QuestionAnswerCounterException;
import quizz.mapper.AnswerMapper;
import quizz.model.Answer;
import quizz.model.Category;
import quizz.model.Difficulty;
import quizz.model.Question;
import quizz.repository.AnswerRepository;
import quizz.repository.QuestionRepository;

@ExtendWith(MockitoExtension.class)
public class AnswerServiceImplTest {

    private static final Long ID = 1L;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerMapper answerMapper;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private AnswerServiceImpl answerService;

    @Test
    void createAnswer_ValidAnswerRequestDto_ReturnsAnswerDto() {
        //given
        Category category = createCategory();
        Optional<Question> question = createQuestion(category);
        Question unwrappedQuestion = question.get();
        AnswerRequestDto answerRequestDto = createAnswerRequestDto();
        Optional<Answer> answer = createAnswer(unwrappedQuestion);
        Answer unwrappedAnswer = answer.get();
        AnswerDto expected = createAnswerDto();

        when(questionRepository.findById(ID)).thenReturn(question);
        when(answerMapper.toModel(answerRequestDto)).thenReturn(unwrappedAnswer);
        when(answerRepository.save(any(Answer.class))).thenReturn(unwrappedAnswer);
        when(answerMapper.toDto(any(Answer.class))).thenReturn(expected);

        //when
        AnswerDto actual = answerService.createAnswer(answerRequestDto);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createAnswer_InvalidRequestDto_Throws404() {
        //given
        AnswerRequestDto answerRequestDto = createAnswerRequestDto();

        when(questionRepository.findById(ID)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> answerService.createAnswer(answerRequestDto));
    }

    @Test
    void createAnswer_InvalidAnswerCounter_Throws400() {
        //given
        AnswerRequestDto answerRequestDto = createAnswerRequestDto();
        Category category = createCategory();
        Optional<Question> question = createQuestion(category);
        Question unwrappedQuestion = question.get();
        unwrappedQuestion.setAnswerCounter(5);

        when(questionRepository.findById(ID)).thenReturn(question);

        //when & then
        Assertions.assertThrows(QuestionAnswerCounterException.class,
                () -> answerService.createAnswer(answerRequestDto));
    }

    @Test
    void createAnswer_NoneOfAnswersAreCorrect_Throws400() {
        //given
        AnswerRequestDto answerRequestDto = createAnswerRequestDto();
        answerRequestDto.setCorrect(false);
        Category category = createCategory();
        Optional<Question> question = createQuestion(category);
        Question unwrappedQuestion = question.get();
        unwrappedQuestion.setAnswerCounter(3);
        Optional<Answer> answer = createAnswer(unwrappedQuestion);
        Answer unwrappedAnswer = answer.get();
        unwrappedAnswer.setCorrect(false);
        unwrappedQuestion.setCorrectAnswer(false);

        when(questionRepository.findById(ID)).thenReturn(question);

        //when & then
        Assertions.assertThrows(AnswerLimitExceededException.class,
                () -> answerService.createAnswer(answerRequestDto));
    }

    @Test
    void createAnswer_CorrectAnswerAlreadyExist_Throws400() {
        //given
        AnswerRequestDto answerRequestDto = createAnswerRequestDto();
        Category category = createCategory();
        Optional<Question> question = createQuestion(category);
        Question unwrappedQuestion = question.get();
        unwrappedQuestion.setCorrectAnswer(true);

        when(questionRepository.findById(ID)).thenReturn(question);

        //when & then
        Assertions.assertThrows(CorrectAnswerAlreadyExistException.class,
                () -> answerService.createAnswer(answerRequestDto));
    }

    @Test
    void createAnswer_setAnswerCorrect_ReturnsTrue() {
        //given
        Category category = createCategory();
        Optional<Question> question = createQuestion(category);
        Question unwrappedQuestion = question.get();
        AnswerRequestDto answerRequestDto = createAnswerRequestDto();
        Optional<Answer> answer = createAnswer(unwrappedQuestion);
        Answer unwrappedAnswer = answer.get();
        AnswerDto expected = createAnswerDto();

        when(questionRepository.findById(ID)).thenReturn(question);
        when(answerMapper.toModel(answerRequestDto)).thenReturn(unwrappedAnswer);
        when(answerRepository.save(any(Answer.class))).thenReturn(unwrappedAnswer);
        when(answerMapper.toDto(any(Answer.class))).thenReturn(expected);

        AnswerDto actual = answerService.createAnswer(answerRequestDto);

        //when & then
        Assertions.assertTrue(actual.isCorrect());
    }

    @Test
    void createAnswer_setPoints_ReturnsOk() {
        //given
        Category category = createCategory();
        Optional<Question> question = createQuestion(category);
        Question unwrappedQuestion = question.get();
        AnswerRequestDto answerRequestDto = createAnswerRequestDto();
        Optional<Answer> answer = createAnswer(unwrappedQuestion);
        Answer unwrappedAnswer = answer.get();
        AnswerDto expected = createAnswerDto();

        when(questionRepository.findById(ID)).thenReturn(question);
        when(answerMapper.toModel(answerRequestDto)).thenReturn(unwrappedAnswer);
        when(answerRepository.save(any(Answer.class))).thenReturn(unwrappedAnswer);
        when(answerMapper.toDto(any(Answer.class))).thenReturn(expected);

        AnswerDto actual = answerService.createAnswer(answerRequestDto);

        //when & then
        Assertions.assertEquals(10, actual.getPoints());
    }

    @Test
    void getAnswer_ValidId_ReturnsAnswerDto() {
        //given
        Category category = createCategory();
        Optional<Question> question = createQuestion(category);
        Optional<Answer> answer = createAnswer(question.get());
        AnswerDto expected = createAnswerDto();

        when(answerRepository.findById(ID)).thenReturn(answer);
        when(answerMapper.toDto(answer.get())).thenReturn(expected);

        //when
        AnswerDto actual = answerService.getAnswer(ID);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getAnswer_InvalidId_Throws404() {
        //given
        when(answerRepository.findById(ID)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> answerService.getAnswer(ID));
    }

    @Test
    void getAnswersByQuestionId_ValidId_ReturnsListOfAnswersDto() {
        //given
        Category category = createCategory();
        Optional<Question> question = createQuestion(category);
        Optional<Answer> answer = createAnswer(question.get());
        AnswerDto answerDto = createAnswerDto();
        List<AnswerDto> expected = List.of(answerDto);

        when(answerRepository.findByQuestionId(ID)).thenReturn(List.of(answer.get()));
        when(answerMapper.toDto(answer.get())).thenReturn(answerDto);

        //when
        List<AnswerDto> actual = answerService.getAnswersByQuestionId(ID);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getAnswersByQuestionId_InvalidId_ReturnsEmptyList() {
        //given
        when(answerRepository.findByQuestionId(ID)).thenReturn(List.of());

        //when
        List<AnswerDto> actual = answerService.getAnswersByQuestionId(ID);

        //then
        Assertions.assertEquals(List.of(), actual);
    }

    @Test
    void getAnswersByQuestionIdWithoutCorrectField_ValidId_ReturnsListOfAnswerDtoWithout() {
        //given
        Category category = createCategory();
        Optional<Question> question = createQuestion(category);
        Optional<Answer> answer = createAnswer(question.get());
        AnswerDtoWithoutCorrectFieldDto answerDtoWithoutCorrectFieldDto
                = createAnswerDtoWithoutCorrectFieldDto();
        List<AnswerDtoWithoutCorrectFieldDto> expected = List.of(answerDtoWithoutCorrectFieldDto);
        when(answerRepository.findByQuestionId(ID))
                .thenReturn(List.of(answer.get()));
        when(answerMapper.toDtoWithoutCorrectField(answer.get()))
                .thenReturn(answerDtoWithoutCorrectFieldDto);

        //when
        List<AnswerDtoWithoutCorrectFieldDto> actual = answerService
                .getAnswersByQuestionIdWithoutCorrectField(ID);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getAnswersByQuestionIdWithoutCorrectField_InvalidId_ReturnsEmptyList() {
        //given
        when(answerRepository.findByQuestionId(ID)).thenReturn(List.of());

        //when
        List<AnswerDtoWithoutCorrectFieldDto> actual
                = answerService.getAnswersByQuestionIdWithoutCorrectField(ID);

        //then
        Assertions.assertEquals(List.of(), actual);
    }

    @Test
    void updateAnswer_ValidUpdateAnswerDto_ReturnsAnswerDto() {
        //given
        Category category = createCategory();
        Optional<Question> question = createQuestion(category);
        Optional<Answer> answer = createAnswer(question.get());
        Answer unwrappedAnswer = answer.get();
        UpdateAnswerDto updateAnswerDto = createUpdateAnswerDto();
        AnswerDto expected = createAnswerDto();

        when(answerRepository.findById(ID)).thenReturn(answer);
        doNothing().when(answerMapper).updateAnswer(updateAnswerDto, unwrappedAnswer);
        when(answerMapper.toDto(unwrappedAnswer)).thenReturn(expected);

        //when
        AnswerDto actual = answerService.updateAnswer(updateAnswerDto);

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertTrue(actual.isCorrect());
    }

    @Test
    void updateAnswer_MoreThanOneAnswerCorrect_Throws400() {
        //given
        Category category = createCategory();
        Optional<Question> question = createQuestion(category);
        question.get().setCorrectAnswer(true);
        Optional<Answer> answer = createAnswer(question.get());
        Answer unwrappedAnswer = answer.get();
        unwrappedAnswer.setCorrect(false);
        UpdateAnswerDto updateAnswerDto = createUpdateAnswerDto();
        updateAnswerDto.setCorrect(true);

        when(answerRepository.findById(ID)).thenReturn(answer);

        //when & then
        Assertions.assertThrows(CorrectAnswerAlreadyExistException.class,
                () -> answerService.updateAnswer(updateAnswerDto));
    }

    @Test
    void deleteAnswer_ValidId_Ok() {
        //given
        Category category = createCategory();
        Optional<Question> question = createQuestion(category);
        question.get().setCorrectAnswer(true);
        Optional<Answer> answer = createAnswer(question.get());

        when(answerRepository.findById(ID)).thenReturn(answer);
        //when
        answerService.deleteAnswer(ID);

        //then
        Assertions.assertEquals(3, question.get().getAnswerCounter());
        Assertions.assertFalse(question.get().isCorrectAnswer());
    }

    private UpdateAnswerDto createUpdateAnswerDto() {
        UpdateAnswerDto updateAnswerDto = new UpdateAnswerDto();
        updateAnswerDto.setCorrect(true);
        updateAnswerDto.setId(ID);
        updateAnswerDto.setText("Basic text");
        return updateAnswerDto;
    }

    private AnswerDtoWithoutCorrectFieldDto createAnswerDtoWithoutCorrectFieldDto() {
        AnswerDtoWithoutCorrectFieldDto answerDtoWithoutCorrectFieldDto
                = new AnswerDtoWithoutCorrectFieldDto();
        answerDtoWithoutCorrectFieldDto.setId(ID);
        answerDtoWithoutCorrectFieldDto.setText("Basic text");
        answerDtoWithoutCorrectFieldDto.setPoints(10);
        answerDtoWithoutCorrectFieldDto.setQuestionId(ID);
        return answerDtoWithoutCorrectFieldDto;
    }

    private AnswerDto createAnswerDto() {
        AnswerDto answerDto = new AnswerDto();
        answerDto.setCorrect(true);
        answerDto.setId(ID);
        answerDto.setText("Basic text");
        answerDto.setPoints(10);
        answerDto.setQuestionId(ID);
        return answerDto;
    }

    private AnswerRequestDto createAnswerRequestDto() {
        AnswerRequestDto answerRequestDto = new AnswerRequestDto();
        answerRequestDto.setCorrect(true);
        answerRequestDto.setText("Basic text");
        answerRequestDto.setQuestionId(ID);
        return answerRequestDto;
    }

    private Optional<Question> createQuestion(Category category) {
        Question question = new Question();
        question.setCategory(category);
        question.setId(ID);
        question.setCorrectAnswer(false);
        question.setText("Question text");
        question.setDifficulty(Difficulty.EASY);
        question.setAnswerCounter(4);
        question.setFeedback("Feedback for question");
        return Optional.of(question);
    }

    private Optional<Answer> createAnswer(Question question) {
        Answer answer = new Answer();
        answer.setPoints(10);
        answer.setId(ID);
        answer.setQuestion(question);
        answer.setCorrect(true);
        answer.setText("Text for question");
        return Optional.of(answer);
    }

    private Category createCategory() {
        Category category = new Category();
        category.setName("Geography");
        category.setId(ID);
        return category;
    }
}
