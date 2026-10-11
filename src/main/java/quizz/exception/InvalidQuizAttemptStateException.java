package quizz.exception;

public class InvalidQuizAttemptStateException extends RuntimeException {
    public InvalidQuizAttemptStateException(String message) {
        super(message);
    }
}
