package timofeyqa.rococo.controller.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import timofeyqa.rococo.controller.error.ApiError;
import timofeyqa.rococo.ex.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${api.version}")
    private String apiVersion;

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        LOG.error(request.getRequestURI(), ex);
        return new ResponseEntity<>(
                new ApiError(
                    apiVersion,
                    HttpStatus.NOT_FOUND.toString(),
                    "Not found",
                    request.getRequestURI(),
                    ex.getMessage()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        LOG.error(request.getRequestURI(), ex);
        return new ResponseEntity<>(
            new ApiError(
                apiVersion,
                ex.getStatusCode().toString(),
                ex.getMessage(),
                request.getRequestURI(),
                ex.getReason()
            ),
            ex.getStatusCode()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        LOG.warn(request.getRequestURI(), ex);
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .findFirst()
            .orElse("Validation error");
        return ResponseEntity.badRequest().body(
            new ApiError(apiVersion, "400", "Validation error", request.getRequestURI(), message)
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        LOG.warn(request.getRequestURI(), ex);
        String message = ex.getConstraintViolations().stream()
            .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
            .findFirst()
            .orElse("Validation failed");
        return ResponseEntity.badRequest().body(
            new ApiError(apiVersion, "400", "Constraint violation", request.getRequestURI(), message)
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        LOG.warn(request.getRequestURI(), ex);
        return ResponseEntity.badRequest().body(
            new ApiError(apiVersion, "400", "Malformed JSON request", request.getRequestURI(), ex.getMessage())
        );
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiError> handleIllegalExceptions(RuntimeException ex, HttpServletRequest request) {
        LOG.error(request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ApiError(apiVersion, "400", "Invalid argument or state", request.getRequestURI(), ex.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception ex, HttpServletRequest request) {
        LOG.error(request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            new ApiError(apiVersion, "500", "Internal Server Error", request.getRequestURI(), ex.getMessage())
        );
    }

}
