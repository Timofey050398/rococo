package timofeyqa.rococo.test.grpc.painting;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.grpc.rococo.AddPaintingRequest;
import timofeyqa.grpc.rococo.Painting;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.mapper.GrpcMapper;
import timofeyqa.rococo.mapper.PaintingMapper;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.service.PaintingClient;
import timofeyqa.rococo.service.db.PaintingDbClient;
import timofeyqa.rococo.test.grpc.BaseGrpcTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static timofeyqa.rococo.utils.RandomDataUtils.*;

@DisplayName("gRPC: Добавление картин")
class GrpcPaintingAddTest extends BaseGrpcTest {

  private final PaintingClient client = new PaintingDbClient();

  @Test
  @DisplayName("Успешное добавление картины")
  @Content(
      artistCount = 1,
      museumCount = 1
  )
  void addPaintingSuccessTest(ContentJson content) {
    final String title = randomName();
    final byte[] photo = randomImage("paintings");

    AddPaintingRequest request = AddPaintingRequest.newBuilder()
        .setTitle(title)
        .setDescription(randomDescription())
        .setArtistId(content.artists().iterator().next().id().toString())
        .setMuseumId(content.museums().iterator().next().id().toString())
        .setContent(GrpcMapper.INSTANCE.fromByte(photo))
        .build();

    Painting actual = paintingStub.addPainting(request);
    content.paintings().add(PaintingMapper.INSTANCE.fromGrpc(actual)); // сохраняем в тестовый контент

    var expected = client.findByTitle(title).orElseThrow();
    client.compareGrpc(expected, actual);
  }

  @Test
  @DisplayName("Успешное добавление картины : музей не обязателен")
  @Content(artistCount = 1)
  void addPaintingWithoutMuseumSuccessTest(ContentJson content) {
    final String title = randomName();
    final byte[] photo = randomImage("paintings");

    AddPaintingRequest request = AddPaintingRequest.newBuilder()
        .setTitle(title)
        .setDescription(randomDescription())
        .setArtistId(content.artists().iterator().next().id().toString())
        .setContent(GrpcMapper.INSTANCE.fromByte(photo))
        .build();

    Painting actual = paintingStub.addPainting(request);
    content.paintings().add(PaintingMapper.INSTANCE.fromGrpc(actual));

    var expected = client.findByTitle(title).orElseThrow();
    client.compareGrpc(expected, actual);
  }

  @Test
  @DisplayName("Ошибка при добавлении картины без названия")
  @Content(artistCount = 1)
  void addPaintingTitleRequiredTest(ContentJson content) {
    AddPaintingRequest request = AddPaintingRequest.newBuilder()
        .setTitle("")
        .setDescription("Some description")
        .setArtistId(content.artists().iterator().next().id().toString())
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> paintingStub.addPainting(request));

    assertEquals("NOT_FOUND", ex.getStatus().getCode().name());
    assertTrue(ex.getMessage().contains("Title is required"));
  }

  @Test
  @DisplayName("Ошибка при добавлении картины без artistId")
  void addPaintingArtistRequiredTest() {
    AddPaintingRequest request = AddPaintingRequest.newBuilder()
        .setTitle("Mona Lisa")
        .setDescription("Famous painting")
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> paintingStub.addPainting(request));

    assertEquals("NOT_FOUND", ex.getStatus().getCode().name());
    assertTrue(ex.getMessage().contains("Artist is required"));
  }

  @Test
  @DisplayName("Ошибка при добавлении картины с слишком длинным названием")
  void addPaintingNameTooLongTest() {
    UUID artistId = UUID.randomUUID();
    AddPaintingRequest request = AddPaintingRequest.newBuilder()
        .setTitle("A".repeat(101)) // @Size(max = 100)
        .setDescription(randomDescription())
        .setArtistId(artistId.toString())
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> paintingStub.addPainting(request));

    assertEquals(Status.INVALID_ARGUMENT.getCode(), ex.getStatus().getCode());
    assertTrue(ex.getMessage().contains("title size must be between 1 and 100"));
  }

  @Test
  @DisplayName("Ошибка при добавлении картины с слишком длинным описанием")
  void addPaintingDescriptionTooLongTest() {
    UUID artistId = UUID.randomUUID();
    String longDescription = "B".repeat(1001); // @Size(max = 1000)

    AddPaintingRequest request = AddPaintingRequest.newBuilder()
        .setTitle("The Night Watch")
        .setDescription(longDescription)
        .setArtistId(artistId.toString())
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> paintingStub.addPainting(request));

    assertEquals(Status.INVALID_ARGUMENT.getCode(), ex.getStatus().getCode());
    assertTrue(ex.getMessage().contains("description size must be between 0 and 1000"));
  }

  @Test
  @DisplayName("Добавление картины с несуществующим artistId")
  void addPaintingArtistNotFoundTest() {
    final String title = randomName();

    AddPaintingRequest request = AddPaintingRequest.newBuilder()
        .setTitle(title)
        .setDescription(randomDescription())
        .setArtistId(UUID.randomUUID().toString())
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> paintingStub.addPainting(request));

    assertEquals(Status.NOT_FOUND.getCode(), ex.getStatus().getCode(),ex.getMessage());
    assertEquals("Artist with provided Id not found", ex.getStatus().getDescription());
  }

  @Test
  @DisplayName("Добавление картины с несуществующим museumId")
  @Content(artistCount = 1)
  void addPaintingMuseumNotFoundTest(ContentJson content) {
    final String title = randomName();

    AddPaintingRequest request = AddPaintingRequest.newBuilder()
        .setTitle(title)
        .setDescription(randomDescription())
        .setArtistId(content.artists().iterator().next().id().toString())
        .setMuseumId(UUID.randomUUID().toString())
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> paintingStub.addPainting(request));

    assertEquals(Status.NOT_FOUND.getCode(), ex.getStatus().getCode(),ex.getMessage());
    assertEquals("Museum with provided Id not found", ex.getStatus().getDescription());
  }

}
