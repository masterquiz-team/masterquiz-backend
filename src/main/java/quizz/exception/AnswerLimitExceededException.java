package quizz.exception;

public class AnswerLimitExceededException extends RuntimeException {
    public AnswerLimitExceededException(String message) {
        super(message);
    }
}
