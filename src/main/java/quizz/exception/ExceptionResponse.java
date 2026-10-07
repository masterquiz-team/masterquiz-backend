package quizz.exception;

import java.time.Instant;

public record ExceptionResponse(Instant time, String message, int status) {
}
