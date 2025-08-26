package timofeyqa.rococo.test.grpc.artist;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.mapper.GrpcMapper;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.dto.ArtistDto;
import timofeyqa.rococo.service.ArtistClient;
import timofeyqa.rococo.service.db.ArtistDbClient;
import timofeyqa.rococo.test.grpc.BaseGrpcTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("gRPC: Получение артистов")
class GrpcArtistGetTest extends BaseGrpcTest {

  private final ArtistClient artistClient = new ArtistDbClient();

  @Test
  @DisplayName("Корректное получение артиста по UUID")
  @Content(artistCount = 1)
  void correctGetArtistTest(ContentJson content) {
    var artist = content.artists().iterator().next();
    Uuid request = Uuid.newBuilder().setUuid(artist.id().toString()).build();
    Artist response = artistStub.getArtist(request);

    assertEquals(artist.id().toString(), response.getId());
    assertEquals(artist.name(), response.getName());
  }

  @Test
  @DisplayName("NOT_FOUND при запросе артиста по случайному UUID")
  void artistNotFoundTest() {
    Uuid request = Uuid.newBuilder().setUuid(UUID.randomUUID().toString()).build();
    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> artistStub.getArtist(request));

    assertEquals("NOT_FOUND", ex.getStatus().getCode().name());
  }

  @Test
  @DisplayName("Корректное получение артистов по списку UUID")
  @Content(artistCount = 2)
  void correctGetArtistsByUuidsTest(ContentJson content) {
    var uuids = content.artists().stream()
        .map(ArtistDto::id)
        .toList();

    UuidList request = GrpcMapper.INSTANCE.toGrpcUuidList(uuids);

    ArtistList response = artistStub.getArtistsByUuids(request);

    var actual = new HashSet<>(response.getArtistsList());

    var expected = new HashSet<>(artistClient.findAllById(uuids));

    artistClient.compareGrpcSets(expected,actual);
  }

  @Test
  @DisplayName("INVALID_ARGUMENT при передаче списка UUID с некорректным значением")
  void getGeosByUuidsIllegalUuidTest() {
    UuidList request = UuidList.newBuilder()
        .addUuid(Uuid.newBuilder().setUuid("abcdfs"))
        .addUuid(Uuid.newBuilder().setUuid("abcd"))
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> artistStub.getArtistsByUuids(request));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
  }

  @Test
  @DisplayName("INVALID_ARGUMENT при запросе артиста с некорректным UUID")
  void geoByUuidIllegalUuidTest() {
    Uuid request = Uuid.newBuilder().setUuid("abc").build();
    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> artistStub.getArtist(request));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
  }
}
