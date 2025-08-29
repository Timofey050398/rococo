package timofeyqa.rococo.test.rest.gateway.artist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.HttpException;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.jupiter.annotation.meta.RestTest;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.dto.ArtistDto;
import timofeyqa.rococo.service.api.ArtistRestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("REST: Получение артистов по ID")
@RestTest
class RestArtistGetTest {

  private final ArtistRestClient artistClient = new ArtistRestClient();

  @Test
  @DisplayName("Корректное получение артиста по UUID")
  @Content(artistCount = 1)
  void correctfindByIdTest(ContentJson content) {
    ArtistDto expected = content.artists().iterator().next();

    ArtistDto response = artistClient.findById(expected.id().toString());

    assertNotNull(response);
    response.compare(expected);
  }

  @Test
  @DisplayName("NOT_FOUND при запросе артиста по случайному UUID")
  void artistNotFoundTest() {
    UUID randomId = UUID.randomUUID();

    HttpException ex = assertThrows(HttpException.class,
        () -> artistClient.findById(randomId.toString()));

    artistClient.assertError(
        404,
        ex,
        "404",
        "gRPC error",
        "/api/artist/"+randomId,
        String.format("Artist not found: %s", randomId)
    );
  }

  @Test
  @DisplayName("INVALID_ARGUMENT при запросе артиста с некорректным UUID")
  void invalidUuidTest() {
    HttpException ex = assertThrows(HttpException.class,
        () -> artistClient.findById("abc"));

    artistClient.assertError(
        400,
        ex,
        "400",
        "Invalid argument or state",
        "/api/artist/abc",
        "Invalid UUID string: abc"
    );
  }
}
