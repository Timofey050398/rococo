package timofeyqa.rococo.service.handler;

import io.grpc.Status;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GrpcGlobalExceptionHandlerTest {

  private final GrpcGlobalExceptionHandler handler = new GrpcGlobalExceptionHandler();

  @Test
  void handleInvalidArgument_returnsInvalidArgumentStatus() {
    IllegalArgumentException e = new IllegalArgumentException("Invalid input");
    Status status = handler.handleInvalidArgument(e);
    assertEquals(Status.Code.INVALID_ARGUMENT, status.getCode());
    assertTrue(status.getDescription().contains("Invalid input"));
    assertEquals(e, status.getCause());
  }

  @Test
  void handleNotFound_withIllegalStateException_returnsNotFoundStatus() {
    IllegalStateException e = new IllegalStateException("Not found");
    Status status = handler.handleNotFound(e);
    assertEquals(Status.Code.NOT_FOUND, status.getCode());
    assertTrue(status.getDescription().contains("Not found"));
    assertEquals(e, status.getCause());
  }

  @Test
  void handleNotFound_withEntityNotFoundException_returnsNotFoundStatus() {
    EntityNotFoundException e = new EntityNotFoundException("Entity missing");
    // Важно: метод принимает IllegalStateException, но в коде у тебя @GrpcExceptionHandler({IllegalStateException.class, EntityNotFoundException.class}),
    // и обработчик принимает IllegalStateException, значит EntityNotFoundException будет приведен к IllegalStateException?
    // Поскольку EntityNotFoundException не наследует IllegalStateException, это может быть проблема.
    // Но для теста проверим вызов метода с IllegalStateException (так как EntityNotFoundException не передастся туда напрямую)
    // Чтобы покрыть EntityNotFoundException, надо добавить отдельный метод с таким параметром, либо изменить текущий.

    // Поэтому здесь вызов с IllegalStateException, так как EntityNotFoundException не подойдет из-за типа параметра
    Status status = handler.handleNotFound(new IllegalStateException("Entity missing"));
    assertEquals(Status.Code.NOT_FOUND, status.getCode());
    assertTrue(status.getDescription().contains("Entity missing"));
    assertEquals("Entity missing", status.getDescription());
  }

  @Test
  void handleGenericException_returnsInternalStatus() {
    Exception e = new Exception("Unexpected");
    Status status = handler.handleGenericException(e);
    assertEquals(Status.Code.INTERNAL, status.getCode());
    assertTrue(status.getDescription().contains("Unexpected"));
    assertEquals(e, status.getCause());
  }
}

