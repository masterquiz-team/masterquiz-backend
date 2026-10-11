package quizz.service;

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
import quizz.model.Question;
import quizz.model.QuizAttempt;
import quizz.model.Status;
import quizz.model.User;
import quizz.repository.AnswerAttemptRepository;
import quizz.repository.AnswerRepository;
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

    @Override
    @Transactional
    public AnswerAttemptDto createAnswerAttempt(
            AnswerAttemptRequestDto answerAttemptRequestDto,
            String email) {
        QuizAttempt quizAttempt = quizAttemptRepository
                .findById(answerAttemptRequestDto.getQuizAttemptId()).orElseThrow(
                        () -> new EntityNotFoundException("Quiz attempt with id: "
                                + answerAttemptRequestDto.getQuizAttemptId() + " doesn't exist"));
        User user = findUser(email);

        checkPermission(quizAttempt.getUser(), user);

        if (!quizAttempt.getStatus().equals(Status.IN_PROGRESS)) {
            throw new InvalidQuizAttemptStateException("Quiz attempt has to be in progress");
        }

        Answer answer = answerRepository.findById(answerAttemptRequestDto.getAnswerId())
                .orElseThrow(() -> new EntityNotFoundException("Answer with id: "
                        + answerAttemptRequestDto.getAnswerId() + " doesn't exist"));

        Optional<Question> question = quizAttempt.getQuiz().getListOfQuestions().stream()
                .filter(q -> answer.getQuestion().equals(q))
                .findFirst();

        if (question.isEmpty()) {
            throw new EntityNotFoundException("Answer doesn't belong to any question");
        }

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
        answerAttempt.setCorrectAnswer(answer.isCorrect());
        answerAttempt.setQuizAttempt(quizAttempt);

        AnswerAttempt savedAttempt = answerAttemptRepository.save(answerAttempt);

        return answerAttemptMapper.toDto(savedAttempt);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnswerAttemptDto> getAnswerAttemptsByQuizAttemptId(Long id, String email) {
        QuizAttempt quizAttempt = quizAttemptRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "Quiz attempt with id: " + id + " doesn't exist"));
        User user = findUser(email);

        checkPermission(quizAttempt.getUser(), user);

        List<AnswerAttempt> answerAttempts = answerAttemptRepository
                .findByQuizAttemptId(quizAttempt.getId());

        return answerAttempts.stream()
                .map(answerAttemptMapper::toDto)
                .toList();
    }

    private void checkPermission(User quizAttemptUser, User user) {
        if (!quizAttemptUser.equals(user)) {
            throw new UnauthorizedAccessException("You have no permission");
        }
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email: " + email + " doesn't exist"));
    }
}
