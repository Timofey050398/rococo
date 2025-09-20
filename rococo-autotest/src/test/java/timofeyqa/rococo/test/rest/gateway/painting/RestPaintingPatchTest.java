package timofeyqa.rococo.test.rest.gateway.painting;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.HttpException;
import timofeyqa.rococo.jupiter.annotation.*;
import timofeyqa.rococo.jupiter.annotation.meta.RestTest;
import timofeyqa.rococo.jupiter.extension.ApiLoginExtension;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.dto.ArtistDto;
import timofeyqa.rococo.model.dto.MuseumDto;
import timofeyqa.rococo.model.dto.PaintingDto;
import timofeyqa.rococo.service.PaintingClient;
import timofeyqa.rococo.service.api.PaintingRestClient;
import timofeyqa.rococo.service.db.PaintingDbClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static timofeyqa.rococo.jupiter.extension.ApiLoginExtension.rest;
import static timofeyqa.rococo.utils.PhotoConverter.loadImageAsBytes;
import static timofeyqa.rococo.utils.RandomDataUtils.*;

@DisplayName("REST: Частичное обновление картин (PATCH)")
@RestTest
class RestPaintingPatchTest {

  private final PaintingRestClient paintingClient = new PaintingRestClient();
  private final PaintingClient paintingDbClient = new PaintingDbClient();

  @RegisterExtension
  private static final ApiLoginExtension extension = rest();

  @Test
  @User
  @Content(paintingCount = 1, artistCount = 2, museumCount = 1)
  @ApiLogin
  @DisplayName("Успешное частичное обновление картины")
  void updatePaintingSuccessTest(ContentJson content, @Token String token) {
    var painting = content.paintings().iterator().next();

    var newTitle = randomName();
    var newDescription = randomDescription();
    var newContent = randomImage("paintings");

    ArtistDto newArtist = content.artists()
        .stream()
        .filter(a -> !a.id().equals(painting.artist().id()))
        .findFirst()
        .orElseThrow();

    PaintingDto patchRequest = PaintingDto.builder()
        .id(painting.id())
        .title(newTitle)
        .description(newDescription)
        .artist(newArtist)
        .museum(content.museums().iterator().next())
        .content(newContent)
        .build();

    PaintingDto response = paintingClient.updatePainting(patchRequest, "Bearer " + token);

    assertAll(
        () -> assertEquals(painting.id(), response.id()),
        () -> assertEquals(newTitle, response.title()),
        () -> assertEquals(newDescription, response.description()),
        () -> assertEquals(newArtist.id(), response.artist().id()),
        () -> assertArrayEquals(newContent, response.content())
    );

    PaintingDto expected = paintingDbClient.findByTitle(newTitle).orElseThrow();
    expected.compare(response);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("NOT_FOUND при попытке PATCH несуществующей картины")
  void updatePaintingNotFoundTest(@Token String token) {
    UUID id = UUID.randomUUID();
    PaintingDto request = PaintingDto.builder()
        .id(id)
        .title("Ghost painting")
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> paintingClient.updatePainting(request, "Bearer " + token));

    paintingClient.assertError(
        404,
        ex,
        "404",
        "gRPC error",
        "/api/painting",
        "painting not found: " + id
    );
  }

  @Test
  @User
  @Content(paintingCount = 1)
  @ApiLogin
  @DisplayName("Ошибка при слишком длинном названии")
  void updatePaintingTitleTooLongTest(ContentJson content, @Token String token) {
    var painting = content.paintings().iterator().next();

    PaintingDto request = PaintingDto.builder()
        .id(painting.id())
        .title("A".repeat(101))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> paintingClient.updatePainting(request, "Bearer " + token));

    paintingClient.assertError(
        400,
        ex,
        "400",
        "gRPC error",
        "/api/painting",
        "Validation errors: title size must be between 1 and 100; "
    );
  }

  @Test
  @User
  @Content(paintingCount = 1)
  @ApiLogin
  @DisplayName("Ошибка при слишком длинном описании")
  void updatePaintingDescriptionTooLongTest(ContentJson content, @Token String token) {
    var painting = content.paintings().iterator().next();

    PaintingDto request = PaintingDto.builder()
        .id(painting.id())
        .description("B".repeat(1001))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> paintingClient.updatePainting(request, "Bearer " + token));

    paintingClient.assertError(
        400,
        ex,
        "400",
        "Validation error",
        "/api/painting",
        "description: Can`t be longer than 1000 characters"
    );
  }

  @Test
  @User
  @Content(paintingCount = 1, museumCount = 1)
  @ApiLogin
  @DisplayName("Ошибка при несуществующем artistId")
  void updatePaintingArtistNotFoundTest(ContentJson content, @Token String token) {
    var painting = content.paintings().iterator().next();

    var fakeArtist = ArtistDto.builder()
        .id(UUID.randomUUID())
        .name("Ghost Artist")
        .biography("Unknown")
        .build();

    PaintingDto request = PaintingDto.builder()
        .id(painting.id())
        .artist(fakeArtist)
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> paintingClient.updatePainting(request, "Bearer " + token));

    paintingClient.assertError(
        404,
        ex,
        "404",
        "gRPC error",
        "/api/painting",
        "Artist with provided Id not found"
    );
  }

  @Test
  @User
  @Content(paintingCount = 1, artistCount = 1)
  @ApiLogin
  @DisplayName("Ошибка при несуществующем museumId")
  void updatePaintingMuseumNotFoundTest(ContentJson content, @Token String token) {
    var painting = content.paintings().iterator().next();

    var fakeMuseum = MuseumDto.builder()
        .id(UUID.randomUUID())
        .title("Phantom Museum")
        .description("Not exists")
        .build();

    PaintingDto request = PaintingDto.builder()
        .id(painting.id())
        .museum(fakeMuseum)
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> paintingClient.updatePainting(request, "Bearer " + token));

    paintingClient.assertError(
        404,
        ex,
        "404",
        "gRPC error",
        "/api/painting",
        "Museum with provided Id not found"
    );
  }

  @Test
  @User
  @Content(paintingCount = 1, artistCount = 1, museumCount = 1)
  @ApiLogin
  @DisplayName("Ошибка при слишком большом файле")
  void updatePaintingOversizeImageTest(ContentJson content, @Token String token) {
    var painting = content.paintings().iterator().next();

    PaintingDto request = PaintingDto.builder()
        .id(painting.id())
        .content(loadImageAsBytes("img/content/oversize.png"))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> paintingClient.withOversizedContent().updatePainting(request, "Bearer " + token));

    paintingClient.assertError(
        400,
        ex,
        "400",
        "Validation error",
        "/api/painting",
        "content: File size exceeds allowed limit"
    );
  }

  @Test
  @DisplayName("Ошибка при некорректном токене")
  void updatePaintingIncorrectTokenTest() {
    PaintingDto request = PaintingDto.builder()
        .id(UUID.randomUUID())
        .title("Forbidden Update")
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> paintingClient.updatePainting(request, "Bearer invalid-token"));

    paintingClient.assertError(
        401,
        ex,
        "401",
        "Unauthorized",
        "/api/painting",
        "An error occurred while attempting to decode the Jwt: Malformed token"
    );
  }

  @Test
  @User
  @Content(paintings = @Painting(
      museum = "Random",
      content = "screenshots/local/paintings-list/the-kiss.png"
  ))
  @ApiLogin
  @DisplayName("При передаче пустых полей они не обновляют запись")
  void updatePaintingBlankFieldsShouldNotUpdate(ContentJson content, @Token String token) {
    var painting = content.paintings().iterator().next();

    PaintingDto request = PaintingDto.builder()
        .id(painting.id())
        .title(null)
        .description(null)
        .artist(null)
        .museum(null)
        .content(null)
        .build();

    PaintingDto response = paintingClient.updatePainting(request, "Bearer " + token);

    assertEquals(painting.id(), response.id());
    assertFalse(StringUtils.isEmpty(response.title()));
    assertFalse(StringUtils.isEmpty(response.description()));
    assertNotNull(response.artist());
    assertNotNull(response.museum());
    assertNotNull(response.content());
  }
}