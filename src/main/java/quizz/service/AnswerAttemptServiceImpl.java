package quizz.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quizz.dto.answerattempt.AnswerAttemptDto;
import quizz.dto.answerattempt.AnswerAttemptRequestDto;
import quizz.exception.EntityNotFoundException;
import quizz.exception.InvalidQuizAttemptStateException;
import quizz.exception.QuestionAlreadyAnsweredException;
import quizz.exception.UnauthorizedAccessException;
import quizz.mapper.AnswerAttemptMapper;
import quizz.model.Answer;
import quizz.model.AnswerAttempt;
import quizz.model.GamesByCategoryResult;
import quizz.model.GuestUser;
import quizz.model.Level;
import quizz.model.Question;
import quizz.model.QuizAttempt;
import quizz.model.Status;
import quizz.model.User;
import quizz.repository.AnswerAttemptRepository;
import quizz.repository.AnswerRepository;
import quizz.repository.GamesByCategoryResultRepository;
import quizz.repository.GuestUserRepository;
import quizz.repository.QuizAttemptRepository;
import quizz.repository.UserRepository;
import quizz.service.interfaces.AnswerAttemptService;

@Service
@RequiredArgsConstructor
public class AnswerAttemptServiceImpl implements AnswerAttemptService {
    private final AnswerAttemptRepository answerAttemptRepository;
    private final UserRepository userRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final AnswerRepository answerRepository;
    private final AnswerAttemptMapper answerAttemptMapper;
    private final GamesByCategoryResultRepository gamesByCategoryResultRepository;
    private final GuestUserRepository guestUserRepository;

    @Override
    @Transactional
    public AnswerAttemptDto createAnswerAttempt(
            AnswerAttemptRequestDto answerAttemptRequestDto,
            String username) {

        Optional<User> user = userRepository.findByEmail(username);
        Optional<GuestUser> guestUser = guestUserRepository.findByNickname(username);

        QuizAttempt quizAttempt = quizAttemptRepository
                .findById(answerAttemptRequestDto.getQuizAttemptId()).orElseThrow(
                        () -> new EntityNotFoundException("Quiz attempt with id: "
                                + answerAttemptRequestDto.getQuizAttemptId() + " doesn't exist"));

        checkPermission(user, guestUser, quizAttempt);

        if (!quizAttempt.getStatus().equals(Status.IN_PROGRESS)) {
            throw new InvalidQuizAttemptStateException("Quiz attempt has to be in progress");
        }

        Answer answer = answerRepository.findById(answerAttemptRequestDto.getAnswerId())
                .orElseThrow(() -> new EntityNotFoundException("Answer with id: "
                        + answerAttemptRequestDto.getAnswerId() + " doesn't exist"));

        Question question = quizAttempt.getQuiz().getListOfQuestions().stream()
                .filter(q -> answer.getQuestion().getId().equals(q.getId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Answer doesn't belong to any question"));

        int questionPoints = question.getDifficulty().getPoints();

        boolean alreadyAnswered = answerAttemptRepository
                .existsByQuizAttemptIdAndAnswerQuestionId(
                        quizAttempt.getId(),
                        answer.getQuestion().getId());

        if (alreadyAnswered) {
            throw new QuestionAlreadyAnsweredException(
                    "User has already answered this question");
        }

        AnswerAttempt answerAttempt = new AnswerAttempt();
        answerAttempt.setAnswer(answer);
        answerAttempt.setAnsweredAt(LocalDateTime.now());
        answerAttempt.setCorrectAnswer(answer.isCorrect());
        answerAttempt.setQuizAttempt(quizAttempt);

        AnswerAttempt savedAttempt = answerAttemptRepository.save(answerAttempt);

        if (user.isPresent()) {
            User unwrappedUser = user.get();
            GamesByCategoryResult gamesByCategoryResult = gamesByCategoryResultRepository
                    .findByCategoryIdAndUserId(
                            question.getCategory().getId(), unwrappedUser.getId())
                    .orElseGet(() -> {
                        GamesByCategoryResult categoryResult = new GamesByCategoryResult();
                        categoryResult.setCategory(question.getCategory());
                        categoryResult.setUser(unwrappedUser);
                        return gamesByCategoryResultRepository.save(categoryResult);
                    });
            if (answer.isCorrect()) {
                unwrappedUser.setScore(unwrappedUser.getScore() + questionPoints);
                Level level = Level.fromScore(unwrappedUser.getScore());
                unwrappedUser.setLevel(level);
                gamesByCategoryResult.setCategoryScore(gamesByCategoryResult.getCategoryScore()
                        + questionPoints);
            }
        }

        if (guestUser.isPresent()) {
            GuestUser unwrappedGuestUser = guestUser.get();
            GamesByCategoryResult gamesByCategoryResult = gamesByCategoryResultRepository
                    .findByCategoryIdAndGuestUserId(
                            question.getCategory().getId(),
                            unwrappedGuestUser.getId())
                    .orElseGet(() -> {
                        GamesByCategoryResult categoryResult = new GamesByCategoryResult();
                        categoryResult.setCategory(question.getCategory());
                        categoryResult.setGuestUser(unwrappedGuestUser);
                        return gamesByCategoryResultRepository.save(categoryResult);
                    });
            if (answer.isCorrect()) {
                unwrappedGuestUser.setScore(unwrappedGuestUser.getScore() + questionPoints);
                gamesByCategoryResult.setCategoryScore(gamesByCategoryResult.getCategoryScore()
                        + questionPoints);
            }
        }

        List<Long> answersIds = answerRepository
                .findByQuestionIdAndCorrectTrue(question.getId())
                .stream()
                .map(Answer::getId)
                .toList();

        AnswerAttemptDto answerAttemptDto = answerAttemptMapper.toDto(savedAttempt);
        answerAttemptDto.setQuestionId(question.getId());
        answerAttemptDto.setFeedback(question.getFeedback());
        answerAttemptDto.setCorrectAnswerIds(answersIds);

        return answerAttemptDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnswerAttemptDto> getAnswerAttemptsByQuizAttemptId(Long id, String username) {
        QuizAttempt quizAttempt = quizAttemptRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "Quiz attempt with id: " + id + " doesn't exist"));

        Optional<User> user = userRepository.findByEmail(username);
        Optional<GuestUser> guestUser = guestUserRepository.findByNickname(username);

        checkPermission(user, guestUser, quizAttempt);

        List<AnswerAttempt> answerAttempts = answerAttemptRepository
                .findByQuizAttemptId(quizAttempt.getId());

        return answerAttempts.stream()
                .map(answerAttemptMapper::toDto)
                .toList();
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
}
