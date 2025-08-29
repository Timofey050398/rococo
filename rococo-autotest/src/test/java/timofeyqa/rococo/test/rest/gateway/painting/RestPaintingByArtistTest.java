package timofeyqa.rococo.test.rest.gateway.painting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.HttpException;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.jupiter.annotation.Painting;
import timofeyqa.rococo.jupiter.annotation.meta.RestTest;
import timofeyqa.rococo.jupiter.extension.ApiLoginExtension;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.dto.PaintingDto;
import timofeyqa.rococo.service.api.PaintingRestClient;
import timofeyqa.rococo.service.db.PaintingDbClient;
import timofeyqa.rococo.service.PaintingClient;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static timofeyqa.rococo.jupiter.extension.ApiLoginExtension.rest;

@DisplayName("REST: Получение картин по артисту")
@RestTest
class RestPaintingByArtistTest {

  private final PaintingRestClient paintingClient = new PaintingRestClient();
  private final PaintingClient paintingDbClient = new PaintingDbClient();

  @RegisterExtension
  private static final ApiLoginExtension extension = rest();

  @Test
  @DisplayName("Успешное получение картин артиста")
  @Content(paintings = {
      @Painting(title = "Painting 1", artist = "artistRestTest"),
      @Painting(title = "Painting 2", artist = "artistRestTest"),
      @Painting(title = "Painting 3", artist = "artistRestTest")
  })
  void getPaintingsByArtistSuccessTest(ContentJson content) {
    var artist = content.allArtists().iterator().next();

    var response = paintingClient.getPageByArtist(artist.id().toString(), 0, 10);

    assertFalse(response.getContent().isEmpty());
    assertTrue(response.getTotalElements() >= response.getContent().size());
    response.getContent().forEach(p ->
        assertEquals(artist.id(), p.artist().id())
    );

    List<PaintingDto> expected = paintingDbClient.findAllByArtistId(artist.id());

    assertEquals(expected.size(), response.getTotalElements());
  }

  @Test
  @DisplayName("INVALID_ARGUMENT при некорректном UUID артиста")
  void getPaintingsByArtistIllegalUuidTest() {
    HttpException ex = assertThrows(HttpException.class,
        () -> paintingClient.getPageByArtist("abc",0, 10));

    paintingClient.assertError(
        400,
        ex,
        "400",
        "Invalid argument or state",
        "/api/painting/author/abc",
        "Invalid UUID string: abc"
    );
  }

  @Test
  @DisplayName("Пустой результат, если у артиста нет картин")
  @Content(artistCount = 1)
  void getPaintingsByArtistEmptyResultTest(ContentJson content) {
    var artist = content.artists().iterator().next();

    var response = paintingClient.getPageByArtist(artist.id().toString(),0, 10);

    assertEquals(0, response.getContent().size());
    assertEquals(0, response.getTotalElements());
    assertEquals(0, response.getTotalPages());
  }

  @Test
  @DisplayName("404 при несуществующем артисте")
  void getPaintingsByArtistNotFoundTest() {
    UUID fakeArtistId = UUID.randomUUID();

    HttpException ex = assertThrows(HttpException.class,
        () -> paintingClient.getPageByArtist(fakeArtistId.toString(), 0, 10));

    paintingClient.assertError(
        404,
        ex,
        "404 NOT_FOUND",
        "Not found",
        "/api/painting/author/" + fakeArtistId,
        "Artist not found: " + fakeArtistId
    );
  }

  @Test
  @DisplayName("Ошибка при запросе страницы больше максимально допустимого размера")
  @Content(artistCount = 1)
  void invalidPageSizeTest(ContentJson content) {
    var id = content.artists().iterator().next().id().toString();
    HttpException ex = assertThrows(HttpException.class, () -> paintingClient.getPageByArtist(id, 0, 11));

    paintingClient.assertError(
        400,
        ex,
        "400",
        "Constraint violation",
        "/api/painting/author/" + id,
        "getPaintingByArtist.pageable: Page size can't be greater than 10"
    );
  }
}
