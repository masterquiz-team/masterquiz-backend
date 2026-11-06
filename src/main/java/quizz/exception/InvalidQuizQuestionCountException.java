package quizz.exception;

public class InvalidQuizQuestionCountException extends RuntimeException {
    public InvalidQuizQuestionCountException(String message) {
        super(message);
    }
}
