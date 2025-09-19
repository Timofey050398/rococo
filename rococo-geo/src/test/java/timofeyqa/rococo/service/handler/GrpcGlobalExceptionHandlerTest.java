package timofeyqa.rococo.service.handler;

import io.grpc.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GrpcGlobalExceptionHandlerTest {

  private final GrpcGlobalExceptionHandler handler = new GrpcGlobalExceptionHandler();

  @Test
  void handleInvalidArgument_returnsInvalidArgumentStatus() {
    IllegalArgumentException e = new IllegalArgumentException("Invalid input");
    Status status = handler.handleInvalidArgument(e);
    assertEquals(Status.Code.INVALID_ARGUMENT, status.getCode());
    assertContains(status.getDescription(),"Invalid input");
    assertEquals(e, status.getCause());
  }

  @Test
  void handleNotFound_withIllegalStateException_returnsNotFoundStatus() {
    IllegalStateException e = new IllegalStateException("Not found");
    Status status = handler.handleNotFound(e);
    assertEquals(Status.Code.NOT_FOUND, status.getCode());
    assertContains(status.getDescription(),"Not found");
    assertEquals(e, status.getCause());
  }

  @Test
  void handleNotFound_withEntityNotFoundException_returnsNotFoundStatus() {
    Status status = handler.handleNotFound(new IllegalStateException("Entity missing"));
    assertEquals(Status.Code.NOT_FOUND, status.getCode());
    assertContains(status.getDescription(),"Entity missing");
    assertEquals("Entity missing", status.getDescription());
  }

  @Test
  void handleGenericException_returnsInternalStatus() {
    Exception e = new Exception("Unexpected");
    Status status = handler.handleGenericException(e);
    assertEquals(Status.Code.INTERNAL, status.getCode());
    assertContains(status.getDescription(),"Unexpected");
    assertEquals(e, status.getCause());
  }

  private void assertContains(String actual, String expected){
    assertNotNull(actual);
    assertTrue(actual.contains(expected));
  }
}

