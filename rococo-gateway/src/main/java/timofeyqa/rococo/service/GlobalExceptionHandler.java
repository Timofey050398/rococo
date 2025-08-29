package timofeyqa.rococo.service;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import timofeyqa.rococo.config.RococoGatewayServiceConfig;
import timofeyqa.rococo.controller.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import timofeyqa.rococo.ex.BadRequestException;
import timofeyqa.rococo.ex.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final RococoGatewayServiceConfig config;

    @Autowired
    public GlobalExceptionHandler(RococoGatewayServiceConfig config) {
        this.config = config;
    }

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ApiError> handleGrpcException(StatusRuntimeException ex, HttpServletRequest request) {
        LOG.error("gRPC error at {}", request.getRequestURI(), ex);

        Status.Code code = ex.getStatus().getCode();
        HttpStatus httpStatus = switch (code) {
          case INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
          case NOT_FOUND -> HttpStatus.NOT_FOUND;
          case ALREADY_EXISTS -> HttpStatus.CONFLICT;
          case PERMISSION_DENIED -> HttpStatus.FORBIDDEN;
          case UNAUTHENTICATED -> HttpStatus.UNAUTHORIZED;
          case UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
          default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

      String description = ex.getStatus().getDescription() != null
            ? ex.getStatus().getDescription()
            : "gRPC error: " + code;

        return new ResponseEntity<>(
            new ApiError(
                config.getApiVersion(),
                String.valueOf(httpStatus.value()),
                "gRPC error",
                request.getRequestURI(),
                description
            ),
            httpStatus
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        LOG.error(request.getRequestURI(), ex);
        return new ResponseEntity<>(
                new ApiError(
                        config.getApiVersion(),
                        HttpStatus.NOT_FOUND.toString(),
                        "Not found",
                        request.getRequestURI(),
                        ex.getMessage()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        LOG.error(request.getRequestURI(), ex);
        return new ResponseEntity<>(
            new ApiError(
                config.getApiVersion(),
                HttpStatus.NOT_FOUND.toString(),
                "Bad request",
                request.getRequestURI(),
                ex.getMessage()
            ),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        LOG.error(request.getRequestURI(), ex);
        return new ResponseEntity<>(
            new ApiError(
                config.getApiVersion(),
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
            new ApiError(config.getApiVersion(), "400", "Validation error", request.getRequestURI(), message)
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
            new ApiError(config.getApiVersion(), "400", "Constraint violation", request.getRequestURI(), message)
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        LOG.warn(request.getRequestURI(), ex);
        return ResponseEntity.badRequest().body(
            new ApiError(config.getApiVersion(), "400", "Malformed JSON request", request.getRequestURI(), ex.getMessage())
        );
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiError> handleIllegalExceptions(RuntimeException ex, HttpServletRequest request) {
        LOG.error(request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ApiError(config.getApiVersion(), "400", "Invalid argument or state", request.getRequestURI(), ex.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception ex, HttpServletRequest request) {
        LOG.error(request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            new ApiError(config.getApiVersion(), "500", "Internal Server Error", request.getRequestURI(), ex.getMessage())
        );
    }

}
