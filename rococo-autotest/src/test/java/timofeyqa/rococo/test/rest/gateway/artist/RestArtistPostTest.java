package timofeyqa.rococo.test.rest.gateway.artist;

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
import timofeyqa.rococo.service.ArtistClient;
import timofeyqa.rococo.service.api.ArtistRestClient;
import timofeyqa.rococo.service.db.ArtistDbClient;

import static org.junit.jupiter.api.Assertions.*;
import static timofeyqa.rococo.jupiter.extension.ApiLoginExtension.rest;
import static timofeyqa.rococo.utils.PhotoConverter.loadImageAsBytes;
import static timofeyqa.rococo.utils.RandomDataUtils.*;

@DisplayName("REST: Добавление артистов")
@RestTest
class RestArtistPostTest {

  private final ArtistRestClient artistClient = new ArtistRestClient();
  private final ArtistClient artistDbClient = new ArtistDbClient();

  @RegisterExtension
  private static final ApiLoginExtension extension = rest();

  @Test
  @DisplayName("Успешное добавление артиста")
  @User
  @Content
  @ApiLogin
  void addArtistSuccessTest(ContentJson content, @Token String token) {
    final String name = randomName();
    final byte[] photo = randomImage("artists");

    ArtistDto request = ArtistDto.builder()
        .name(name)
        .biography(randomDescription())
        .photo(photo)
        .build();

    ArtistDto response = artistClient.createArtist(request,"Bearer "+ token);
    content.artists().add(response);

    assertNotNull(response);
    assertEquals(name, response.name());

    ArtistDto expected = artistDbClient.findByName(name).orElseThrow();
    expected.compare(response);
  }

  @Test
  @User
  @ApiLogin
  @Content(artistCount = 1)
  @DisplayName("Добавление артиста с занятым именем")
  void addArtistNameNotUniqueTest(ContentJson content, @Token String token) {
    final var existing = content.artists().iterator().next();

    ArtistDto request = ArtistDto.builder()
        .name(existing.name())
        .biography(randomDescription())
        .photo(randomImage("artists"))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> artistClient.createArtist(request, "Bearer "+ token));

    artistClient.assertError(
        404,
        ex,
        "404",
        "gRPC error",
        "/api/artist",
        String.format("Name already exists: %s", existing.name())
    );
  }

  @Test
  @DisplayName("Ошибка при добавлении артиста без имени")
  @User
  @ApiLogin
  void addArtistNameRequiredTest(@Token String token) {
    ArtistDto request = ArtistDto.builder()
        .name("")
        .biography("Some bio")
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> artistClient.createArtist(request,"Bearer "+token));

    artistClient.assertError(
        404,
        ex,
        "404",
        "gRPC error",
        "/api/artist",
        "Name required"
    );
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Ошибка при добавлении артиста без биографии")
  void addArtistBiographyRequiredTest(@Token String token) {
    ArtistDto request = ArtistDto.builder()
        .name("Paul Cézanne")
        .biography("")
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> artistClient.createArtist(request,"Bearer "+token));

    artistClient.assertError(
        404,
        ex,
        "404",
        "gRPC error",
        "/api/artist",
        "Biography required"
    );
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Ошибка при добавлении артиста с слишком длинным именем")
  void addArtistNameTooLongTest(@Token String token) {
    ArtistDto request = ArtistDto.builder()
        .name("A".repeat(256))
        .biography(randomDescription())
        .photo(randomImage("artists"))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> artistClient.createArtist(request,"Bearer "+token));

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
  @ApiLogin
  @DisplayName("Ошибка при добавлении артиста с слишком длинной биографией")
  void addArtistBiographyTooLongTest(@Token String token) {
    String longBio = "B".repeat(2001);

    ArtistDto request = ArtistDto.builder()
        .name(randomName())
        .biography(longBio)
        .photo(randomImage("artists"))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> artistClient.createArtist(request,"Bearer "+token));

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
  @DisplayName("Ошибка при добавлении артиста с некорректным токеном")
  void addArtistIncorrectTokenTest() {
    ArtistDto request = ArtistDto.builder()
        .name(randomName())
        .biography(randomDescription())
        .photo(randomImage("artists"))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> artistClient.createArtist(request,"Bearer invalid-token"));

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
  @DisplayName("Ошибка при добавлении артиста с поддельным токеном")
  void addArtistFakeTokenTest() {
    ArtistDto request = ArtistDto.builder()
        .name(randomName())
        .biography(randomDescription())
        .photo(randomImage("artists"))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> artistClient.createArtist(request,fakeJwt()));

    artistClient.assertError(
        401,
        ex,
        "401",
        "Unauthorized",
        "/api/artist",
        "Full authentication is required to access this resource"
    );
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Ошибка при добавлении артиста с слишком большим изображением")
  void addArtistOversizeImageTest(@Token String token) {
    ArtistDto request = ArtistDto.builder()
        .name(randomName())
        .biography(randomDescription())
        .photo(loadImageAsBytes("img/content/oversize.png"))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> artistClient.createArtist(request,"Bearer "+token));

    artistClient.assertError(
        400,
        ex,
        "400",
        "Validation error",
        "/api/artist",
        "photo: File size exceeds allowed limit"
    );
  }

}
