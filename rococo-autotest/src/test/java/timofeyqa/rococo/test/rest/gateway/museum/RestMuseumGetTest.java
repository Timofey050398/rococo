package timofeyqa.rococo.test.rest.gateway.museum;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.HttpException;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.jupiter.annotation.meta.RestTest;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.dto.MuseumDto;
import timofeyqa.rococo.service.api.MuseumRestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("REST: Получение музеев по ID")
@RestTest
class RestMuseumGetTest {

  private final MuseumRestClient museumClient = new MuseumRestClient();

  @Test
  @DisplayName("Корректное получение музея по UUID")
  @Content(museumCount = 1)
  void correctfindByIdTest(ContentJson content) {
    MuseumDto expected = content.museums().iterator().next();

    MuseumDto response = museumClient.findById(expected.id().toString());

    assertNotNull(response);
    response.compare(expected);
  }

  @Test
  @DisplayName("NOT_FOUND при запросе музея по случайному UUID")
  void MuseumNotFoundTest() {
    UUID randomId = UUID.randomUUID();

    HttpException ex = assertThrows(HttpException.class,
        () -> museumClient.findById(randomId.toString()));

    museumClient.assertError(
        404,
        ex,
        "404",
        "gRPC error",
        "/api/museum/"+randomId,
        String.format("Museum not found: %s", randomId)
    );
  }

  @Test
  @DisplayName("INVALID_ARGUMENT при запросе музея с некорректным UUID")
  void invalidUuidTest() {
    HttpException ex = assertThrows(HttpException.class,
        () -> museumClient.findById("abc"));

    museumClient.assertError(
        400,
        ex,
        "400",
        "Invalid argument or state",
        "/api/museum/abc",
        "Invalid UUID string: abc"
    );
  }
}
