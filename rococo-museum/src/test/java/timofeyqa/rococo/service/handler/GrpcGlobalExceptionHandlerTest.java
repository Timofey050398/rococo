package timofeyqa.rococo.service.handler;

import io.grpc.Status;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GrpcGlobalExceptionHandlerTest {

  private GrpcGlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GrpcGlobalExceptionHandler();
  }

  @Test
  void handleInvalidArgument_returnsInvalidArgumentStatus() {
    IllegalArgumentException ex = new IllegalArgumentException("Bad argument");
    Status status = handler.handleInvalidArgument(ex);

    assertEquals(Status.Code.INVALID_ARGUMENT, status.getCode());
    assertTrue(status.getDescription().contains("Bad argument"));
    assertEquals(ex, status.getCause());
  }

  @Test
  void handleNotFound_returnsNotFoundStatus() {
    IllegalStateException ex = new IllegalStateException("Entity not found");
    Status status = handler.handleNotFound(ex);

    assertEquals(Status.Code.NOT_FOUND, status.getCode());
    assertTrue(status.getDescription().contains("Entity not found"));
    assertEquals(ex, status.getCause());
  }

  @Test
  void handleGenericException_returnsInternalStatus() {
    Exception ex = new Exception("Something went wrong");
    Status status = handler.handleGenericException(ex);

    assertEquals(Status.Code.INTERNAL, status.getCode());
    assertTrue(status.getDescription().contains("Internal error"));
    assertTrue(status.getDescription().contains("Something went wrong"));
    assertEquals(ex, status.getCause());
  }

  @Test
  void handleValidationException_returnsInvalidArgumentWithMessages() {
    ConstraintViolationException ex = new ConstraintViolationException(Set.of(
        new FakeConstraintViolation("field1", "must not be null"),
        new FakeConstraintViolation("field2", "size must be between 1 and 10")
    ));

    Status status = handler.handleValidationException(ex);

    assertEquals(Status.Code.INVALID_ARGUMENT, status.getCode());
    String desc = status.getDescription();
    assertTrue(desc.contains("field1 must not be null"));
    assertTrue(desc.contains("field2 size must be between 1 and 10"));
    assertEquals(ex, status.getCause());
  }

  private static class FakeConstraintViolation implements ConstraintViolation<Object> {

    private final String path;
    private final String message;

    FakeConstraintViolation(String path, String message) {
      this.path = path;
      this.message = message;
    }

    @Override
    public String getMessage() {
      return message;
    }

    @Override
    public String getMessageTemplate() {
      return message;
    }

    @Override
    public Object getRootBean() {
      return null;
    }

    @Override
    public Class<Object> getRootBeanClass() {
      return null;
    }

    @Override
    public Object getLeafBean() {
      return null;
    }

    @Override
    public Object[] getExecutableParameters() {
      return new Object[0];
    }

    @Override
    public Object getExecutableReturnValue() {
      return null;
    }

    @Override
    public Path getPropertyPath() {
      return new Path() {
        @Override
        public Iterator<Node> iterator() {
          return null;
        }

        @Override
        public String toString() {
          return path;
        }
      };
    }

    @Override
    public Object getInvalidValue() {
      return null;
    }

    @Override
    public ConstraintDescriptor<?> getConstraintDescriptor() {
      return null;
    }

    @Override
    public <U> U unwrap(Class<U> type) {
      return null;
    }
  }
}
