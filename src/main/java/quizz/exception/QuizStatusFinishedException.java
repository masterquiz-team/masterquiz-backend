package quizz.exception;

public class QuizStatusFinishedException extends RuntimeException {
    public QuizStatusFinishedException(String message) {
        super(message);
    }
}
