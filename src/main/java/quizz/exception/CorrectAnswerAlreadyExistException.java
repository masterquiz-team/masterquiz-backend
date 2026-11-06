package quizz.exception;

public class CorrectAnswerAlreadyExistException extends RuntimeException {
    public CorrectAnswerAlreadyExistException(String message) {
        super(message);
    }
}
