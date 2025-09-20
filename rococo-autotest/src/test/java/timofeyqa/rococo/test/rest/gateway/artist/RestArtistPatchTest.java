package timofeyqa.rococo.test.rest.gateway.artist;

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
import timofeyqa.rococo.service.ArtistClient;
import timofeyqa.rococo.service.api.ArtistRestClient;
import timofeyqa.rococo.service.db.ArtistDbClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static timofeyqa.rococo.jupiter.extension.ApiLoginExtension.rest;
import static timofeyqa.rococo.utils.PhotoConverter.loadImageAsBytes;
import static timofeyqa.rococo.utils.RandomDataUtils.*;

@DisplayName("REST: Частичное обновление артистов (PATCH)")
@RestTest
class RestArtistPatchTest {

  private final ArtistRestClient artistClient = new ArtistRestClient();
  private final ArtistClient artistDbClient = new ArtistDbClient();

  @RegisterExtension
  private static final ApiLoginExtension extension = rest();

  @Test
  @User
  @Content(artistCount = 1)
  @ApiLogin
  @DisplayName("Успешное частичное обновление артиста")
  void updateArtistSuccessTest(ContentJson content, @Token String token) {
    var artist = content.artists().iterator().next();

    var newName = randomName();
    var newBiography = randomDescription();
    var newPhoto = randomImage("artists");

    ArtistDto request = ArtistDto.builder()
        .id(artist.id())
        .name(newName)
        .biography(newBiography)
        .photo(newPhoto)
        .build();

    ArtistDto response = artistClient.updateArtist(request, "Bearer " + token);

    assertAll(
        () -> assertEquals(artist.id(), response.id()),
        () -> assertEquals(newName, response.name()),
        () -> assertEquals(newBiography, response.biography()),
        () -> assertArrayEquals(newPhoto, response.photo())
    );

    ArtistDto expected = artistDbClient.findByName(newName).orElseThrow();
    expected.compare(response);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("NOT_FOUND при PATCH несуществующего артиста")
  void updateArtistNotFoundTest(@Token String token) {
    UUID id = UUID.randomUUID();

    ArtistDto request = ArtistDto.builder()
        .id(id)
        .name("Ghost Artist")
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> artistClient.updateArtist(request, "Bearer " + token));

    artistClient.assertError(
        404,
        ex,
        "404",
        "gRPC error",
        "/api/artist",
        "Artist not found: " + id
    );
  }

  @Test
  @User
  @Content(artistCount = 2)
  @ApiLogin
  @DisplayName("Ошибка при обновлении: дубликат имени")
  void updateArtistDuplicateNameTest(ContentJson content, @Token String token) {
    var it = content.artists().iterator();
    ArtistDto first = it.next();
    ArtistDto second = it.next();

    ArtistDto request = ArtistDto.builder()
        .id(second.id())
        .name(first.name())
        .biography("New bio")
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> artistClient.updateArtist(request, "Bearer " + token));

    artistClient.assertError(
        404,
        ex,
        "404",
        "gRPC error",
        "/api/artist",
        "Name already exists: " + first.name()
    );
  }

  @Test
  @User
  @Content(artistCount = 1)
  @ApiLogin
  @DisplayName("Ошибка при слишком длинном имени")
  void updateArtistNameTooLongTest(ContentJson content, @Token String token) {
    var artist = content.artists().iterator().next();

    ArtistDto request = ArtistDto.builder()
        .id(artist.id())
        .name("A".repeat(256))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> artistClient.updateArtist(request, "Bearer " + token));

    artistClient.assertError(
        400,
        ex,
        "400",
        "Validation error",
        "/api/artist",
        "name: Can`t be longer than 255 characters"
    );
  }

  @Test
  @User
  @Content(artistCount = 1)
  @ApiLogin
  @DisplayName("Ошибка при слишком длинной биографии")
  void updateArtistBiographyTooLongTest(ContentJson content, @Token String token) {
    var artist = content.artists().iterator().next();

    ArtistDto request = ArtistDto.builder()
        .id(artist.id())
        .biography("B".repeat(2001))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> artistClient.updateArtist(request, "Bearer " + token));

    artistClient.assertError(
        400,
        ex,
        "400",
        "Validation error",
        "/api/artist",
        "biography: Can`t be longer than 2000 characters"
    );
  }

  @Test
  @User
  @Content(artistCount = 1)
  @ApiLogin
  @DisplayName("Ошибка при слишком большом файле")
  void updateArtistOversizeImageTest(ContentJson content, @Token String token) {
    var artist = content.artists().iterator().next();

    ArtistDto request = ArtistDto.builder()
        .id(artist.id())
        .photo(loadImageAsBytes("img/content/oversize.png"))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> artistClient.withOversizedContent().updateArtist(request, "Bearer " + token));

    artistClient.assertError(
        400,
        ex,
        "400",
        "Validation error",
        "/api/artist",
        "photo: File size exceeds allowed limit"
    );
  }

  @Test
  @DisplayName("Ошибка при некорректном токене")
  void updateArtistIncorrectTokenTest() {
    ArtistDto request = ArtistDto.builder()
        .id(UUID.randomUUID())
        .name("Forbidden Update")
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> artistClient.updateArtist(request, "Bearer invalid-token"));

    artistClient.assertError(
        401,
        ex,
        "401",
        "Unauthorized",
        "/api/artist",
        "An error occurred while attempting to decode the Jwt: Malformed token"
    );
  }

  @Test
  @User
  @Content(artists = @Artist(
      name = "Claude Monet",
      biography = "Impressionist painter",
      photo = "screenshots/local/artists-list/dali.png"
  ))
  @ApiLogin
  @DisplayName("При передаче пустых полей они не обновляют запись")
  void updateArtistBlankFieldsShouldNotUpdate(ContentJson content, @Token String token) {
    var artist = content.artists().iterator().next();

    ArtistDto request = ArtistDto.builder()
        .id(artist.id())
        .name(null)
        .biography(null)
        .photo(null)
        .build();

    ArtistDto response = artistClient.updateArtist(request, "Bearer " + token);

    assertEquals(artist.id(), response.id());
    assertFalse(StringUtils.isEmpty(response.name()));
    assertFalse(StringUtils.isEmpty(response.biography()));
    assertNotNull(response.photo());
  }
}