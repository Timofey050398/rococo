package timofeyqa.rococo.test.rest.gateway.museum;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.HttpException;
import timofeyqa.rococo.data.entity.Country;
import timofeyqa.rococo.jupiter.annotation.ApiLogin;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.jupiter.annotation.Token;
import timofeyqa.rococo.jupiter.annotation.User;
import timofeyqa.rococo.jupiter.annotation.meta.RestTest;
import timofeyqa.rococo.jupiter.extension.ApiLoginExtension;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.dto.MuseumDto;
import timofeyqa.rococo.model.rest.GeoJson;
import timofeyqa.rococo.service.CountryClient;
import timofeyqa.rococo.service.MuseumClient;
import timofeyqa.rococo.service.api.MuseumRestClient;
import timofeyqa.rococo.service.db.CountryDbClient;
import timofeyqa.rococo.service.db.MuseumDbClient;

import static org.junit.jupiter.api.Assertions.*;
import static timofeyqa.rococo.jupiter.extension.ApiLoginExtension.rest;
import static timofeyqa.rococo.utils.PhotoConverter.loadImageAsBytes;
import static timofeyqa.rococo.utils.RandomDataUtils.*;

@DisplayName("REST: Добавление музеев")
@RestTest
class RestMuseumPostTest {

  private final MuseumRestClient museumClient = new MuseumRestClient();
  private final MuseumClient museumDbClient = new MuseumDbClient();
  private final CountryClient countryClient = new CountryDbClient();

  @RegisterExtension
  private static final ApiLoginExtension extension = rest();

  @Test
  @User
  @Content
  @ApiLogin
  @DisplayName("Успешное добавление музея")
  void addMuseumSuccessTest(ContentJson content, @Token String token) {
    String title = randomName();
    byte[] photo = randomImage("museums");
    var country = countryClient.getByName(Country.random()).orElseThrow();

    MuseumDto request = MuseumDto.builder()
        .title(title)
        .description(randomDescription())
        .photo(photo)
        .geo(new GeoJson("Paris", country))
        .build();

    MuseumDto response = museumClient.createMuseum(request, "Bearer " + token);
    content.museums().add(response);

    assertNotNull(response);
    assertEquals(title, response.title());

    MuseumDto expected = museumDbClient.findByTitle(title).orElseThrow();
    expected.compare(response);
  }

  @Test
  @User
  @ApiLogin
  @Content(museumCount = 1)
  @DisplayName("Добавление музея с занятым названием")
  void addMuseumTitleNotUniqueTest(ContentJson content, @Token String token) {
    var existing = content.museums().iterator().next();
    var country = countryClient.getByName(Country.random()).orElseThrow();

    MuseumDto request = MuseumDto.builder()
        .title(existing.title())
        .description(randomDescription())
        .geo(new GeoJson("Berlin", country))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> museumClient.createMuseum(request, "Bearer " + token));

    museumClient.assertError(
        404,
        ex,
        "404",
        "gRPC error",
        "/api/museum",
        String.format("Title already exists: %s", existing.title())
    );
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Ошибка при добавлении музея без названия")
  void addMuseumTitleRequiredTest(@Token String token) {
    var country = countryClient.getByName(Country.random()).orElseThrow();

    MuseumDto request = MuseumDto.builder()
        .title("")
        .description("Some description")
        .geo(new GeoJson(randomCity(), country))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> museumClient.createMuseum(request, "Bearer " + token));

    museumClient.assertError(
        404,
        ex,
        "404",
        "gRPC error",
        "/api/museum",
        "Title required"
    );
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Ошибка при добавлении музея без country")
  void addMuseumCountryRequiredTest(@Token String token) {
    MuseumDto request = MuseumDto.builder()
        .title("Louvre")
        .description("Famous museum")
        .geo(new GeoJson(randomCity(), null))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> museumClient.createMuseum(request, "Bearer " + token));

    museumClient.assertError(
        404,
        ex,
        "404",
        "gRPC error",
        "/api/museum",
        "Country required"
    );
  }

  @Test
  @DisplayName("Ошибка при добавлении музея с некорректным токеном")
  void addMuseumIncorrectTokenTest() {
    var country = countryClient.getByName(Country.random()).orElseThrow();

    MuseumDto request = MuseumDto.builder()
        .title(randomName())
        .description(randomDescription())
        .geo(new GeoJson("London", country))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> museumClient.createMuseum(request, "Bearer invalid-token"));

    museumClient.assertError(
        401,
        ex,
        "401",
        "Unauthorized",
        "/api/museum",
        "An error occurred while attempting to decode the Jwt: Malformed token"
    );
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Ошибка при добавлении музея с слишком длинным названием")
  void addMuseumTitleTooLongTest(@Token String token) {
    var country = countryClient.getByName(Country.random()).orElseThrow();

    MuseumDto request = MuseumDto.builder()
        .title("A".repeat(256))
        .description(randomDescription())
        .geo(new GeoJson("Madrid", country))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> museumClient.createMuseum(request, "Bearer " + token));

    museumClient.assertError(
        400,
        ex,
        "400",
        "gRPC error",
        "/api/museum",
        "Validation errors: title size must be between 0 and 255; "
    );
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Ошибка при добавлении музея с слишком длинным описанием")
  void addMuseumDescriptionTooLongTest(@Token String token) {
    var country = countryClient.getByName(Country.random()).orElseThrow();
    String longDescription = "B".repeat(1001);

    MuseumDto request = MuseumDto.builder()
        .title(randomName())
        .description(longDescription)
        .geo(new GeoJson("Oslo", country))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> museumClient.createMuseum(request, "Bearer " + token));

    museumClient.assertError(
        400,
        ex,
        "400",
        "Validation error",
        "/api/museum",
        "description: Can`t be longer than 1000 characters"
    );
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Ошибка при добавлении музея с слишком большим изображением")
  void addMuseumOversizeImageTest(@Token String token) {
    var country = countryClient.getByName(Country.random()).orElseThrow();

    MuseumDto request = MuseumDto.builder()
        .title(randomName())
        .description(randomDescription())
        .geo(new GeoJson("Tokyo", country))
        .photo(loadImageAsBytes("img/content/oversize.png"))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> museumClient.withOversizedContent().createMuseum(request, "Bearer " + token));

    museumClient.assertError(
        400,
        ex,
        "400",
        "Validation error",
        "/api/museum",
        "photo: File size exceeds allowed limit"
    );
  }
}
