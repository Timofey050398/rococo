package timofeyqa.rococo.test.rest.gateway.painting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.HttpException;
import timofeyqa.rococo.jupiter.annotation.ApiLogin;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.jupiter.annotation.Token;
import timofeyqa.rococo.jupiter.annotation.User;
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

@DisplayName("REST: Добавление картин")
@RestTest
class RestPaintingPostTest {

  private final PaintingRestClient paintingClient = new PaintingRestClient();
  private final PaintingClient paintingDbClient = new PaintingDbClient();

  @RegisterExtension
  private static final ApiLoginExtension extension = rest();

  @Test
  @User
  @Content(artistCount = 1, museumCount = 1)
  @ApiLogin
  @DisplayName("Успешное добавление картины")
  void addPaintingSuccessTest(ContentJson content, @Token String token) {
    String title = randomName();
    byte[] image = randomImage("paintings");

    PaintingDto request = PaintingDto.builder()
        .title(title)
        .description(randomDescription())
        .artist(content.artists().iterator().next())
        .museum(content.museums().iterator().next())
        .content(image)
        .build();

    PaintingDto response = paintingClient.createPainting(request, "Bearer " + token);
    content.paintings().add(response);

    assertNotNull(response);
    assertEquals(title, response.title());

    PaintingDto expected = paintingDbClient.findByTitle(title).orElseThrow();
    expected.compare(response);
  }

  @Test
  @User
  @Content(artistCount = 1)
  @ApiLogin
  @DisplayName("Успешное добавление картины без музея")
  void addPaintingWithoutMuseumSuccessTest(ContentJson content, @Token String token) {
    String title = randomName();

    PaintingDto request = PaintingDto.builder()
        .title(title)
        .description(randomDescription())
        .artist(content.artists().iterator().next())
        .content(randomImage("paintings"))
        .build();

    PaintingDto response = paintingClient.createPainting(request, "Bearer " + token);
    content.paintings().add(response);

    assertNotNull(response);
    assertEquals(title, response.title());

    PaintingDto expected = paintingDbClient.findByTitle(title).orElseThrow();
    expected.compare(response);
  }

  @Test
  @User
  @Content(artistCount = 1)
  @ApiLogin
  @DisplayName("Ошибка при добавлении картины без названия")
  void addPaintingTitleRequiredTest(ContentJson content, @Token String token) {
    PaintingDto request = PaintingDto.builder()
        .title("")
        .description("Some description")
        .artist(content.artists().iterator().next())
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> paintingClient.createPainting(request, "Bearer " + token));

    paintingClient.assertError(
        404,
        ex,
        "404",
        "gRPC error",
        "/api/painting",
        "Title is required"
    );
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Ошибка при добавлении картины без artist")
  void addPaintingArtistRequiredTest(@Token String token) {
    PaintingDto request = PaintingDto.builder()
        .title("Mona Lisa")
        .description("Famous painting")
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> paintingClient.createPainting(request, "Bearer " + token));

    paintingClient.assertError(
        404,
        ex,
        "404",
        "gRPC error",
        "/api/painting",
        "Artist is required"
    );
  }

  @Test
  @User
  @Content(artistCount = 1)
  @ApiLogin
  @DisplayName("Ошибка при добавлении картины с слишком длинным названием")
  void addPaintingTitleTooLongTest(ContentJson content, @Token String token) {
    PaintingDto request = PaintingDto.builder()
        .title("A".repeat(101))
        .description(randomDescription())
        .artist(content.artists().iterator().next())
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> paintingClient.createPainting(request, "Bearer " + token));

    paintingClient.assertError(
        400,
        ex,
        "400",
        "Validation error",
        "/api/painting",
        "title: Title can`t be longer than 100 characters"
    );
  }

  @Test
  @User
  @Content(artistCount = 1)
  @ApiLogin
  @DisplayName("Ошибка при добавлении картины с слишком длинным описанием")
  void addPaintingDescriptionTooLongTest(ContentJson content, @Token String token) {
    String longDescription = "B".repeat(1001);

    PaintingDto request = PaintingDto.builder()
        .title("The Night Watch")
        .description(longDescription)
        .artist(content.artists().iterator().next())
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> paintingClient.createPainting(request, "Bearer " + token));

    paintingClient.assertError(
        400,
        ex,
        "400",
        "Validation error",
        "/api/painting",
        "description: Description can`t be longer than 1000 characters"
    );
  }

  @Test
  @User
  @Content(museumCount = 1)
  @ApiLogin
  @DisplayName("Добавление картины с несуществующим artistId")
  void addPaintingArtistNotFoundTest(ContentJson content, @Token String token) {
    var fakeArtist = ArtistDto.builder()
        .id(UUID.randomUUID())
        .name("Ghost Artist")
        .biography("Non existing")
        .build();

    PaintingDto request = PaintingDto.builder()
        .title(randomName())
        .description(randomDescription())
        .artist(fakeArtist)
        .museum(content.museums().iterator().next())
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> paintingClient.createPainting(request, "Bearer " + token));

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
  @Content(artistCount = 1)
  @ApiLogin
  @DisplayName("Добавление картины с несуществующим museumId")
  void addPaintingMuseumNotFoundTest(ContentJson content, @Token String token) {
    var fakeMuseum = MuseumDto.builder()
        .id(UUID.randomUUID())
        .title("Phantom Museum")
        .description("Does not exist")
        .build();

    PaintingDto request = PaintingDto.builder()
        .title(randomName())
        .description(randomDescription())
        .artist(content.artists().iterator().next())
        .museum(fakeMuseum)
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> paintingClient.createPainting(request, "Bearer " + token));

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
  @DisplayName("Ошибка при добавлении картины с некорректным токеном")
  void addPaintingIncorrectTokenTest() {
    PaintingDto request = PaintingDto.builder()
        .title(randomName())
        .description(randomDescription())
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> paintingClient.createPainting(request, "Bearer invalid-token"));

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
  @Content(artistCount = 1, museumCount = 1)
  @ApiLogin
  @DisplayName("Ошибка при добавлении картины с слишком большим файлом")
  void addPaintingOversizeImageTest(ContentJson content, @Token String token) {
    PaintingDto request = PaintingDto.builder()
        .title(randomName())
        .description(randomDescription())
        .artist(content.artists().iterator().next())
        .museum(content.museums().iterator().next())
        .content(loadImageAsBytes("img/content/oversize.png"))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> paintingClient.createPainting(request, "Bearer " + token));

    paintingClient.assertError(
        400,
        ex,
        "400",
        "Validation error",
        "/api/painting",
        "content: File size exceeds allowed limit"
    );
  }
}