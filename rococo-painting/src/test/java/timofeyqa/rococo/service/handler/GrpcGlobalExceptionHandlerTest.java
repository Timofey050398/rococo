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
  void testHandleInvalidArgument() {
    IllegalArgumentException ex = new IllegalArgumentException("Invalid ID");
    Status status = handler.handleInvalidArgument(ex);

    assertEquals(Status.INVALID_ARGUMENT.getCode(), status.getCode());
    assertTrue(status.getDescription().contains("Invalid ID"));
    assertEquals(ex, status.getCause());
  }

  @Test
  void testHandleNotFound() {
    IllegalStateException ex = new IllegalStateException("Entity not found");
    Status status = handler.handleNotFound(ex);

    assertEquals(Status.NOT_FOUND.getCode(), status.getCode());
    assertEquals("Entity not found", status.getDescription());
    assertEquals(ex, status.getCause());
  }

  @Test
  void testHandleGenericException() {
    Exception ex = new RuntimeException("Something went wrong");
    Status status = handler.handleGenericException(ex);

    assertEquals(Status.INTERNAL.getCode(), status.getCode());
    assertTrue(status.getDescription().contains("Something went wrong"));
    assertEquals(ex, status.getCause());
  }

  @Test
  void testHandleValidationException() {
    @SuppressWarnings("unchecked")
    ConstraintViolation<Object> violation1 = mock(ConstraintViolation.class);
    ConstraintViolation<Object> violation2 = mock(ConstraintViolation.class);

    Path path1 = mock(Path.class);
    when(path1.toString()).thenReturn("field1");
    when(violation1.getPropertyPath()).thenReturn(path1);
    when(violation1.getMessage()).thenReturn("must not be null");

    Path path2 = mock(Path.class);
    when(path2.toString()).thenReturn("field2");
    when(violation2.getPropertyPath()).thenReturn(path2);
    when(violation2.getMessage()).thenReturn("must be a valid email");

    ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation1, violation2));

    Status status = handler.handleValidationException(ex);

    assertEquals(Status.INVALID_ARGUMENT.getCode(), status.getCode());
    assertTrue(status.getDescription().contains("field1 must not be null"));
    assertTrue(status.getDescription().contains("field2 must be a valid email"));
    assertEquals(ex, status.getCause());
  }

}
