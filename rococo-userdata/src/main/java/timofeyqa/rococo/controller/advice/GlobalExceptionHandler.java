package timofeyqa.rococo.controller.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
    Map<String, Object> error = new HashMap<>();
    error.put("timestamp", Instant.now().toString());
    error.put("status", 400);
    error.put("error", "Bad Request");
    error.put("message", ex.getMessage());
    error.put("path", request.getRequestURI());

    return ResponseEntity.badRequest().body(error);
  }
}
