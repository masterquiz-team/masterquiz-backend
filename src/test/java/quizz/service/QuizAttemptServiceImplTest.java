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
import quizz.dto.quizattempt.QuizAttemptDto;
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
        Optional<GuestUser> guestUser = createGuestUser();
        Category category = createCategory();
        Question question = createQuestion(category);
        Quiz quiz = createQuiz(category, List.of(question));
        Optional<QuizAttempt> quizAttempt = createQuizAttemptForUser(user.get(), quiz);
        QuizAttemptDto expected = createQuizAttemptDto(user.get(), guestUser.get());

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

    private QuizAttemptDto createQuizAttemptDto(User user, GuestUser guestUser) {
        QuizAttemptDto quizAttemptDto = new QuizAttemptDto();
        quizAttemptDto.setId(ID);
        quizAttemptDto.setUserId(user.getId());
        quizAttemptDto.setGuestUserId(guestUser.getId());
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
}
