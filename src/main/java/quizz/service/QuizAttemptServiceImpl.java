package quizz.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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
import quizz.model.Question;
import quizz.model.Quiz;
import quizz.model.QuizAttempt;
import quizz.model.Status;
import quizz.model.User;
import quizz.repository.CategoryRepository;
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

    @Override
    @Transactional
    public QuizAttemptDto createQuizAttempt(Long quizId,
                                            String email) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Quiz with id: " + quizId + " doesn't exist"));
        User user = checkIfUserExist(email);

        QuizAttempt quizAttempt = new QuizAttempt();
        quizAttempt.setQuiz(quiz);
        quizAttempt.setUser(user);
        quizAttempt.setStatus(Status.IN_PROGRESS);
        quizAttempt.setStartedAt(LocalDateTime.now());

        QuizAttempt savedQuizAttempt = attemptRepository.save(quizAttempt);

        return quizAttemptMapper.toDto(savedQuizAttempt);
    }

    @Override
    @Transactional
    public QuizAttemptDto createQuizAttemptWithRandomQuestions(
            QuizAttemptRandomRequestDto requestDto,
            String email) {
        User user = checkIfUserExist(email);
        QuizAttempt quizAttempt = new QuizAttempt();
        quizAttempt.setUser(user);
        Category categoryName = categoryRepository.findByName(requestDto.getCategory())
                .orElseThrow(() -> new EntityNotFoundException("Category name: "
                        + requestDto.getCategory() + " doesn't exist"));

        if (!isDifficultyValid(requestDto.getDifficulty())) {
            throw new EntityNotFoundException("Difficulty name: " + requestDto.getDifficulty()
                    + " doesn't exist");
        }

        Difficulty difficultyName = Difficulty.valueOf(requestDto.getDifficulty().toUpperCase());
        List<Question> listOfQuestions = questionRepository.findByDifficulty(difficultyName);
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

        quizAttempt.setQuiz(savedQuiz);
        quizAttempt.setStatus(Status.IN_PROGRESS);
        quizAttempt.setStartedAt(LocalDateTime.now());
        QuizAttempt savedQuizAttempt = attemptRepository.save(quizAttempt);

        return quizAttemptMapper.toDto(savedQuizAttempt);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizAttemptDto getQuizAttempt(Long id, String email) {
        User user = checkIfUserExist(email);
        QuizAttempt quizAttempt = attemptRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "Quiz attempt with id: " + id + " doesn't exist"));

        checkIfHasPermission(quizAttempt.getUser(), user);

        return quizAttemptMapper.toDto(quizAttempt);
    }

    @Override
    @Transactional
    public QuizAttemptDto finishQuizAttempt(Long id, String email) {
        QuizAttempt quizAttempt = attemptRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "Quiz attempt with id: " + id + " doesn't exist"));
        User user = checkIfUserExist(email);

        checkIfHasPermission(quizAttempt.getUser(), user);

        if (!quizAttempt.getStatus().equals(Status.IN_PROGRESS)) {
            throw new InvalidQuizAttemptStateException("Quiz status is not in progress");
        }
        quizAttempt.setFinishedAt(LocalDateTime.now());
        quizAttempt.setStatus(Status.FINISHED);
        return quizAttemptMapper.toDto(quizAttempt);
    }

    private User checkIfUserExist(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(
                        "User with email: " + email + " doesn't exist"));
    }

    private void checkIfHasPermission(User quizAttemptUser, User user) {
        if (!quizAttemptUser.getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You have no permission");
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
