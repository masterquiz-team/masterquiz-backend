package quizz.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import quizz.dto.question.QuestionDto;
import quizz.dto.question.QuestionRequestDto;
import quizz.dto.question.UpdateQuestionDto;
import quizz.exception.EntityFoundException;
import quizz.exception.EntityNotFoundException;
import quizz.mapper.QuestionMapper;
import quizz.model.Category;
import quizz.model.Difficulty;
import quizz.model.Question;
import quizz.repository.CategoryRepository;
import quizz.repository.QuestionRepository;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceImplTest {
    private static final Long ID = 1L;

    @Mock
    private QuestionMapper questionMapper;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private QuestionServiceImpl questionService;

    @Test
    void createQuestion_ValidQuestionRequestDto_ReturnsQuestionDto() {
        //given
        QuestionRequestDto questionRequestDto = createQuestionRequest();
        Category category = createCategory();
        Question question = createQuestion(category);
        QuestionDto excepted = createQuestionDto(category);

        when(questionRepository.findByText(questionRequestDto.getText()))
                .thenReturn(Optional.empty());
        when(categoryRepository.findById(ID)).thenReturn(Optional.of(category));
        when(questionMapper.toModel(questionRequestDto)).thenReturn(question);
        when(questionRepository.save(any(Question.class))).thenReturn(question);
        when(questionMapper.toDto(question)).thenReturn(excepted);

        //when
        QuestionDto actual = questionService.createQuestion(questionRequestDto);

        //then
        Assertions.assertEquals(excepted, actual);
    }

    @Test
    void createQuestion_questionAlreadyExist_Throws409() {
        //given
        QuestionRequestDto questionRequestDto = createQuestionRequest();
        Category category = createCategory();
        Question question = createQuestion(category);
        when(questionRepository.findByText(questionRequestDto.getText()))
                .thenReturn(Optional.of(question));

        //when & then
        Assertions.assertThrows(EntityFoundException.class,
                () -> questionService.createQuestion(questionRequestDto));
    }

    @Test
    void createQuestion_categoryDoesntExist_Throws404() {
        //given
        QuestionRequestDto questionRequestDto = createQuestionRequest();
        when(questionRepository.findByText(questionRequestDto.getText()))
                .thenReturn(Optional.empty());
        when(categoryRepository.findById(questionRequestDto.getCategoryId()))
                .thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> questionService.createQuestion(questionRequestDto));
    }

    @Test
    void getQuestion_ValidId_ReturnsQuestionDto() {
        //given
        Category category = createCategory();
        Question question = createQuestion(category);
        QuestionDto expected = createQuestionDto(category);

        when(questionRepository.findById(ID)).thenReturn(Optional.of(question));
        when(questionMapper.toDto(question)).thenReturn(expected);

        //when
        QuestionDto actual = questionService.getQuestion(ID);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getQuestion_InvalidId_Throws404() {
        //given
        when(questionRepository.findById(ID)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> questionService.getQuestion(ID));
    }

    @Test
    void getAllQuestionsByCategory_ValidCategoryName_ReturnsListOfQuestionDto() {
        //given
        Category category = createCategory();
        Question question = createQuestion(category);
        QuestionDto questionDto = createQuestionDto(category);
        List<QuestionDto> expected = List.of(questionDto);

        when(categoryRepository.findByName(category.getName())).thenReturn(Optional.of(category));
        when(questionRepository.findAll()).thenReturn(List.of(question));
        when(questionMapper.toDto(question)).thenReturn(questionDto);

        //when
        List<QuestionDto> actual = questionService.getAllQuestionsByCategory(category.getName());

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getAllQuestionsByCategory_ValidCategoryNameButNoQuestions_ReturnsEmptyListOfQuestions() {
        //given
        Category category = createCategory();
        List<QuestionDto> expected = List.of();
        when(categoryRepository.findByName(category.getName())).thenReturn(Optional.of(category));
        when(questionRepository.findAll()).thenReturn(List.of());

        //when
        List<QuestionDto> actual = questionService.getAllQuestionsByCategory(category.getName());

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(0, actual.size());
    }

    @Test
    void getAllQuestionsByCategory_InvalidCategoryName_Throws404() {
        //given
        Category category = createCategory();
        when(categoryRepository.findByName(category.getName())).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> questionService.getAllQuestionsByCategory(category.getName()));
    }

    @Test
    void updateQuestion_ValidUpdateQuestionDto_ReturnsQuestionDto() {
        //given
        Category category = createCategory();
        Question question = createQuestion(category);
        QuestionDto expected = createQuestionDto(category);
        expected.setText("Not basic text");

        UpdateQuestionDto updateQuestionDto = createUpdateQuestionDto();
        when(questionRepository.findById(ID)).thenReturn(Optional.of(question));
        when(categoryRepository.findById(ID)).thenReturn(Optional.of(category));
        doNothing().when(questionMapper).update(updateQuestionDto, question);
        when(questionMapper.toDto(question)).thenReturn(expected);

        //when
        QuestionDto actual = questionService.updateQuestion(updateQuestionDto);

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(expected.getText(), actual.getText());
    }

    @Test
    void updateQuestion_QuestionDoesntExist_Throws404() {
        //given
        UpdateQuestionDto updateQuestionDto = createUpdateQuestionDto();

        when(questionRepository.findById(ID)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> questionService.updateQuestion(updateQuestionDto));
    }

    @Test
    void updateQuestion_CategoryDoesntExist_Throws404() {
        //given
        UpdateQuestionDto updateQuestionDto = createUpdateQuestionDto();
        Question question = createQuestion(createCategory());

        when(questionRepository.findById(ID)).thenReturn(Optional.of(question));
        when(categoryRepository.findById(ID)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> questionService.updateQuestion(updateQuestionDto));
    }

    @Test
    void deleteQuestion_ValidId_Ok() {
        //given
        Category category = createCategory();
        Question question = createQuestion(category);

        //when
        questionRepository.delete(question);

        //then
        verify(questionRepository).delete(question);
    }

    @Test
    void deleteQuestion_InvalidId_Throws404() {
        //given
        when(questionRepository.findById(ID)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> questionService.deleteQuestion(ID));
    }

    private UpdateQuestionDto createUpdateQuestionDto() {
        UpdateQuestionDto updateQuestionDto = new UpdateQuestionDto();
        updateQuestionDto.setText("Not basic text");
        updateQuestionDto.setId(ID);
        updateQuestionDto.setFeedback("Basic feedback");
        updateQuestionDto.setDifficultyName("Easy");
        updateQuestionDto.setCategoryId(ID);
        return updateQuestionDto;
    }

    private QuestionDto createQuestionDto(Category category) {
        QuestionDto question = new QuestionDto();
        question.setCategory(category);
        question.setCorrectAnswer(true);
        question.setDifficultyName("Easy");
        question.setId(ID);
        question.setFeedback("Basic feedback");
        question.setAnswerCounter(4);
        question.setText("Basic text");
        return question;
    }

    private Question createQuestion(Category category) {
        Question question = new Question();
        question.setCategory(category);
        question.setCorrectAnswer(true);
        question.setDifficulty(Difficulty.EASY);
        question.setId(ID);
        question.setFeedback("Basic feedback");
        question.setAnswerCounter(4);
        question.setText("Basic text");
        return question;
    }

    private QuestionRequestDto createQuestionRequest() {
        QuestionRequestDto questionRequestDto = new QuestionRequestDto();
        questionRequestDto.setCategoryId(ID);
        questionRequestDto.setText("Basic text");
        questionRequestDto.setFeedback("Basic feedback");
        questionRequestDto.setDifficultyName("Easy");
        return questionRequestDto;
    }

    private Category createCategory() {
        Category category = new Category();
        category.setName("Geography");
        category.setId(ID);
        return category;
    }
}
