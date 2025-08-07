package timofeyqa.rococo.service.handler;

import io.grpc.Status;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GrpcGlobalExceptionHandlerTest {

  private GrpcGlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GrpcGlobalExceptionHandler();
  }

  @Test
  void handleInvalidArgument_returnsInvalidArgumentStatus() {
    IllegalArgumentException ex = new IllegalArgumentException("Invalid input");

    Status status = handler.handleInvalidArgument(ex);

    assertEquals(Status.INVALID_ARGUMENT.getCode(), status.getCode());
    assertTrue(status.getDescription().contains("Invalid input"));
    assertSame(ex, status.getCause());
  }

  @Test
  void handleNotFound_returnsNotFoundStatus() {
    IllegalStateException ex = new IllegalStateException("Not found entity");

    Status status = handler.handleNotFound(ex);

    assertEquals(Status.NOT_FOUND.getCode(), status.getCode());
    assertTrue(status.getDescription().contains("Not found entity"));
    assertSame(ex, status.getCause());
  }

  @Test
  void handleGenericException_returnsInternalStatus() {
    Exception ex = new Exception("Unexpected error");

    Status status = handler.handleGenericException(ex);

    assertEquals(Status.INTERNAL.getCode(), status.getCode());
    assertTrue(status.getDescription().contains("Internal error"));
    assertTrue(status.getDescription().contains("Unexpected error"));
    assertSame(ex, status.getCause());
  }

  @Test
  void handleValidationException_returnsInvalidArgumentStatusWithViolations() {
    Path path1 = mock(Path.class);
    when(path1.toString()).thenReturn("field1");

    ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
    when(violation1.getPropertyPath()).thenReturn(path1);
    when(violation1.getMessage()).thenReturn("must not be blank");

    Path path2 = mock(Path.class);
    when(path2.toString()).thenReturn("field2");

    ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
    when(violation2.getPropertyPath()).thenReturn(path2);
    when(violation2.getMessage()).thenReturn("must be positive");

    ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation1, violation2));

    Status status = handler.handleValidationException(ex);

    assertEquals(Status.INVALID_ARGUMENT.getCode(), status.getCode());
    String desc = status.getDescription();
    assertTrue(desc.contains("Validation errors"));
    assertTrue(desc.contains("field1 must not be blank"));
    assertTrue(desc.contains("field2 must be positive"));
    assertSame(ex, status.getCause());
  }
}
