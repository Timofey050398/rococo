package timofeyqa.rococo.test.grpc.painting;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.grpc.rococo.Painting;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.mapper.GrpcMapper;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.dto.ArtistDto;
import timofeyqa.rococo.model.dto.MuseumDto;
import timofeyqa.rococo.model.dto.PaintingDto;
import timofeyqa.rococo.service.PaintingClient;
import timofeyqa.rococo.service.ArtistClient;
import timofeyqa.rococo.service.MuseumClient;
import timofeyqa.rococo.service.db.PaintingDbClient;
import timofeyqa.rococo.service.db.ArtistDbClient;
import timofeyqa.rococo.service.db.MuseumDbClient;
import timofeyqa.rococo.test.grpc.BaseGrpcTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static timofeyqa.rococo.utils.RandomDataUtils.*;

@DisplayName("gRPC: Обновление картин")
class GrpcPaintingUpdateTest extends BaseGrpcTest {

  private final PaintingClient paintingClient = new PaintingDbClient();

  @Test
  @DisplayName("Успешное обновление картины")
  @Content(paintingCount = 1, museumCount = 1, artistCount = 2)
  void updatePaintingSuccessTest(ContentJson content) {
    var painting = content.paintings().iterator().next();

    var newContent = GrpcMapper.INSTANCE.fromByte(new byte[]{10, 20, 30});
    String newTitle = randomName();
    String newDescription = randomDescription();

    UUID artistId = content.artists()
        .stream()
        .map(ArtistDto::id)
        .filter(id -> !id.equals(painting.artist().id()))
        .findFirst()
        .orElseThrow();

    UUID museumId = content.museums()
        .stream()
        .map(MuseumDto::id)
        .findFirst()
        .orElseThrow();

    var updated = paintingStub.updatePainting(
        Painting.newBuilder()
            .setId(painting.id().toString())
            .setTitle(newTitle)
            .setDescription(newDescription)
            .setContent(newContent)
            .setArtistId(artistId.toString())
            .setMuseumId(museumId.toString())
            .build()
    );

    assertAll(() -> {
      paintingClient.compareGrpc(
          paintingClient.findAllById(List.of(painting.id())).getFirst(),
          updated
      );
      assertEquals(newTitle, updated.getTitle());
      assertEquals(newDescription, updated.getDescription());
      assertEquals(newContent, updated.getContent());
      assertEquals(artistId.toString(), updated.getArtistId());
      assertEquals(museumId.toString(), updated.getMuseumId());
    });
  }

  @Test
  @DisplayName("NOT_FOUND при обновлении несуществующей картины")
  void updatePaintingNotFoundTest() {
    Painting request = Painting.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setTitle("Ghost Painting")
        .setDescription("Invisible art")
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> paintingStub.updatePainting(request));

    assertEquals(Status.NOT_FOUND.getCode(), ex.getStatus().getCode());
  }

  @Test
  @DisplayName("Ошибка при обновлении с несуществующим artistId")
  @Content(paintingCount = 1)
  void updatePaintingArtistNotFoundTest(ContentJson content) {
    Painting request = Painting.newBuilder()
        .setId(content.paintings().iterator().next().id().toString())
        .setArtistId(UUID.randomUUID().toString())
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> paintingStub.updatePainting(request));

    assertEquals(Status.NOT_FOUND.getCode(), ex.getStatus().getCode(),ex.getMessage());
    assertEquals("Artist with provided Id not found", ex.getStatus().getDescription());
  }

  @Test
  @DisplayName("Ошибка при обновлении с несуществующим museumId")
  @Content(paintingCount = 1)
  void updatePaintingMuseumNotFoundTest(ContentJson content) {
    Painting request = Painting.newBuilder()
        .setId(content.paintings().iterator().next().id().toString())
        .setMuseumId(UUID.randomUUID().toString())
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> paintingStub.updatePainting(request));

    assertEquals(Status.NOT_FOUND.getCode(), ex.getStatus().getCode(),ex.getMessage());
    assertEquals("Museum with provided Id not found", ex.getStatus().getDescription());
  }

  @Test
  @DisplayName("Ошибка при неверном формате artistId")
  @Content(paintingCount = 1)
  void updatePaintingArtistIdNotUuidTest(ContentJson content) {
    Painting request = Painting.newBuilder()
        .setId(content.paintings().iterator().next().id().toString())
        .setArtistId("abc")
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> paintingStub.updatePainting(request));

    assertEquals(Status.INVALID_ARGUMENT.getCode(), ex.getStatus().getCode());
  }

  @Test
  @DisplayName("Ошибка при неверном формате museumId")
  @Content(paintingCount = 1)
  void updatePaintingMuseumIdNotUuidTest(ContentJson content) {
    Painting request = Painting.newBuilder()
        .setId(content.paintings().iterator().next().id().toString())
        .setMuseumId("abc")
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> paintingStub.updatePainting(request));

    assertEquals(Status.INVALID_ARGUMENT.getCode(), ex.getStatus().getCode());
  }

  @Test
  @DisplayName("Ошибка при слишком длинном названии")
  @Content(paintingCount = 1)
  void updatePaintingTitleTooLongTest(ContentJson content) {
    Painting request = Painting.newBuilder()
        .setId(content.paintings().iterator().next().id().toString())
        .setTitle("A".repeat(101))
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> paintingStub.updatePainting(request));

    assertEquals(Status.INVALID_ARGUMENT.getCode(), ex.getStatus().getCode());
    assertEquals("INVALID_ARGUMENT: Validation errors: title size must be between 1 and 100; ", ex.getMessage());
  }

  @Test
  @DisplayName("Ошибка при слишком длинном описании")
  @Content(paintingCount = 1)
  void updatePaintingDescriptionTooLongTest(ContentJson content) {
    Painting request = Painting.newBuilder()
        .setId(content.paintings().iterator().next().id().toString())
        .setDescription("B".repeat(1001))
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> paintingStub.updatePainting(request));

    assertEquals(Status.INVALID_ARGUMENT.getCode(), ex.getStatus().getCode());
    assertTrue(ex.getMessage().contains("description size must be between 0 and 1000"));
  }

  @Test
  @DisplayName("При передаче пустых полей, поля не обновляются")
  @Content(paintings = @timofeyqa.rococo.jupiter.annotation.Painting(
      museum = "Random",
      content = "img/pages/paintings-list/the-kiss.png"
  ))
  void updatePaintingBlankFieldsShouldNotUpdated(ContentJson content) {
    var painting = content.paintings().iterator().next();

    assertAll(() -> {
      assertFalse(StringUtils.isEmpty(painting.title()), "painting title should not be empty");
      assertFalse(StringUtils.isEmpty(painting.description()), "painting description should not be empty");
      assertNotNull(painting.artist().id(), "painting artist should not be null");
      assertNotNull(painting.museum().id(), "painting museum should not be null");
      assertFalse(ArrayUtils.isEmpty(painting.content()), "painting content should not be empty");
    });

    Painting request = Painting.newBuilder()
        .setId(painting.id().toString())
        .setTitle("")
        .setDescription("")
        .setArtistId("")
        .setMuseumId("")
        .setContent(ByteString.EMPTY)
        .build();

    var response = paintingStub.updatePainting(request);

    assertAll(() -> {
      assertEquals(request.getId(), response.getId(),"painting id should equal request id");
      assertFalse(StringUtils.isEmpty(response.getTitle()), "painting title should not be empty");
      assertFalse(StringUtils.isEmpty(response.getDescription()), "painting description should not be empty");
      assertFalse(StringUtils.isEmpty(response.getArtistId()), "painting artist should not be empty");
      assertFalse(StringUtils.isEmpty(response.getMuseumId()), "painting museum should not be empty");
      assertFalse(response.getContent().isEmpty(), "painting content should not be empty");
    });
  }
}