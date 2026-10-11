package quizz.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quizz.dto.quizattempt.QuizAttemptDto;
import quizz.dto.quizattempt.QuizAttemptRequestDto;
import quizz.exception.EntityNotFoundException;
import quizz.exception.InvalidQuizAttemptStateException;
import quizz.exception.UnauthorizedAccessException;
import quizz.mapper.QuizAttemptMapper;
import quizz.model.Quiz;
import quizz.model.QuizAttempt;
import quizz.model.Status;
import quizz.model.User;
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

    @Override
    @Transactional
    public QuizAttemptDto createQuizAttempt(QuizAttemptRequestDto quizAttemptRequestDto,
                                            String email) {
        Quiz quiz = quizRepository.findById(quizAttemptRequestDto.getQuizId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "Quiz with id: " + quizAttemptRequestDto.getQuizId() + " doesn't exist"));
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
        if (!quizAttemptUser.equals(user)) {
            throw new UnauthorizedAccessException("You have no permission");
        }
    }
}
