package timofeyqa.rococo.test.grpc.artist;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.grpc.rococo.AddArtistRequest;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.mapper.ArtistMapper;
import timofeyqa.rococo.mapper.GrpcMapper;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.dto.ArtistDto;
import timofeyqa.rococo.service.ArtistClient;
import timofeyqa.rococo.service.db.ArtistDbClient;
import timofeyqa.rococo.test.grpc.BaseGrpcTest;

import static org.junit.jupiter.api.Assertions.*;
import static timofeyqa.rococo.utils.RandomDataUtils.*;

@DisplayName("gRPC: Добавление артистов")
class GrpcArtistAddTest extends BaseGrpcTest {

  private final ArtistClient client = new ArtistDbClient();

  @Test
  @DisplayName("Успешное добавление артиста")
  @Content
  void addArtistSuccessTest(ContentJson content) {
    final String name = randomName();
    final byte[] photo = randomImage("artists");
    AddArtistRequest request = AddArtistRequest.newBuilder()
        .setName(name)
        .setBiography(randomDescription())
        .setPhoto(GrpcMapper.INSTANCE.fromByte(photo))
        .build();

    var actual = artistStub.addArtist(request);
    content.artists().add(ArtistMapper.INSTANCE.fromGrpc(actual));
    ArtistDto expected = client.findByName(name)
        .orElseThrow();

    client.compareGrpc(expected, actual);
  }

  @Test
  @DisplayName("Добавление артиста с занятым именем")
  @Content(artistCount = 1)
  void addArtistNameNotUniqueTest(ContentJson content) {
    final var artist = content.artists().iterator().next();
    AddArtistRequest request = AddArtistRequest.newBuilder()
        .setName(artist.name())
        .setBiography(randomDescription())
        .setPhoto(GrpcMapper.INSTANCE.fromByte(randomImage("artists")))
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> artistStub.addArtist(request));

    assertEquals("NOT_FOUND", ex.getStatus().getCode().name());
    assertEquals(String.format("NOT_FOUND: Name already exists: %s",artist.name()), ex.getMessage());
  }

  @Test
  @DisplayName("Ошибка при добавлении артиста без имени")
  void addArtistNameRequiredTest() {
    AddArtistRequest request = AddArtistRequest.newBuilder()
        .setName("")
        .setBiography("Some bio")
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> artistStub.addArtist(request));

    assertEquals("NOT_FOUND", ex.getStatus().getCode().name());
    assertTrue(ex.getMessage().contains("Name required"));
  }

  @Test
  @DisplayName("Ошибка при добавлении артиста без биографии")
  void addArtistBiographyRequiredTest() {
    AddArtistRequest request = AddArtistRequest.newBuilder()
        .setName("Paul Cézanne")
        .setBiography("")
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> artistStub.addArtist(request));

    assertEquals("NOT_FOUND", ex.getStatus().getCode().name());
    assertTrue(ex.getMessage().contains("Biography required"));
  }

  @Test
  @DisplayName("Ошибка при добавлении артиста с слишком длинным именем")
  void addArtistNameTooLongTest() {
    AddArtistRequest request = AddArtistRequest.newBuilder()
        .setName("A".repeat(256))
        .setBiography(randomDescription())
        .setPhoto(GrpcMapper.INSTANCE.fromByte(randomImage("artists")))
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> artistStub.addArtist(request));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
    assertEquals("INVALID_ARGUMENT: Validation errors: name size must be between 0 and 255; ",ex.getMessage(),ex.getMessage());
  }

  @Test
  @DisplayName("Ошибка при добавлении артиста с слишком длинной биографией")
  void addArtistBiographyTooLongTest() {
    String longBio = "B".repeat(2001);
    AddArtistRequest request = AddArtistRequest.newBuilder()
        .setName(randomName())
        .setBiography(longBio)
        .setPhoto(GrpcMapper.INSTANCE.fromByte(randomImage("artists")))
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> artistStub.addArtist(request));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
    assertEquals("INVALID_ARGUMENT: Validation errors: biography size must be between 0 and 2000; ",ex.getMessage(),ex.getMessage());
  }
}
