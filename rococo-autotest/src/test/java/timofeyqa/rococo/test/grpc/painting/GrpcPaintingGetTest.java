package timofeyqa.rococo.test.grpc.painting;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.test.grpc.BaseGrpcTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("gRPC: Получение картин")
class GrpcPaintingGetTest extends BaseGrpcTest {

  @Test
  @DisplayName("Корректное получение картины по UUID")
  @Content(paintingCount = 1)
  void correctGetPaintingTest(ContentJson content) {
    var painting = content.paintings().iterator().next();
    Uuid request = Uuid.newBuilder().setUuid(painting.id().toString()).build();
    Painting response = paintingStub.getPainting(request);

    assertEquals(painting.id().toString(), response.getId());
    assertEquals(painting.title(), response.getTitle());
  }

  @Test
  @DisplayName("NOT_FOUND при запросе картины по случайному UUID")
  void paintingNotFoundTest() {
    Uuid request = Uuid.newBuilder().setUuid(UUID.randomUUID().toString()).build();
    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> paintingStub.getPainting(request));

    assertEquals("NOT_FOUND", ex.getStatus().getCode().name());
  }

  @Test
  @DisplayName("INVALID_ARGUMENT при запросе картины с некорректным UUID")
  void paintingByUuidIllegalUuidTest() {
    Uuid request = Uuid.newBuilder().setUuid("abc").build();
    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> paintingStub.getPainting(request));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
  }
}
