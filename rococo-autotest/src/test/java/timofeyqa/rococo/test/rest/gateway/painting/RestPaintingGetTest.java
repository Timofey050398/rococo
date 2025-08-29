package timofeyqa.rococo.test.rest.gateway.painting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.HttpException;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.jupiter.annotation.Painting;
import timofeyqa.rococo.jupiter.annotation.meta.RestTest;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.dto.PaintingDto;
import timofeyqa.rococo.service.api.PaintingRestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("REST: Получение картин по ID")
@RestTest
class RestPaintingGetTest {

  private final PaintingRestClient paintingClient = new PaintingRestClient();

  @Test
  @DisplayName("Корректное получение картин по UUID")
  @Content(paintings = @Painting(
      museum = "Random",
      content = "img/pages/paintings-list/the-kiss.png"
  ))
  void correctfindByIdTest(ContentJson content) {
    PaintingDto expected = content.paintings().iterator().next();

    PaintingDto response = paintingClient.findById(expected.id().toString());

    assertNotNull(response);
    response.compare(expected);
  }

  @Test
  @DisplayName("NOT_FOUND при запросе картин по случайному UUID")
  void PaintingNotFoundTest() {
    UUID randomId = UUID.randomUUID();

    HttpException ex = assertThrows(HttpException.class,
        () -> paintingClient.findById(randomId.toString()));

    paintingClient.assertError(
        404,
        ex,
        "404",
        "gRPC error",
        "/api/painting/"+randomId,
        String.format("Painting not found: %s", randomId)
    );
  }

  @Test
  @DisplayName("INVALID_ARGUMENT при запросе картин с некорректным UUID")
  void invalidUuidTest() {
    HttpException ex = assertThrows(HttpException.class,
        () -> paintingClient.findById("abc"));

    paintingClient.assertError(
        400,
        ex,
        "400",
        "Invalid argument or state",
        "/api/painting/abc",
        "Invalid UUID string: abc"
    );
  }
}
