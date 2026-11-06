package quizz.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import quizz.dto.answerattempt.AnswerAttemptDto;
import quizz.dto.answerattempt.AnswerAttemptRequestDto;
import quizz.exception.EntityNotFoundException;
import quizz.exception.InvalidQuizAttemptStateException;
import quizz.exception.QuestionAlreadyAnsweredException;
import quizz.exception.UnauthorizedAccessException;
import quizz.mapper.AnswerAttemptMapper;
import quizz.model.Answer;
import quizz.model.AnswerAttempt;
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
import quizz.repository.AnswerAttemptRepository;
import quizz.repository.AnswerRepository;
import quizz.repository.GamesByCategoryResultRepository;
import quizz.repository.GuestUserRepository;
import quizz.repository.QuizAttemptRepository;
import quizz.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AnswerAttemptServiceImplTest {

    private static final String USERNAME = "email@email.com";
    private static final Long ID = 1L;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GuestUserRepository guestUserRepository;

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private AnswerAttemptRepository answerAttemptRepository;

    @Mock
    private GamesByCategoryResultRepository gamesByCategoryResultRepository;

    @Mock
    private AnswerAttemptMapper answerAttemptMapper;

    @InjectMocks
    private AnswerAttemptServiceImpl answerAttemptService;

    @Test
    void createAnswerAttempt_ValidRequestForUser_ReturnsAnswerAttemptDto() {
        //given
        AnswerAttemptRequestDto answerAttemptRequestDto = createAnswerAttemptRequestDto();
        Optional<User> user = createUser();
        User unwrappedUser = user.get();
        AnswerAttemptDto expected = createAnswerAttemptDto(List.of(ID));
        Category category = createCategory();
        Question question = createQuestion(category);
        Optional<Answer> answer = createAnswer(question);
        Answer unwrappedAnswer = answer.get();
        Quiz quiz = createQuiz(category, List.of(question));
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForUser(unwrappedUser, quiz);
        QuizAttempt unwrappedQuizAttempt = quizAttempt.get();

        AnswerAttempt answerAttempt = createAnswerAttempt(unwrappedQuizAttempt, unwrappedAnswer);
        Optional<GamesByCategoryResult> gamesByCategoryResult = createGamesByCategoryResultForUser(
                category, unwrappedUser);

        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(answerRepository.findById(ID)).thenReturn(answer);
        when(answerAttemptRepository.existsByQuizAttemptIdAndAnswerQuestionId(ID, ID))
                .thenReturn(false);
        when(answerAttemptRepository.save(any(AnswerAttempt.class))).thenReturn(answerAttempt);
        when(gamesByCategoryResultRepository.findByCategoryIdAndUserId(ID, ID))
                .thenReturn(gamesByCategoryResult);
        when(answerRepository.findByQuestionIdAndCorrectTrue(ID))
                .thenReturn(List.of(unwrappedAnswer));
        when(answerAttemptMapper.toDto(any(AnswerAttempt.class))).thenReturn(expected);

        //when
        AnswerAttemptDto actual = answerAttemptService.createAnswerAttempt
                (answerAttemptRequestDto, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createAnswerAttempt_ValidRequestForGuestUser_ReturnsAnswerAttemptDto() {
        //given
        AnswerAttemptRequestDto answerAttemptRequestDto = createAnswerAttemptRequestDto();
        Optional<GuestUser> guestUser = createGuestUser();
        GuestUser unwrappedGuestUser = guestUser.get();
        AnswerAttemptDto expected = createAnswerAttemptDto(List.of(ID));
        Category category = createCategory();
        Question question = createQuestion(category);
        Optional<Answer> answer = createAnswer(question);
        Answer unwrappedAnswer = answer.get();
        Quiz quiz = createQuiz(category, List.of(question));
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForGuest(unwrappedGuestUser, quiz);
        QuizAttempt unwrappedQuizAttempt = quizAttempt.get();

        AnswerAttempt answerAttempt = createAnswerAttempt(unwrappedQuizAttempt, unwrappedAnswer);
        Optional<GamesByCategoryResult> gamesByCategoryResult =
                createGamesByCategoryResultForGuestUser(category, unwrappedGuestUser);

        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(guestUser);
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(answerRepository.findById(ID)).thenReturn(answer);
        when(answerAttemptRepository.existsByQuizAttemptIdAndAnswerQuestionId(ID, ID))
                .thenReturn(false);
        when(answerAttemptRepository.save(any(AnswerAttempt.class))).thenReturn(answerAttempt);
        when(gamesByCategoryResultRepository.findByCategoryIdAndGuestUserId(ID, ID))
                .thenReturn(gamesByCategoryResult);
        when(answerRepository.findByQuestionIdAndCorrectTrue(ID))
                .thenReturn(List.of(unwrappedAnswer));
        when(answerAttemptMapper.toDto(any(AnswerAttempt.class))).thenReturn(expected);

        //when
        AnswerAttemptDto actual = answerAttemptService.createAnswerAttempt
                (answerAttemptRequestDto, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createAnswerAttempt_InvalidQuizAttempt_Throws404() {
        //given
        Optional<User> user = createUser();
        AnswerAttemptRequestDto answerAttemptRequestDto = createAnswerAttemptRequestDto();

        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());
        when(quizAttemptRepository.findById(ID)).thenReturn(Optional.empty());

        //when && then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> answerAttemptService.createAnswerAttempt(answerAttemptRequestDto, USERNAME));
    }

    @Test
    void createAnswerAttempt_UserAndGuestUserFound_Throws403() {
        //given
        Optional<User> user = createUser();
        Optional<GuestUser> guestUser = createGuestUser();
        AnswerAttemptRequestDto answerAttemptRequestDto = createAnswerAttemptRequestDto();
        Quiz quiz = createQuiz(createCategory(), List.of(createQuestion(createCategory())));
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForUser(user.get(),quiz);

        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(guestUser);
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);

        //when & then
        Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> answerAttemptService.createAnswerAttempt(answerAttemptRequestDto, USERNAME));
    }

    @Test
    void createAnswerAttempt_NoUserFoundInQuizAttempt_Throws403() {
        //given
        Optional<User> user = createUser();
        AnswerAttemptRequestDto answerAttemptRequestDto = createAnswerAttemptRequestDto();
        Quiz quiz = createQuiz(createCategory(), List.of(createQuestion(createCategory())));
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForUser(user.get(),quiz);
        quizAttempt.get().setUser(null);

        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);

        //when & then
        Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> answerAttemptService.createAnswerAttempt(answerAttemptRequestDto, USERNAME));
    }

    @Test
    void createAnswerAttempt_NoGuestUserFoundInQuizAttempt_Throws403() {
        //given
        Optional<GuestUser> user = createGuestUser();
        AnswerAttemptRequestDto answerAttemptRequestDto = createAnswerAttemptRequestDto();
        Quiz quiz = createQuiz(createCategory(), List.of(createQuestion(createCategory())));
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForGuest(user.get(),quiz);
        quizAttempt.get().setGuestUser(null);

        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(user);
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);

        //when & then
        Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> answerAttemptService.createAnswerAttempt(answerAttemptRequestDto, USERNAME));
    }

    @Test
    void createAnswerAttempt_InvalidQuizAttemptStatus_Throws401() {
        //given
        Optional<GuestUser> user = createGuestUser();
        AnswerAttemptRequestDto answerAttemptRequestDto = createAnswerAttemptRequestDto();
        Quiz quiz = createQuiz(createCategory(), List.of(createQuestion(createCategory())));
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForGuest(user.get(),quiz);
        quizAttempt.get().setStatus(Status.FINISHED);

        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(user);
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);

        //when & then
        Assertions.assertThrows(InvalidQuizAttemptStateException.class,
                () -> answerAttemptService.createAnswerAttempt(answerAttemptRequestDto, USERNAME));
    }

    @Test
    void createAnswerAttempt_InvalidAnswerId_Throws404() {
        //given
        Optional<GuestUser> user = createGuestUser();
        AnswerAttemptRequestDto answerAttemptRequestDto = createAnswerAttemptRequestDto();
        Category category = createCategory();
        Question question = createQuestion(category);
        Quiz quiz = createQuiz(category, List.of(question));
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForGuest(user.get(),quiz);

        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(user);
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> answerAttemptService.createAnswerAttempt(answerAttemptRequestDto, USERNAME));
    }

    @Test
    void createAnswerAttempt_AnswerDoesntBelongToAnyQuestion_Throws404() {
        //given
        Optional<GuestUser> user = createGuestUser();
        AnswerAttemptRequestDto answerAttemptRequestDto = createAnswerAttemptRequestDto();
        Category category = createCategory();
        Question question = createQuestion(category);
        Quiz quiz = createQuiz(category, List.of());
        Optional<Answer> answer = createAnswer(question);
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForGuest(user.get(),quiz);


        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(user);
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(answerRepository.findById(ID)).thenReturn(answer);

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> answerAttemptService.createAnswerAttempt(answerAttemptRequestDto, USERNAME));
    }

    @Test
    void createAnswerAttempt_QuestionAlreadyAnswered_Throws409() {
        //given
        Optional<GuestUser> user = createGuestUser();
        AnswerAttemptRequestDto answerAttemptRequestDto = createAnswerAttemptRequestDto();
        Category category = createCategory();
        Question question = createQuestion(category);
        Quiz quiz = createQuiz(category, List.of(question));
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForGuest(user.get(),quiz);
        Optional<Answer> answer = createAnswer(question);

        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(user);
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(answerRepository.findById(ID)).thenReturn(answer);
        when(answerAttemptRepository.existsByQuizAttemptIdAndAnswerQuestionId(ID, ID))
                .thenReturn(true);

        //when & then
        Assertions.assertThrows(QuestionAlreadyAnsweredException.class,
                () -> answerAttemptService.createAnswerAttempt(answerAttemptRequestDto, USERNAME));
    }

    @Test
    void createAnswerAttempt_InvalidCategoryIdAndUserId_ReturnsNewGamesByCategoryResult() {
        //given
        Optional<User> user = createUser();
        AnswerAttemptRequestDto answerAttemptRequestDto = createAnswerAttemptRequestDto();
        Category category = createCategory();
        Question question = createQuestion(category);
        Quiz quiz = createQuiz(category, List.of(question));
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForUser(user.get(),quiz);
        Optional<Answer> answer = createAnswer(question);
        Answer unwrappedAnswer = answer.get();
        AnswerAttempt answerAttempt = createAnswerAttempt(quizAttempt.get(), answer.get());
        AnswerAttemptDto expected = createAnswerAttemptDto(List.of());
        Optional<GamesByCategoryResult> gamesByCategoryResult = createGamesByCategoryResultForUser(
                category, user.get());

        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(answerRepository.findById(ID)).thenReturn(answer);
        when(answerAttemptRepository.existsByQuizAttemptIdAndAnswerQuestionId(ID, ID))
                .thenReturn(false);
        when(answerAttemptRepository.save(any(AnswerAttempt.class))).thenReturn(answerAttempt);
        when(gamesByCategoryResultRepository.findByCategoryIdAndUserId(ID, ID))
                .thenReturn(Optional.empty());
        when(gamesByCategoryResultRepository.save(any(GamesByCategoryResult.class)))
                .thenReturn(gamesByCategoryResult.get());
        when(answerRepository.findByQuestionIdAndCorrectTrue(ID))
                .thenReturn(List.of(unwrappedAnswer));
        when(answerAttemptMapper.toDto(any(AnswerAttempt.class))).thenReturn(expected);

        //when
        AnswerAttemptDto actual = answerAttemptService
                .createAnswerAttempt(answerAttemptRequestDto, USERNAME);

        // then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createAnswerAttempt_InvalidCategoryIdAndGuestUserId_ReturnsNewGamesByCategoryResult() {
        //given
        Optional<GuestUser> user = createGuestUser();
        AnswerAttemptRequestDto answerAttemptRequestDto = createAnswerAttemptRequestDto();
        Category category = createCategory();
        Question question = createQuestion(category);
        Quiz quiz = createQuiz(category, List.of(question));
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForGuest(user.get(),quiz);
        Optional<Answer> answer = createAnswer(question);
        Answer unwrappedAnswer = answer.get();
        AnswerAttempt answerAttempt = createAnswerAttempt(quizAttempt.get(), answer.get());
        AnswerAttemptDto expected = createAnswerAttemptDto(List.of());
        Optional<GamesByCategoryResult> gamesByCategoryResult =
                createGamesByCategoryResultForGuestUser(category, user.get());

        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(user);
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(answerRepository.findById(ID)).thenReturn(answer);
        when(answerAttemptRepository.existsByQuizAttemptIdAndAnswerQuestionId(ID, ID))
                .thenReturn(false);
        when(answerAttemptRepository.save(any(AnswerAttempt.class))).thenReturn(answerAttempt);
        when(gamesByCategoryResultRepository.findByCategoryIdAndGuestUserId(ID, ID))
                .thenReturn(Optional.empty());
        when(gamesByCategoryResultRepository.save(any(GamesByCategoryResult.class)))
                .thenReturn(gamesByCategoryResult.get());
        when(answerRepository.findByQuestionIdAndCorrectTrue(ID))
                .thenReturn(List.of(unwrappedAnswer));
        when(answerAttemptMapper.toDto(any(AnswerAttempt.class))).thenReturn(expected);

        //when
        AnswerAttemptDto actual = answerAttemptService
                .createAnswerAttempt(answerAttemptRequestDto, USERNAME);

        // then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createAnswerAttempt_ValidTotalScoreAndCategoryScoreAndLevelForUser_ReturnsCorrectScore() {
        //given
        AnswerAttemptRequestDto answerAttemptRequestDto = createAnswerAttemptRequestDto();
        Optional<User> user = createUser();
        User unwrappedUser = user.get();
        AnswerAttemptDto expected = createAnswerAttemptDto(List.of(ID));
        Category category = createCategory();
        Question question = createQuestion(category);
        Optional<Answer> answer = createAnswer(question);
        Answer unwrappedAnswer = answer.get();
        Quiz quiz = createQuiz(category, List.of(question));
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForUser(unwrappedUser, quiz);
        QuizAttempt unwrappedQuizAttempt = quizAttempt.get();

        AnswerAttempt answerAttempt = createAnswerAttempt(unwrappedQuizAttempt, unwrappedAnswer);
        Optional<GamesByCategoryResult> gamesByCategoryResult = createGamesByCategoryResultForUser(
                category, unwrappedUser);

        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(answerRepository.findById(ID)).thenReturn(answer);
        when(answerAttemptRepository.existsByQuizAttemptIdAndAnswerQuestionId(ID, ID))
                .thenReturn(false);
        when(answerAttemptRepository.save(any(AnswerAttempt.class))).thenReturn(answerAttempt);
        when(gamesByCategoryResultRepository.findByCategoryIdAndUserId(ID, ID))
                .thenReturn(gamesByCategoryResult);
        when(answerRepository.findByQuestionIdAndCorrectTrue(ID))
                .thenReturn(List.of(unwrappedAnswer));
        when(answerAttemptMapper.toDto(any(AnswerAttempt.class))).thenReturn(expected);

        //when
        AnswerAttemptDto actual = answerAttemptService.createAnswerAttempt
                (answerAttemptRequestDto, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(500, unwrappedUser.getScore());
        Assertions.assertEquals(Level.LEVEL_2, unwrappedUser.getLevel());
        Assertions.assertEquals(20, gamesByCategoryResult.get().getCategoryScore());
    }

    @Test
    void createAnswerAttempt_ValidTotalScoreAndCategoryScoreForGuest_ReturnsCorrectScore() {
        //given
        AnswerAttemptRequestDto answerAttemptRequestDto = createAnswerAttemptRequestDto();
        Optional<GuestUser> user = createGuestUser();
        GuestUser unwrappedUser = user.get();
        AnswerAttemptDto expected = createAnswerAttemptDto(List.of(ID));
        Category category = createCategory();
        Question question = createQuestion(category);
        Optional<Answer> answer = createAnswer(question);
        Answer unwrappedAnswer = answer.get();
        Quiz quiz = createQuiz(category, List.of(question));
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForGuest(unwrappedUser, quiz);
        QuizAttempt unwrappedQuizAttempt = quizAttempt.get();

        AnswerAttempt answerAttempt = createAnswerAttempt(unwrappedQuizAttempt, unwrappedAnswer);
        Optional<GamesByCategoryResult> gamesByCategoryResult =
                createGamesByCategoryResultForGuestUser(category, unwrappedUser);

        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(user);
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(answerRepository.findById(ID)).thenReturn(answer);
        when(answerAttemptRepository.existsByQuizAttemptIdAndAnswerQuestionId(ID, ID))
                .thenReturn(false);
        when(answerAttemptRepository.save(any(AnswerAttempt.class))).thenReturn(answerAttempt);
        when(gamesByCategoryResultRepository.findByCategoryIdAndGuestUserId(ID, ID))
                .thenReturn(gamesByCategoryResult);
        when(answerRepository.findByQuestionIdAndCorrectTrue(ID))
                .thenReturn(List.of(unwrappedAnswer));
        when(answerAttemptMapper.toDto(any(AnswerAttempt.class))).thenReturn(expected);

        //when
        AnswerAttemptDto actual = answerAttemptService.createAnswerAttempt
                (answerAttemptRequestDto, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(10, unwrappedUser.getScore());
        Assertions.assertEquals(20, gamesByCategoryResult.get().getCategoryScore());
    }

    @Test
    void createAnswerAttempt_IncorrectAnswerForGuestUser_GivesNoPoints() {
        //given
        AnswerAttemptRequestDto answerAttemptRequestDto = createAnswerAttemptRequestDto();
        Optional<GuestUser> user = createGuestUser();
        GuestUser unwrappedUser = user.get();
        AnswerAttemptDto expected = createAnswerAttemptDto(List.of(ID));
        Category category = createCategory();
        Question question = createQuestion(category);
        question.setCorrectAnswer(false);
        Optional<Answer> answer = createAnswer(question);
        Answer unwrappedAnswer = answer.get();
        unwrappedAnswer.setCorrect(false);
        Quiz quiz = createQuiz(category, List.of(question));
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForGuest(unwrappedUser, quiz);
        QuizAttempt unwrappedQuizAttempt = quizAttempt.get();

        AnswerAttempt answerAttempt = createAnswerAttempt(unwrappedQuizAttempt, unwrappedAnswer);
        Optional<GamesByCategoryResult> gamesByCategoryResult =
                createGamesByCategoryResultForGuestUser(category, unwrappedUser);

        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(user);
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(answerRepository.findById(ID)).thenReturn(answer);
        when(answerAttemptRepository.existsByQuizAttemptIdAndAnswerQuestionId(ID, ID))
                .thenReturn(false);
        when(answerAttemptRepository.save(any(AnswerAttempt.class))).thenReturn(answerAttempt);
        when(gamesByCategoryResultRepository.findByCategoryIdAndGuestUserId(ID, ID))
                .thenReturn(gamesByCategoryResult);
        when(answerRepository.findByQuestionIdAndCorrectTrue(ID))
                .thenReturn(List.of(unwrappedAnswer));
        when(answerAttemptMapper.toDto(any(AnswerAttempt.class))).thenReturn(expected);

        //when
        AnswerAttemptDto actual = answerAttemptService.createAnswerAttempt
                (answerAttemptRequestDto, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(0, unwrappedUser.getScore());
        Assertions.assertEquals(10, gamesByCategoryResult.get().getCategoryScore());
    }

    @Test
    void createAnswerAttempt_IncorrectAnswerForUser_GivesNoPoints() {
        //given
        AnswerAttemptRequestDto answerAttemptRequestDto = createAnswerAttemptRequestDto();
        Optional<User> user = createUser();
        User unwrappedUser = user.get();
        AnswerAttemptDto expected = createAnswerAttemptDto(List.of(ID));
        Category category = createCategory();
        Question question = createQuestion(category);
        question.setCorrectAnswer(false);
        Optional<Answer> answer = createAnswer(question);
        Answer unwrappedAnswer = answer.get();
        unwrappedAnswer.setCorrect(false);
        Quiz quiz = createQuiz(category, List.of(question));
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForUser(unwrappedUser, quiz);
        QuizAttempt unwrappedQuizAttempt = quizAttempt.get();

        AnswerAttempt answerAttempt = createAnswerAttempt(unwrappedQuizAttempt, unwrappedAnswer);
        Optional<GamesByCategoryResult> gamesByCategoryResult =
                createGamesByCategoryResultForUser(category, unwrappedUser);

        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());
        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(answerRepository.findById(ID)).thenReturn(answer);
        when(answerAttemptRepository.existsByQuizAttemptIdAndAnswerQuestionId(ID, ID))
                .thenReturn(false);
        when(answerAttemptRepository.save(any(AnswerAttempt.class))).thenReturn(answerAttempt);
        when(gamesByCategoryResultRepository.findByCategoryIdAndUserId(ID, ID))
                .thenReturn(gamesByCategoryResult);
        when(answerRepository.findByQuestionIdAndCorrectTrue(ID))
                .thenReturn(List.of(unwrappedAnswer));
        when(answerAttemptMapper.toDto(any(AnswerAttempt.class))).thenReturn(expected);

        //when
        AnswerAttemptDto actual = answerAttemptService.createAnswerAttempt
                (answerAttemptRequestDto, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(490, unwrappedUser.getScore());
        Assertions.assertEquals(10, gamesByCategoryResult.get().getCategoryScore());
    }

    @Test
    void getAnswerAttempt_ValidIDAndUsername_ReturnsListOfAnswerAttemptDTO() {
        //given
        Optional<User> user = createUser();
        Category category = createCategory();
        Question question = createQuestion(category);
        Quiz quiz = createQuiz(category, List.of(question));
        Optional<Answer> answer = createAnswer(question);
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForUser(user.get(), quiz);
        AnswerAttempt answerAttempt = createAnswerAttempt(quizAttempt.get(), answer.get());
        AnswerAttemptDto answerAttemptDto = createAnswerAttemptDto(List.of(ID));
        List<AnswerAttemptDto> expected = List.of(answerAttemptDto);

        when(quizAttemptRepository.findById(ID)).thenReturn(quizAttempt);
        when(userRepository.findByEmail(USERNAME)).thenReturn(user);
        when(guestUserRepository.findByNickname(USERNAME)).thenReturn(Optional.empty());
        when(answerAttemptRepository.findByQuizAttemptId(ID)).thenReturn(List.of(answerAttempt));
        when(answerAttemptMapper.toDto(any(AnswerAttempt.class))).thenReturn(answerAttemptDto);

        //when
        List<AnswerAttemptDto> actual
                = answerAttemptService.getAnswerAttemptsByQuizAttemptId(ID, USERNAME);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getAnswerAttempt_InvalidId_Throws404() {
        //given & when
        when(quizAttemptRepository.findById(ID)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> answerAttemptService.getAnswerAttemptsByQuizAttemptId(ID, USERNAME));
    }

    private AnswerAttemptRequestDto createAnswerAttemptRequestDto() {
        AnswerAttemptRequestDto answerAttemptRequestDto = new AnswerAttemptRequestDto();
        answerAttemptRequestDto.setAnswerId(ID);
        answerAttemptRequestDto.setQuizAttemptId(ID);
        return answerAttemptRequestDto;
    }

    private AnswerAttemptDto createAnswerAttemptDto(List<Long> answerIds) {
        AnswerAttemptDto answerAttemptDto = new AnswerAttemptDto();
        answerAttemptDto.setCorrectAnswer(true);
        answerAttemptDto.setQuestionId(ID);
        answerAttemptDto.setFeedback("Feedback for answer");
        answerAttemptDto.setId(ID);
        answerAttemptDto.setAnswerId(ID);
        answerAttemptDto.setCorrectAnswerIds(answerIds);
        return answerAttemptDto;
    }

    private Optional<GamesByCategoryResult> createGamesByCategoryResultForUser(
            Category category, User user) {
        GamesByCategoryResult gamesByCategoryResult = new GamesByCategoryResult();
        gamesByCategoryResult.setCategoryScore(10);
        gamesByCategoryResult.setCategory(category);
        gamesByCategoryResult.setGamesPlayed(1);
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

    private AnswerAttempt createAnswerAttempt(QuizAttempt quizAttempt, Answer answer) {
        AnswerAttempt answerAttempt = new AnswerAttempt();
        answerAttempt.setAnsweredAt(LocalDateTime.now());
        answerAttempt.setQuizAttempt(quizAttempt);
        answerAttempt.setCorrectAnswer(true);
        answerAttempt.setId(ID);
        answerAttempt.setAnswer(answer);
        return answerAttempt;
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

    private Optional<Answer> createAnswer(Question question) {
        Answer answer = new Answer();
        answer.setPoints(10);
        answer.setId(ID);
        answer.setQuestion(question);
        answer.setCorrect(true);
        answer.setText("Text for question");
        return Optional.of(answer);
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
}
