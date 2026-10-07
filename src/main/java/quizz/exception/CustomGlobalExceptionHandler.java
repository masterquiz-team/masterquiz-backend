package quizz.exception;


import java.time.Instant;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now(),
                "Invalid argument",
                400);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Object> handleLoginException(String message) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now(),
                message,
                401);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RegisterException.class)
    public ResponseEntity<Object> handleRegisterException(String message) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now(),
                message,
                409);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.CONFLICT);
    }
}
