package timofeyqa.rococo.test.grpc.museum;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.mapper.GrpcMapper;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.dto.MuseumDto;
import timofeyqa.rococo.service.MuseumClient;
import timofeyqa.rococo.service.db.MuseumDbClient;
import timofeyqa.rococo.test.grpc.BaseGrpcTest;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("gRPC: Получение музеев")
class GrpcMuseumGetTest extends BaseGrpcTest {

  private final MuseumClient museumClient = new MuseumDbClient();

  @Test
  @DisplayName("Корректное получение музея по UUID")
  @Content(museumCount = 1)
  void correctGetMuseumTest(ContentJson content) {
    var museum = content.museums().iterator().next();
    Uuid request = Uuid.newBuilder().setUuid(museum.id().toString()).build();
    Museum response = museumStub.getMuseum(request);

    assertEquals(museum.id().toString(), response.getId());
    assertEquals(museum.title(), response.getTitle());
  }

  @Test
  @DisplayName("NOT_FOUND при запросе музея по случайному UUID")
  void museumNotFoundTest() {
    Uuid request = Uuid.newBuilder().setUuid(UUID.randomUUID().toString()).build();
    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> museumStub.getMuseum(request));

    assertEquals("NOT_FOUND", ex.getStatus().getCode().name());
  }

  @Test
  @DisplayName("Корректное получение музеев по списку UUID")
  @Content(museumCount = 2)
  void correctGetMuseumsByUuidsTest(ContentJson content) {
    var uuids = content.museums().stream()
        .map(MuseumDto::id)
        .toList();

    UuidList request = GrpcMapper.INSTANCE.toGrpcUuidList(uuids);

    MuseumList response = museumStub.getMuseumsByUuids(request);

    Set<Museum> actual = new HashSet<>(response.getMuseumsList());
    Set<MuseumDto> expected = new HashSet<>(museumClient.findAllById(uuids));

    museumClient.compareGrpcSets(expected, actual);
  }

  @Test
  @DisplayName("INVALID_ARGUMENT при передаче списка UUID с некорректным значением")
  void getMuseumsByUuidsIllegalUuidTest() {
    UuidList request = UuidList.newBuilder()
        .addUuid(Uuid.newBuilder().setUuid("abcdfs"))
        .addUuid(Uuid.newBuilder().setUuid("abcd"))
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> museumStub.getMuseumsByUuids(request));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
  }

  @Test
  @DisplayName("INVALID_ARGUMENT при запросе музея с некорректным UUID")
  void museumByUuidIllegalUuidTest() {
    Uuid request = Uuid.newBuilder().setUuid("abc").build();
    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> museumStub.getMuseum(request));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
  }
}
