package com.hackaton.vk_mini_backend.exception;

import com.hackaton.vk_mini_backend.dto.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@NoArgsConstructor
public class GlobalExceptionHandler {

    @Value("${global-exception-handler.trace:false}")
    private boolean printStackTrace;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex) {
        final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Ошибка валидации");
        final BindingResult bindingResult = ex.getBindingResult();

        for (final FieldError fieldError : bindingResult.getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.unprocessableEntity().body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleConstraintViolationException(
            final ConstraintViolationException constraintViolationException) {
        log.error(constraintViolationException.getMessage(), constraintViolationException);
        return buildErrorResponse(constraintViolationException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNoSuchElementFoundException(
            final NoSuchElementFoundException itemNotFoundException) {
        log.error(itemNotFoundException.getMessage(), itemNotFoundException);
        return buildErrorResponse(itemNotFoundException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateElementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleDuplicateCodeException(
            final DuplicateElementException duplicateElementException) {
        log.error(duplicateElementException.getMessage(), duplicateElementException);
        return buildErrorResponse(duplicateElementException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleAccessException(final ForbiddenException forbiddenException) {
        log.error(forbiddenException.getMessage(), forbiddenException);
        return buildErrorResponse(forbiddenException, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleAccessDeniedException(final AccessDeniedException exception) {
        log.error(exception.getMessage(), exception);
        return buildErrorResponse(exception, "Доступ запрещен", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(TokenRefreshException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleTokenRefreshException(final TokenRefreshException exception) {
        log.error(exception.getMessage(), exception);
        return buildErrorResponse(exception, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleIllegalArgumentException(
            final IllegalArgumentException illegalArgumentException) {
        log.warn("Некорректные данные: {}", illegalArgumentException.getMessage());
        return buildErrorResponse(illegalArgumentException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleEntityNotFoundException(final EntityNotFoundException entityNotFoundException) {
        log.warn("Данные не найдены: {}", entityNotFoundException.getMessage());
        return buildErrorResponse(entityNotFoundException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(final Exception exception) {
        log.error(exception.getMessage(), exception);
        return buildErrorResponse(exception, "Произошла внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> buildErrorResponse(final Exception exception, final HttpStatus httpStatus) {
        return buildErrorResponse(exception, exception.getMessage(), httpStatus);
    }

    private ResponseEntity<Object> buildErrorResponse(
            final Exception exception, final String message, final HttpStatus httpStatus) {
        final ErrorResponse response = printStackTrace
                ? new ErrorResponse(httpStatus, message, exception)
                : new ErrorResponse(httpStatus, message);

        return ResponseEntity.status(httpStatus).body(response);
    }
}
