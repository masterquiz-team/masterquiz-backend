package quizz.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import quizz.dto.quizattempt.QuizAttemptDto;
import quizz.dto.quizattempt.QuizAttemptRandomRequestDto;
import quizz.exception.EntityNotFoundException;
import quizz.exception.InvalidQuizAttemptStateException;
import quizz.exception.UnauthorizedAccessException;
import quizz.mapper.QuizAttemptMapper;
import quizz.model.Answer;
import quizz.model.Avatar;
import quizz.model.Category;
import quizz.model.Difficulty;
import quizz.model.GamesByCategoryResult;
import quizz.model.GuestUser;
import quizz.model.Level;
import quizz.model.Question;
import quizz.model.Quiz;
import quizz.model.QuizAttempt;
import quizz.model.Role;
import quizz.model.Status;
import quizz.model.User;
import quizz.repository.CategoryRepository;
import quizz.repository.GamesByCategoryResultRepository;
import quizz.repository.GuestUserRepository;
import quizz.repository.QuestionRepository;
import quizz.repository.QuizAttemptRepository;
import quizz.repository.QuizRepository;
import quizz.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class QuizAttemptServiceImplTest {
    private static final Long ID = 1L;
    private static final String USERNAME = "email@email.com";

    @Mock
    private QuizAttemptRepository quizAttemptRepository;
    @Mock
    private QuizAttemptMapper quizAttemptMapper;
    @Mock
    private QuizRepository quizRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private GamesByCategoryResultRepository gamesByCategoryResultRepository;
    @Mock
    private GuestUserRepository guestUserRepository;
    @InjectMocks
    private QuizAttemptServiceImpl quizAttemptService;

    @Test
    void createQuizAttempt_ValidQuizIdAndUsername_ReturnsQuizAttemptDto() {
        //given
        Optional<User> user = createUser();
        Category category = createCategory();
        Question question = createQuestion(category);
        Quiz quiz = createQuiz(category, List.of(question));
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForUser(user.get(), quiz);
        QuizAttemptDto expected = createQuizAttemptDtoForUser(user.get(), quiz);

        when(quizRepository.findById(ID)).thenReturn(Optional.of(quiz));
        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenReturn(quizAttempt.get());
        when(quizAttemptMapper.toDto(quizAttempt.get())).thenReturn(expected);

        //when
        QuizAttemptDto actual = quizAttemptService.createQuizAttempt(ID, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createQuizAttempt_ValidQuizIdAndGuestUsername_ReturnsQuizAttemptDto() {
        //given
        Optional<GuestUser> guestUser = createGuestUser();
        Category category = createCategory();
        Question question = createQuestion(category);
        Quiz quiz = createQuiz(category, List.of(question));
        Optional<QuizAttempt> guestUserQuizAttempt = createQuizAttemptForGuest(
                guestUser.get(), quiz);
        QuizAttempt unwrappedQuizAttempt = guestUserQuizAttempt.get();
        QuizAttemptDto expected = createQuizAttemptDtoForGuestUser(guestUser.get(), quiz);

        when(quizRepository.findById(ID)).thenReturn(Optional.of(quiz));
        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(guestUser);
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenReturn(unwrappedQuizAttempt);
        when(quizAttemptMapper.toDto(unwrappedQuizAttempt)).thenReturn(expected);

        //when
        QuizAttemptDto actual = quizAttemptService.createQuizAttempt(ID, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createQuizAttempt_InvalidQuizId_Throws404() {
        //given
        when(quizRepository.findById(ID)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> quizAttemptService.createQuizAttempt(ID, USERNAME));
    }

    @Test
    void createQuizAttempt_UserAndGuestUserPresent_Throws404() {
        //given
        Category category = createCategory();
        Question question = createQuestion(category);
        Quiz quiz = createQuiz(category, List.of(question));
        Optional<User> user = createUser();
        Optional<GuestUser> guestUser = createGuestUser();

        when(quizRepository.findById(ID)).thenReturn(Optional.of(quiz));
        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(guestUser);

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> quizAttemptService.createQuizAttempt(ID, USERNAME));
    }

    @Test
    void createQuizAttempt_UserAndGuestAreNull_Throws404() {
        //given
        Category category = createCategory();
        Question question = createQuestion(category);
        Quiz quiz = createQuiz(category, List.of(question));

        when(quizRepository.findById(ID)).thenReturn(Optional.of(quiz));
        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> quizAttemptService.createQuizAttempt(ID, USERNAME));
    }

    @Test
    void createQuizAttemptWithRandomQuestions_everythingValidForUser_ReturnsQuizAttemptDto() {
        //given
        QuizAttemptRandomRequestDto quizAttemptRandomRequestDto
                = createQuizAttemptRandomRequestDto();

        Category category = createCategory();
        Difficulty difficulty = Difficulty.EASY;
        Question question = createQuestion(category);
        List<Question> questions = new ArrayList<>();
        questions.add(question);
        questions.add(question);
        questions.add(question);
        questions.add(question);
        questions.add(question);
        questions.add(question);
        questions.add(question);
        questions.add(question);
        questions.add(question);
        questions.add(question);
        Quiz quiz = createQuiz(category, questions);
        Optional<User> user = createUser();
        User unwrappedUser = user.get();
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForUser(unwrappedUser, quiz);
        QuizAttempt unwrappedQuizAttempt = quizAttempt.get();
        QuizAttemptDto expected = createQuizAttemptDtoForUser(unwrappedUser, quiz);

        when(categoryRepository.findByName(quizAttemptRandomRequestDto.getCategory()))
                .thenReturn(Optional.of(category));
        when(questionRepository.findByDifficultyAndCategory(difficulty, category))
                .thenReturn(questions);
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);
        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenReturn(unwrappedQuizAttempt);
        when(quizAttemptMapper.toDto(unwrappedQuizAttempt)).thenReturn(expected);

        //when
        QuizAttemptDto actual = quizAttemptService
                .createQuizAttemptWithRandomQuestions(quizAttemptRandomRequestDto, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(10, quiz.getListOfQuestions().size());
    }

    @Test
    void createQuizAttemptWithRandomQuestions_everythingValidForGuestUser_ReturnsQuizAttemptDto() {
        //given
        QuizAttemptRandomRequestDto quizAttemptRandomRequestDto
                = createQuizAttemptRandomRequestDto();

        Category category = createCategory();
        Difficulty difficulty = Difficulty.EASY;
        Question question = createQuestion(category);
        List<Question> questions = List.of(question);
        Quiz quiz = createQuiz(category, questions);
        Optional<GuestUser> guestUser = createGuestUser();
        GuestUser unwrappedGuestUser = guestUser.get();
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForGuest(unwrappedGuestUser, quiz);
        QuizAttempt unwrappedQuizAttempt = quizAttempt.get();
        QuizAttemptDto expected = createQuizAttemptDtoForGuestUser(unwrappedGuestUser, quiz);

        when(categoryRepository.findByName(quizAttemptRandomRequestDto.getCategory()))
                .thenReturn(Optional.of(category));
        when(questionRepository.findByDifficultyAndCategory(difficulty, category))
                .thenReturn(questions);
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);
        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(guestUser);
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenReturn(unwrappedQuizAttempt);
        when(quizAttemptMapper.toDto(unwrappedQuizAttempt)).thenReturn(expected);

        //when
        QuizAttemptDto actual = quizAttemptService
                .createQuizAttemptWithRandomQuestions(quizAttemptRandomRequestDto, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createQuizAttemptWithRandomQuestions_InvalidCategoryName_Throws404() {
        //given
        QuizAttemptRandomRequestDto quizAttemptRandomRequestDto
                = createQuizAttemptRandomRequestDto();
        when(categoryRepository.findByName(quizAttemptRandomRequestDto.getCategory()))
                .thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> quizAttemptService.createQuizAttemptWithRandomQuestions(
                        quizAttemptRandomRequestDto, USERNAME));
    }

    @Test
    void createQuizAttemptWithRandomQuestions_InvalidDifficultyName_Throws404() {
        //given
        QuizAttemptRandomRequestDto quizAttemptRandomRequestDto
                = createQuizAttemptRandomRequestDto();
        quizAttemptRandomRequestDto.setDifficulty("123");
        Category category = createCategory();
        when(categoryRepository.findByName(quizAttemptRandomRequestDto.getCategory()))
                .thenReturn(Optional.of(category));

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> quizAttemptService.createQuizAttemptWithRandomQuestions(
                        quizAttemptRandomRequestDto, USERNAME));
    }

    @Test
    void createQuizAttemptWithRandomQuestions_UserAndGuestUserPresent_Throws404() {
        //given
        QuizAttemptRandomRequestDto quizAttemptRandomRequestDto
                = createQuizAttemptRandomRequestDto();

        Category category = createCategory();
        Difficulty difficulty = Difficulty.EASY;
        Question question = createQuestion(category);
        List<Question> questions = List.of(question);
        Quiz quiz = createQuiz(category, questions);
        Optional<GuestUser> guestUser = createGuestUser();
        Optional<User> user = createUser();

        when(categoryRepository.findByName(quizAttemptRandomRequestDto.getCategory()))
                .thenReturn(Optional.of(category));
        when(questionRepository.findByDifficultyAndCategory(difficulty, category))
                .thenReturn(questions);
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);
        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(guestUser);

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> quizAttemptService.createQuizAttemptWithRandomQuestions(
                        quizAttemptRandomRequestDto, USERNAME));
    }

    @Test
    void createQuizAttemptWithRandomQuestions_UserAndGuestUserNotPresent_Throws404() {
        //given
        QuizAttemptRandomRequestDto quizAttemptRandomRequestDto
                = createQuizAttemptRandomRequestDto();

        Category category = createCategory();
        Difficulty difficulty = Difficulty.EASY;
        Question question = createQuestion(category);
        List<Question> questions = List.of(question);
        Quiz quiz = createQuiz(category, questions);

        when(categoryRepository.findByName(quizAttemptRandomRequestDto.getCategory()))
                .thenReturn(Optional.of(category));
        when(questionRepository.findByDifficultyAndCategory(difficulty, category))
                .thenReturn(questions);
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);
        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> quizAttemptService.createQuizAttemptWithRandomQuestions(
                        quizAttemptRandomRequestDto, USERNAME));
    }

    @Test
    void getQuizAttempt_ValidIdAndUsernameForUser_ReturnsQuizAttemptDto() {
        //given
        Optional<User> user = createUser();
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> listOfQuestions = List.of(question);
        Quiz quiz = createQuiz(category, listOfQuestions);
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForUser(user.get(), quiz);
        QuizAttemptDto expected = createQuizAttemptDtoForUser(user.get(), quiz);

        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(quizAttemptMapper.toDto(any(QuizAttempt.class))).thenReturn(expected);

        //when
        QuizAttemptDto actual = quizAttemptService.getQuizAttempt(ID, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getQuizAttempt_ValidIdAndUsernameForGuestUser_ReturnsQuizAttemptDto() {
        //given
        Optional<GuestUser> guestUser = createGuestUser();
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> listOfQuestions = List.of(question);
        Quiz quiz = createQuiz(category, listOfQuestions);
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForGuest(guestUser.get(), quiz);
        QuizAttemptDto expected = createQuizAttemptDtoForGuestUser(guestUser.get(), quiz);

        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(guestUser);
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(quizAttemptMapper.toDto(any(QuizAttempt.class))).thenReturn(expected);

        //when
        QuizAttemptDto actual = quizAttemptService.getQuizAttempt(ID, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getQuizAttempt_InvalidId_ValidUsername_Throws404() {
        //given
        Optional<User> user = createUser();

        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());
        when(quizAttemptRepository.findById(ID)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> quizAttemptService.getQuizAttempt(ID, USERNAME));
    }

    @Test
    void getQuizAttempt_ValidIdUserAndGuestUserPresent_Throws403() {
        //given
        Optional<GuestUser> guestUser = createGuestUser();
        Optional<User> user = createUser();
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> listOfQuestions = List.of(question);
        Quiz quiz = createQuiz(category, listOfQuestions);
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForGuest(guestUser.get(), quiz);

        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(guestUser);
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);

        //when & then
        Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> quizAttemptService.getQuizAttempt(ID, USERNAME));
    }

    @Test
    void getQuizAttempt_ValidIdUserPresentButUserNull_Throws403() {
        //given
        Optional<User> user = createUser();
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> listOfQuestions = List.of(question);
        Quiz quiz = createQuiz(category, listOfQuestions);
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForUser(user.get(), quiz);
        quizAttempt.get().setUser(null);

        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);

        //when & then
        Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> quizAttemptService.getQuizAttempt(ID, USERNAME));
    }

    @Test
    void getQuizAttempt_ValidIdUserPresentButWrongUserId_Throws403() {
        //given
        Optional<User> user = createUser();
        Optional<User> user2 = createUser();
        user2.get().setId(2L);
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> listOfQuestions = List.of(question);
        Quiz quiz = createQuiz(category, listOfQuestions);
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForUser(user2.get(), quiz);

        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);

        //when & then
        Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> quizAttemptService.getQuizAttempt(ID, USERNAME));
    }

    @Test
    void getQuizAttempt_ValidIdGuestUserPresentButGuestUserNull_Throws403() {
        //given
        Optional<GuestUser> guestUser = createGuestUser();
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> listOfQuestions = List.of(question);
        Quiz quiz = createQuiz(category, listOfQuestions);
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForGuest(guestUser.get(), quiz);
        quizAttempt.get().setGuestUser(null);

        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(guestUser);
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);

        //when & then
        Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> quizAttemptService.getQuizAttempt(ID, USERNAME));
    }

    @Test
    void getQuizAttempt_ValidIdGuestUserPresentButWrongGuestId_Throws403() {
        //given
        Optional<GuestUser> guestUser = createGuestUser();
        Optional<GuestUser> guestUser2 = createGuestUser();
        guestUser2.get().setId(2L);
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> listOfQuestions = List.of(question);
        Quiz quiz = createQuiz(category, listOfQuestions);
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForGuest(guestUser2.get(), quiz);

        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(guestUser);
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);

        //when & then
        Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> quizAttemptService.getQuizAttempt(ID, USERNAME));
    }

    @Test
    void finishQuizAttempt_ValidIdAndUsernameForUser_ReturnsQuizAttemptDto() {
        //given
        Optional<User> user = createUser();
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> questions = List.of(question);
        Quiz quiz = createQuiz(category, questions);
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForUser(user.get(), quiz);
        Optional<GamesByCategoryResult> result
                = createGamesByCategoryResultForUser(category, user.get());
        QuizAttemptDto expected = createQuizAttemptDtoForUser(user.get(), quiz);

        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());
        when(gamesByCategoryResultRepository.findByCategoryIdAndUserId(
                category.getId(), user.get().getId())).thenReturn(result);
        when(quizAttemptMapper.toDto(quizAttempt.get())).thenReturn(expected);

        //when
        QuizAttemptDto actual = quizAttemptService.finishQuizAttempt(ID, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(Status.FINISHED, actual.getStatus());
        Assertions.assertEquals(1, user.get().getStreak());
        Assertions.assertEquals(1, user.get().getTotalGamesPlayed());
        Assertions.assertEquals(1, result.get().getGamesPlayed());
    }

    @Test
    void finishQuizAttempt_ValidIdAndUsernameForGuestUser_ReturnsQuizAttemptDto() {
        //given
        Optional<GuestUser> guestUser = createGuestUser();
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> questions = List.of(question);
        Quiz quiz = createQuiz(category, questions);
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForGuest(guestUser.get(), quiz);
        Optional<GamesByCategoryResult> result
                = createGamesByCategoryResultForGuestUser(category, guestUser.get());
        QuizAttemptDto expected = createQuizAttemptDtoForGuestUser(guestUser.get(), quiz);

        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(guestUser);
        when(gamesByCategoryResultRepository.findByCategoryIdAndGuestUserId(
                category.getId(), guestUser.get().getId())).thenReturn(result);
        when(quizAttemptMapper.toDto(any(QuizAttempt.class))).thenReturn(expected);

        //when
        QuizAttemptDto actual = quizAttemptService.finishQuizAttempt(ID, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(Status.FINISHED, actual.getStatus());
        Assertions.assertEquals(1, guestUser.get().getTotalGamesPlayed());
        Assertions.assertEquals(2, result.get().getGamesPlayed());
    }

    @Test
    void finishQuizAttempt_InvalidId_Throws404() {
        //given
        when(quizAttemptRepository.findById(ID)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> quizAttemptService.finishQuizAttempt(ID, USERNAME));
    }

    @Test
    void finishQuizAttempt_InvalidQuizAttemptStatus_Throws404() {
        //given
        Optional<GuestUser> guestUser = createGuestUser();
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> questions = List.of(question);
        Quiz quiz = createQuiz(category, questions);
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForGuest(guestUser.get(), quiz);
        quizAttempt.get().setStatus(Status.FINISHED);

        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);

        //when & then
        Assertions.assertThrows(InvalidQuizAttemptStateException.class,
                () -> quizAttemptService.finishQuizAttempt(ID, USERNAME));
    }

    @Test
    void finishQuizAttempt_BothUserAndGuestUserArePresent_Throws403() {
        //given
        Optional<GuestUser> guestUser = createGuestUser();
        Optional<User> user = createUser();
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> questions = List.of(question);
        Quiz quiz = createQuiz(category, questions);
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForGuest(guestUser.get(), quiz);

        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(guestUser);

        //when & then
        Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> quizAttemptService.finishQuizAttempt(ID, USERNAME));
    }

    @Test
    void finishQuizAttempt_UserIsPresentButIdInQuizAttemptWrong_Throws403() {
        //given
        Optional<User> user = createUser();
        Optional<User> user2 = createUser();
        user2.get().setId(2L);
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> questions = List.of(question);
        Quiz quiz = createQuiz(category, questions);
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForUser(user2.get(), quiz);

        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> quizAttemptService.finishQuizAttempt(ID, USERNAME));
    }

    @Test
    void finishQuizAttempt_GuestUserIsPresentButIdInQuizAttemptWrong_Throws403() {
        //given
        Optional<GuestUser> user = createGuestUser();
        Optional<GuestUser> user2 = createGuestUser();
        user2.get().setId(2L);
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> questions = List.of(question);
        Quiz quiz = createQuiz(category, questions);
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForGuest(user2.get(), quiz);

        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(user);

        //when & then
        Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> quizAttemptService.finishQuizAttempt(ID, USERNAME));
    }

    @Test
    void finishQuizAttempt_GamesByCategoryResultIsEmptyForUser_ReturnsQuizAttemptDto() {
        //given
        Optional<User> user = createUser();
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> questions = List.of(question);
        Quiz quiz = createQuiz(category, questions);
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForUser(user.get(), quiz);
        QuizAttemptDto expected = createQuizAttemptDtoForUser(user.get(), quiz);
        Optional<GamesByCategoryResult> gamesByCategoryResult
                = createGamesByCategoryResultForUser(category, user.get());
        gamesByCategoryResult.get().setCategoryScore(0);
        gamesByCategoryResult.get().setGamesPlayed(0);
        gamesByCategoryResult.get().setCategory(quiz.getCategory());
        gamesByCategoryResult.get().setGuestUser(null);
        gamesByCategoryResult.get().setUser(user.get());

        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());
        when(gamesByCategoryResultRepository.save(any(GamesByCategoryResult.class)))
                .thenReturn(gamesByCategoryResult.get());
        when(quizAttemptMapper.toDto(quizAttempt.get())).thenReturn(expected);

        //when
        QuizAttemptDto actual = quizAttemptService.finishQuizAttempt(ID, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void finishQuizAttempt_GamesByCategoryResultIsEmptyForGuestUser_ReturnsQuizAttemptDto() {
        //given
        Optional<GuestUser> user = createGuestUser();
        Category category = createCategory();
        Question question = createQuestion(category);
        List<Question> questions = List.of(question);
        Quiz quiz = createQuiz(category, questions);
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForGuest(user.get(), quiz);
        QuizAttemptDto expected = createQuizAttemptDtoForGuestUser(user.get(), quiz);
        Optional<GamesByCategoryResult> gamesByCategoryResult
                = createGamesByCategoryResultForGuestUser(category, user.get());
        gamesByCategoryResult.get().setCategoryScore(0);
        gamesByCategoryResult.get().setGamesPlayed(0);
        gamesByCategoryResult.get().setCategory(quiz.getCategory());
        gamesByCategoryResult.get().setGuestUser(user.get());
        gamesByCategoryResult.get().setUser(null);

        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(user);
        when(gamesByCategoryResultRepository.save(any(GamesByCategoryResult.class)))
                .thenReturn(gamesByCategoryResult.get());
        when(quizAttemptMapper.toDto(quizAttempt.get())).thenReturn(expected);

        //when
        QuizAttemptDto actual = quizAttemptService.finishQuizAttempt(ID, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void finishQuizAttempt_UserPlayedYesterday_IncreasesStreak() {
        //given
        Optional<User> user = createUser();
        user.get().setStreak(3);
        user.get().setLastTimePlayed(LocalDate.now().minusDays(1));

        Category category = createCategory();
        Question question = createQuestion(category);
        Quiz quiz = createQuiz(category, List.of(question));
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForUser(user.get(), quiz);
        Optional<GamesByCategoryResult> result =
                createGamesByCategoryResultForUser(category, user.get());
        QuizAttemptDto expected = createQuizAttemptDtoForUser(user.get(), quiz);

        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());
        when(gamesByCategoryResultRepository.findByCategoryIdAndUserId(
                category.getId(), user.get().getId())).thenReturn(result);
        when(quizAttemptMapper.toDto(quizAttempt.get())).thenReturn(expected);

        //when
        QuizAttemptDto actual = quizAttemptService.finishQuizAttempt(ID, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(4, user.get().getStreak());
        Assertions.assertEquals(LocalDate.now(), user.get().getLastTimePlayed());
    }

    @Test
    void finishQuizAttempt_UserDidNotPlayForSeveralDays_ResetsStreak() {
        //given
        Optional<User> user = createUser();
        user.get().setStreak(5);
        user.get().setLastTimePlayed(LocalDate.now().minusDays(3));

        Category category = createCategory();
        Question question = createQuestion(category);
        Quiz quiz = createQuiz(category, List.of(question));
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForUser(user.get(), quiz);
        Optional<GamesByCategoryResult> result =
                createGamesByCategoryResultForUser(category, user.get());
        QuizAttemptDto expected = createQuizAttemptDtoForUser(user.get(), quiz);

        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());
        when(gamesByCategoryResultRepository.findByCategoryIdAndUserId(
                category.getId(), user.get().getId())).thenReturn(result);
        when(quizAttemptMapper.toDto(quizAttempt.get())).thenReturn(expected);

        //when
        QuizAttemptDto actual = quizAttemptService.finishQuizAttempt(ID, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(1, user.get().getStreak());
        Assertions.assertEquals(LocalDate.now(), user.get().getLastTimePlayed());
    }

    @Test
    void finishQuizAttempt_UserPlayedToday_KeepsCurrentStreak() {
        //given
        Optional<User> user = createUser();
        user.get().setStreak(5);
        user.get().setLastTimePlayed(LocalDate.now());

        Category category = createCategory();
        Question question = createQuestion(category);
        Quiz quiz = createQuiz(category, List.of(question));
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForUser(user.get(), quiz);
        Optional<GamesByCategoryResult> result =
                createGamesByCategoryResultForUser(category, user.get());
        QuizAttemptDto expected = createQuizAttemptDtoForUser(user.get(), quiz);

        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());
        when(gamesByCategoryResultRepository.findByCategoryIdAndUserId(
                category.getId(), user.get().getId())).thenReturn(result);
        when(quizAttemptMapper.toDto(quizAttempt.get())).thenReturn(expected);

        //when
        QuizAttemptDto actual = quizAttemptService.finishQuizAttempt(ID, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(5, user.get().getStreak());
        Assertions.assertEquals(LocalDate.now(), user.get().getLastTimePlayed());
    }

    private QuizAttemptRandomRequestDto createQuizAttemptRandomRequestDto() {
        QuizAttemptRandomRequestDto random = new QuizAttemptRandomRequestDto();
        random.setCategory("Geography");
        random.setQuizTitle("Basic title");
        random.setDifficulty("EASY");
        return random;
    }

    private QuizAttemptDto createQuizAttemptDtoForGuestUser(GuestUser guestUser, Quiz quiz) {
        QuizAttemptDto quizAttemptDto = new QuizAttemptDto();
        quizAttemptDto.setId(ID);
        quizAttemptDto.setStartedAt(LocalDateTime.now());
        quizAttemptDto.setUserId(null);
        quizAttemptDto.setStatus(Status.FINISHED);
        quizAttemptDto.setGuestUserId(guestUser.getId());
        quizAttemptDto.setQuizId(quiz.getId());
        return quizAttemptDto;
    }

    private QuizAttemptDto createQuizAttemptDtoForUser(User user, Quiz quiz) {
        QuizAttemptDto quizAttemptDto = new QuizAttemptDto();
        quizAttemptDto.setId(ID);
        quizAttemptDto.setStartedAt(LocalDateTime.now());
        quizAttemptDto.setUserId(user.getId());
        quizAttemptDto.setGuestUserId(null);
        quizAttemptDto.setStatus(Status.FINISHED);
        quizAttemptDto.setQuizId(quiz.getId());
        return quizAttemptDto;
    }

    private Quiz createQuiz(Category category, List<Question> questions) {
        Quiz quiz = new Quiz();
        quiz.setId(ID);
        quiz.setTitle("Title");
        quiz.setCategory(category);
        quiz.setDifficulty(Difficulty.EASY);
        quiz.setListOfQuestions(questions);
        return quiz;
    }

    private Category createCategory() {
        Category category = new Category();
        category.setName("Geography");
        category.setId(ID);
        return category;
    }

    private Optional<QuizAttempt> createQuizAttemptForUser(User user, Quiz quiz) {
        QuizAttempt quizAttempt = new QuizAttempt();
        quizAttempt.setUser(user);
        quizAttempt.setGuestUser(null);
        quizAttempt.setStatus(Status.IN_PROGRESS);
        quizAttempt.setId(ID);
        quizAttempt.setStartedAt(LocalDateTime.now());
        quizAttempt.setQuiz(quiz);
        return Optional.of(quizAttempt);
    }

    private Optional<QuizAttempt> createQuizAttemptForGuest(GuestUser guestUser, Quiz quiz) {
        QuizAttempt quizAttempt = new QuizAttempt();
        quizAttempt.setGuestUser(guestUser);
        quizAttempt.setUser(null);
        quizAttempt.setStatus(Status.IN_PROGRESS);
        quizAttempt.setId(ID);
        quizAttempt.setStartedAt(LocalDateTime.now());
        quizAttempt.setQuiz(quiz);
        return Optional.of(quizAttempt);
    }

    private Question createQuestion(Category category) {
        Question question = new Question();
        question.setCategory(category);
        question.setId(ID);
        question.setCorrectAnswer(true);
        question.setText("Question text");
        question.setDifficulty(Difficulty.EASY);
        question.setAnswerCounter(4);
        question.setFeedback("Feedback for question");
        return question;
    }

    private Optional<User> createUser() {
        User user = new User();
        user.setId(ID);
        user.setLevel(Level.LEVEL_1);
        user.setStreak(0);
        user.setGuestUser(null);
        user.setCreatedAt(LocalDateTime.now());
        user.setScore(490);
        user.setAvatar(Avatar.AVATAR_1);
        user.setPassword("password");
        user.setNickname("nickname");
        user.setEmail("email@email.com");
        user.setRoles(Collections.singletonList(Role.USER));
        return Optional.of(user);
    }

    private Optional<GuestUser> createGuestUser() {
        GuestUser guestUser = new GuestUser();
        guestUser.setRole(Role.GUEST);
        guestUser.setCreatedAt(LocalDateTime.now());
        guestUser.setAvatar(Avatar.AVATAR_1);
        guestUser.setNickname("guest");
        guestUser.setId(ID);
        guestUser.setScore(0);
        guestUser.setTotalGamesPlayed(0);
        return Optional.of(guestUser);
    }

    private Optional<GamesByCategoryResult> createGamesByCategoryResultForUser(
            Category category, User user) {
        GamesByCategoryResult gamesByCategoryResult = new GamesByCategoryResult();
        gamesByCategoryResult.setCategoryScore(0);
        gamesByCategoryResult.setCategory(category);
        gamesByCategoryResult.setGamesPlayed(0);
        gamesByCategoryResult.setId(ID);
        gamesByCategoryResult.setUser(user);
        gamesByCategoryResult.setGuestUser(null);
        return Optional.of(gamesByCategoryResult);
    }

    private Optional<GamesByCategoryResult> createGamesByCategoryResultForGuestUser(
            Category category, GuestUser user) {
        GamesByCategoryResult gamesByCategoryResult = new GamesByCategoryResult();
        gamesByCategoryResult.setCategoryScore(10);
        gamesByCategoryResult.setCategory(category);
        gamesByCategoryResult.setGamesPlayed(1);
        gamesByCategoryResult.setId(ID);
        gamesByCategoryResult.setUser(null);
        gamesByCategoryResult.setGuestUser(user);
        return Optional.of(gamesByCategoryResult);
    }
}
