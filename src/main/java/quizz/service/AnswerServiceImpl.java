package quizz.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quizz.dto.answer.AnswerDto;
import quizz.dto.answer.AnswerRequestDto;
import quizz.dto.answer.UpdateRequestDto;
import quizz.exception.AnswerLimitExceededException;
import quizz.exception.CorrectAnswerAlreadyExistException;
import quizz.exception.EntityNotFoundException;
import quizz.exception.QuestionAnswerCounterException;
import quizz.mapper.AnswerMapper;
import quizz.model.Answer;
import quizz.model.Question;
import quizz.repository.AnswerRepository;
import quizz.repository.QuestionRepository;
import quizz.service.interfaces.AnswerService;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AnswerMapper answerMapper;

    @Override
    @Transactional
    public AnswerDto createAnswer(AnswerRequestDto answerRequestDto) {
        Question question = questionRepository.findById(answerRequestDto.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Question with id: " + answerRequestDto.getQuestionId()
                                + " doesn't exist"));
        if (question.getAnswerCounter() >= 4) {
            throw new QuestionAnswerCounterException("The maximum amount of the answers is 4");
        }
        if (!question.isCorrectAnswer() && !answerRequestDto.isCorrect()
                && question.getAnswerCounter() == 3) {
            throw new AnswerLimitExceededException("At least one of 4 answers has to be correct");
        }
        correctAnswerExist(question.isCorrectAnswer(), answerRequestDto.isCorrect());

        if (answerRequestDto.isCorrect()) {
            question.setCorrectAnswer(true);
        }
        Answer answer = answerMapper.toModel(answerRequestDto);
        answer.setQuestion(question);
        Answer savedAnswer = answerRepository.save(answer);
        question.setAnswerCounter(question.getAnswerCounter() + 1);
        return answerMapper.toDto(savedAnswer);
    }

    @Override
    @Transactional(readOnly = true)
    public AnswerDto getAnswer(Long id) {
        Answer answer = findById(id);
        return answerMapper.toDto(answer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnswerDto> getAnswersByQuestionId(Long id) {
        return answerRepository.findByQuestionId(id).stream()
                .map(answerMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public AnswerDto updateAnswer(UpdateRequestDto updateRequestDto) {
        Answer answer = findById(updateRequestDto.getId());
        Question question = answer.getQuestion();

        if (!answer.isCorrect() && updateRequestDto.isCorrect()
                && question.isCorrectAnswer()) {
            throw new CorrectAnswerAlreadyExistException(
                    "Only one answer can be correct");
        }
        boolean wasCorrect = answer.isCorrect();

        answerMapper.updateAnswer(updateRequestDto, answer);

        if (wasCorrect != answer.isCorrect()) {
            question.setCorrectAnswer(answer.isCorrect());
        }

        return answerMapper.toDto(answer);
    }

    @Override
    @Transactional
    public void deleteAnswer(Long id) {
        Answer answer = findById(id);
        Question question = answer.getQuestion();
        question.setAnswerCounter(question.getAnswerCounter() - 1);
        if (answer.isCorrect()) {
            question.setCorrectAnswer(false);
        }
        answerRepository.delete(answer);
    }

    private Answer findById(Long id) {
        return answerRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Answer with id: "
                        + id + " doesn't exist"));
    }

    private void correctAnswerExist(boolean answerCorrect, boolean requestAnswerCorrect) {
        if (answerCorrect && requestAnswerCorrect) {
            throw new CorrectAnswerAlreadyExistException(
                    "Correct answer already exist. Only one answer can be correct");
        }
    }
}
