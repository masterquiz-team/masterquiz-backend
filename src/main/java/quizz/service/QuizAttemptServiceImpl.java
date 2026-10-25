package quizz.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quizz.dto.quizattempt.QuizAttemptDto;
import quizz.dto.quizattempt.QuizAttemptRandomRequestDto;
import quizz.exception.EntityNotFoundException;
import quizz.exception.InvalidQuizAttemptStateException;
import quizz.exception.UnauthorizedAccessException;
import quizz.mapper.QuizAttemptMapper;
import quizz.model.Category;
import quizz.model.Difficulty;
import quizz.model.GamesByCategoryResult;
import quizz.model.GuestUser;
import quizz.model.Question;
import quizz.model.Quiz;
import quizz.model.QuizAttempt;
import quizz.model.Status;
import quizz.model.User;
import quizz.repository.CategoryRepository;
import quizz.repository.GamesByCategoryResultRepository;
import quizz.repository.GuestUserRepository;
import quizz.repository.QuestionRepository;
import quizz.repository.QuizAttemptRepository;
import quizz.repository.QuizRepository;
import quizz.repository.UserRepository;
import quizz.service.interfaces.QuizAttemptService;

@Service
@RequiredArgsConstructor
public class QuizAttemptServiceImpl implements QuizAttemptService {
    private final QuizAttemptRepository attemptRepository;
    private final QuizAttemptMapper quizAttemptMapper;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final GamesByCategoryResultRepository gamesByCategoryResultRepository;
    private final GuestUserRepository guestUserRepository;

    @Override
    @Transactional
    public QuizAttemptDto createQuizAttempt(Long quizId,
                                            String username) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Quiz with id: " + quizId + " doesn't exist"));
        Optional<User> user = userRepository.findByEmail(username);
        Optional<GuestUser> guestUser = guestUserRepository.findByNickname(username);

        QuizAttempt quizAttempt = createQuizAttemptMethod(user, guestUser, quiz);

        return quizAttemptMapper.toDto(quizAttempt);
    }

    @Override
    @Transactional
    public QuizAttemptDto createQuizAttemptWithRandomQuestions(
            QuizAttemptRandomRequestDto requestDto,
            String username) {

        Category categoryName = categoryRepository.findByName(requestDto.getCategory())
                .orElseThrow(() -> new EntityNotFoundException("Category name: "
                        + requestDto.getCategory() + " doesn't exist"));

        if (!isDifficultyValid(requestDto.getDifficulty())) {
            throw new EntityNotFoundException("Difficulty name: " + requestDto.getDifficulty()
                    + " doesn't exist");
        }

        Difficulty difficultyName = Difficulty.valueOf(requestDto.getDifficulty().toUpperCase());
        List<Question> listOfQuestions = questionRepository
                .findByDifficultyAndCategory(difficultyName, categoryName);
        Collections.shuffle(listOfQuestions);

        List<Question> randomizedQuestions = listOfQuestions.stream()
                .limit(10)
                .toList();

        Quiz quiz = new Quiz();
        quiz.setCategory(categoryName);
        quiz.setDifficulty(difficultyName);
        quiz.setTitle(requestDto.getQuizTitle());
        quiz.setListOfQuestions(randomizedQuestions);

        Quiz savedQuiz = quizRepository.save(quiz);

        Optional<User> user = userRepository.findByEmail(username);
        Optional<GuestUser> guestUser = guestUserRepository.findByNickname(username);

        QuizAttempt quizAttempt = createQuizAttemptMethod(user, guestUser, savedQuiz);

        return quizAttemptMapper.toDto(quizAttempt);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizAttemptDto getQuizAttempt(Long id, String username) {
        Optional<User> user = userRepository.findByEmail(username);

        Optional<GuestUser> guestUser = guestUserRepository.findByNickname(username);

        QuizAttempt quizAttempt = attemptRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "Quiz attempt with id: " + id + " doesn't exist"));

        checkPermission(user, guestUser, quizAttempt);

        return quizAttemptMapper.toDto(quizAttempt);
    }

    @Override
    @Transactional
    public QuizAttemptDto finishQuizAttempt(Long id, String username) {
        QuizAttempt quizAttempt = attemptRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "Quiz attempt with id: " + id + " doesn't exist"));

        if (!quizAttempt.getStatus().equals(Status.IN_PROGRESS)) {
            throw new InvalidQuizAttemptStateException("Quiz status is not in progress");
        }

        Optional<User> user = userRepository.findByEmail(username);
        Optional<GuestUser> guestUser = guestUserRepository.findByNickname(username);

        if (user.isPresent() == guestUser.isPresent()) {
            throw new UnauthorizedAccessException("Invalid user identity");
        }

        if (user.isPresent()) {
            User unwrappedUser = user.get();
            if (quizAttempt.getUser() == null
                    || !unwrappedUser.getId().equals(quizAttempt.getUser().getId())) {
                throw new UnauthorizedAccessException("You have no permission");
            }
            unwrappedUser.setTotalGamesPlayed(unwrappedUser.getTotalGamesPlayed() + 1);

            GamesByCategoryResult result = gamesByCategoryResultRepository
                    .findByCategoryIdAndUserId(
                            quizAttempt.getQuiz().getCategory().getId(),
                            unwrappedUser.getId()).orElseGet(() -> {
                                GamesByCategoryResult userCatResult = new GamesByCategoryResult();
                                userCatResult.setGuestUser(null);
                                userCatResult.setCategoryScore(0);
                                userCatResult.setUser(unwrappedUser);
                                userCatResult.setCategory(quizAttempt.getQuiz().getCategory());
                                userCatResult.setGamesPlayed(0);
                                return gamesByCategoryResultRepository.save(userCatResult);
                            });

            result.setGamesPlayed(result.getGamesPlayed() + 1);

        } else {

            if (quizAttempt.getGuestUser() == null
                    || !guestUser.get().getId().equals(quizAttempt.getGuestUser().getId())) {
                throw new UnauthorizedAccessException("You have no permission");
            }
            GuestUser unwrappedGuestUser = guestUser.get();
            unwrappedGuestUser.setTotalGamesPlayed(unwrappedGuestUser.getTotalGamesPlayed() + 1);

            GamesByCategoryResult result = gamesByCategoryResultRepository
                    .findByCategoryIdAndGuestUserId(
                            quizAttempt.getQuiz().getCategory().getId(),
                            unwrappedGuestUser.getId()).orElseGet(() -> {

                                GamesByCategoryResult guestCatResult = new GamesByCategoryResult();
                                guestCatResult.setUser(null);
                                guestCatResult.setCategoryScore(0);
                                guestCatResult.setGuestUser(unwrappedGuestUser);
                                guestCatResult.setCategory(quizAttempt.getQuiz().getCategory());
                                guestCatResult.setGamesPlayed(0);
                                return gamesByCategoryResultRepository.save(guestCatResult);
                            });

            result.setGamesPlayed(result.getGamesPlayed() + 1);
        }
        quizAttempt.setFinishedAt(LocalDateTime.now());
        quizAttempt.setStatus(Status.FINISHED);
        return quizAttemptMapper.toDto(quizAttempt);
    }

    private QuizAttempt createQuizAttemptMethod(
            Optional<User> user,
            Optional<GuestUser> guestUser,
            Quiz quiz) {

        QuizAttempt quizAttempt = new QuizAttempt();
        quizAttempt.setStatus(Status.IN_PROGRESS);
        quizAttempt.setStartedAt(LocalDateTime.now());
        quizAttempt.setQuiz(quiz);
        if (user.isEmpty() && guestUser.isPresent()) {
            GuestUser unwrappedGuestUser = guestUser.get();
            quizAttempt.setGuestUser(unwrappedGuestUser);
        } else if (user.isPresent() && guestUser.isEmpty()) {
            User unwrappedUser = user.get();
            quizAttempt.setUser(unwrappedUser);
        } else {
            throw new EntityNotFoundException("User or guest user doesn't exist");
        }
        return attemptRepository.save(quizAttempt);
    }

    private void checkPermission(
            Optional<User> user,
            Optional<GuestUser> guestUser,
            QuizAttempt quizAttempt) {

        if (user.isPresent() == guestUser.isPresent()) {
            throw new UnauthorizedAccessException("Invalid user identity");
        }
        if (user.isPresent()) {
            User unwrappedUser = user.get();

            if (quizAttempt.getUser() == null
                    || !unwrappedUser.getId().equals(quizAttempt.getUser().getId())) {
                throw new UnauthorizedAccessException("You have no permission");
            }
        } else {
            GuestUser unwrappedGuestUser = guestUser.get();
            if (quizAttempt.getGuestUser() == null
                    || !unwrappedGuestUser.getId().equals(quizAttempt.getGuestUser().getId())) {
                throw new UnauthorizedAccessException("You have no permission");
            }
        }
    }

    private boolean isDifficultyValid(String difficulty) {
        try {
            Difficulty.valueOf(difficulty.toUpperCase());
            return true;
        } catch (IllegalArgumentException | NullPointerException ex) {
            return false;
        }
    }

}
