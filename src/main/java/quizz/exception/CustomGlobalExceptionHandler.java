package quizz.exception;

import java.time.Instant;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
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
                status.value(),
                new ArrayList<>(ex.getBindingResult().getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage).toList()));
        log.warn("Invalid argument: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Object> handleLoginException(LoginException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now(),
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value());
        log.warn("Login exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RegisterException.class)
    public ResponseEntity<Object> handleRegisterException(RegisterException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now(),
                ex.getMessage(),
                HttpStatus.CONFLICT.value());
        log.warn("Register exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityFoundException.class)
    public ResponseEntity<Object> handleEntityFoundException(EntityFoundException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now(),
                ex.getMessage(),
                HttpStatus.CONFLICT.value());
        log.warn("Entity found exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value());
        log.warn("Entity not found exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(QuestionAnswerCounterException.class)
    public ResponseEntity<Object> handleQuestionCounterException(
            QuestionAnswerCounterException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value());
        log.warn("Question answer counter exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AnswerLimitExceededException.class)
    public ResponseEntity<Object> handleAnswerLimitExceededException(
            AnswerLimitExceededException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value());
        log.warn("Answer limit exceeded exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CorrectAnswerAlreadyExistException.class)
    public ResponseEntity<Object> handleCorrectAnswerAlreadyExistException(
            CorrectAnswerAlreadyExistException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value());
        log.warn("Correct answer already exist exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidQuizQuestionCountException.class)
    public ResponseEntity<Object> handleInvalidQuizQuestionCountException(
            InvalidQuizQuestionCountException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value());
        log.warn("Invalid quiz question count exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<Object> handleUnauthorizedAccessException(
            UnauthorizedAccessException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now(),
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value());
        log.warn("Unauthorized access exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidQuizAttemptStateException.class)
    public ResponseEntity<Object> handleQuizStatusInProgressException(
            InvalidQuizAttemptStateException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value());
        log.warn("Invalid quiz attempt state exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(QuestionAlreadyAnsweredException.class)
    public ResponseEntity<Object> handleQuestionAlreadyAnsweredException(
            QuestionAlreadyAnsweredException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now(),
                ex.getMessage(),
                HttpStatus.CONFLICT.value());
        log.warn("Question already answered exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value());
        log.warn("Illegal argument exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
