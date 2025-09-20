package timofeyqa.rococo.test.rest.gateway.museum;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.HttpException;
import timofeyqa.rococo.data.entity.Country;
import timofeyqa.rococo.jupiter.annotation.*;
import timofeyqa.rococo.jupiter.annotation.meta.RestTest;
import timofeyqa.rococo.jupiter.extension.ApiLoginExtension;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.rest.CountryJson;
import timofeyqa.rococo.model.dto.MuseumDto;
import timofeyqa.rococo.service.CountryClient;
import timofeyqa.rococo.service.MuseumClient;
import timofeyqa.rococo.service.api.MuseumRestClient;
import timofeyqa.rococo.service.db.CountryDbClient;
import timofeyqa.rococo.service.db.MuseumDbClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static timofeyqa.rococo.jupiter.extension.ApiLoginExtension.rest;
import static timofeyqa.rococo.utils.PhotoConverter.loadImageAsBytes;
import static timofeyqa.rococo.utils.RandomDataUtils.*;

@DisplayName("REST: Частичное обновление музеев (PATCH)")
@RestTest
class RestMuseumPatchTest {

  private final MuseumRestClient museumClient = new MuseumRestClient();
  private final MuseumClient museumDbClient = new MuseumDbClient();
  private final CountryClient countryClient = new CountryDbClient();

  @RegisterExtension
  private static final ApiLoginExtension extension = rest();

  @Test
  @User
  @Content(museumCount = 1)
  @ApiLogin
  @DisplayName("Успешное частичное обновление музея")
  void updateMuseumSuccessTest(ContentJson content, @Token String token) {
    var museum = content.museums().iterator().next();

    var newTitle = randomName();
    var newDescription = randomDescription();
    var newCity = randomCity();
    var newPhoto = randomImage("museums");

    CountryJson newCountry = countryClient.getByName(Country.random())
        .orElseThrow();

    MuseumDto request = MuseumDto.builder()
        .id(museum.id())
        .title(newTitle)
        .description(newDescription)
        .geo(museum.geo().toBuilder().city(newCity).country(newCountry).build())
        .photo(newPhoto)
        .build();

    MuseumDto response = museumClient.updateMuseum(request, "Bearer " + token);

    assertAll(
        () -> assertEquals(museum.id(), response.id()),
        () -> assertEquals(newTitle, response.title()),
        () -> assertEquals(newDescription, response.description()),
        () -> assertEquals(newCity, response.geo().city()),
        () -> assertEquals(newCountry.id(), response.geo().country().id()),
        () -> assertArrayEquals(newPhoto, response.photo())
    );

    MuseumDto expected = museumDbClient.findByTitle(newTitle).orElseThrow();
    expected.compare(response);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Ошибка при обновлении: дубликат title")
  @Content(museumCount = 2)
  void updateMuseumDuplicateNameTest(ContentJson content, @Token String token) {
    var it = content.museums().iterator();
    MuseumDto first = it.next();
    MuseumDto second = it.next();

    MuseumDto conflict = MuseumDto.builder()
        .id(second.id())
        .title(first.title())
        .description(randomDescription())
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> museumClient.updateMuseum(conflict,"Bearer " + token));

    museumClient.assertError(
        404,
        ex,
        "404",
        "gRPC error",
        "/api/museum",
        "Title already exists: " + first.title()
    );
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("NOT_FOUND при попытке PATCH несуществующего музея")
  void updateMuseumNotFoundTest(@Token String token) {
    UUID id = UUID.randomUUID();
    MuseumDto request = MuseumDto.builder()
        .id(id)
        .title("Ghost Museum")
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> museumClient.updateMuseum(request, "Bearer " + token));

    museumClient.assertError(
        404,
        ex,
        "404",
        "gRPC error",
        "/api/museum",
        "Museum not found: " + id
    );
  }

  @Test
  @User
  @Content(museumCount = 1)
  @ApiLogin
  @DisplayName("Ошибка при слишком длинном названии")
  void updateMuseumTitleTooLongTest(ContentJson content, @Token String token) {
    var museum = content.museums().iterator().next();

    MuseumDto request = MuseumDto.builder()
        .id(museum.id())
        .title("A".repeat(256))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> museumClient.updateMuseum(request, "Bearer " + token));

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
  @Content(museumCount = 1)
  @ApiLogin
  @DisplayName("Ошибка при слишком длинном описании")
  void updateMuseumDescriptionTooLongTest(ContentJson content, @Token String token) {
    var museum = content.museums().iterator().next();

    MuseumDto request = MuseumDto.builder()
        .id(museum.id())
        .description("B".repeat(1001))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> museumClient.updateMuseum(request, "Bearer " + token));

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
  @Content(museumCount = 1)
  @ApiLogin
  @DisplayName("Ошибка при слишком длинном названии города")
  void updateMuseumCityTooLongTest(ContentJson content, @Token String token) {
    var museum = content.museums().iterator().next();

    MuseumDto request = MuseumDto.builder()
        .id(museum.id())
        .geo(museum.geo().toBuilder().city("C".repeat(256)).build())
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> museumClient.updateMuseum(request, "Bearer " + token));

    museumClient.assertError(
        400,
        ex,
        "400",
        "gRPC error",
        "/api/museum",
        "Validation errors: city size must be between 0 and 255; "
    );
  }

  @Test
  @User
  @Content(museumCount = 1)
  @ApiLogin
  @DisplayName("Ошибка при несуществующем countryId")
  void updateMuseumCountryNotFoundTest(ContentJson content, @Token String token) {
    var museum = content.museums().iterator().next();
    UUID id = UUID.randomUUID();

    CountryJson fakeCountry = CountryJson.builder()
        .id(id)
        .name("Ghost Country")
        .build();

    MuseumDto request = MuseumDto.builder()
        .id(museum.id())
        .geo(museum.geo().toBuilder().country(fakeCountry).build())
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> museumClient.updateMuseum(request, "Bearer " + token));

    museumClient.assertError(
        404,
        ex,
        "404",
        "gRPC error",
        "/api/museum",
        "Country not found "+id
    );
  }

  @Test
  @User
  @Content(museumCount = 1)
  @ApiLogin
  @DisplayName("Ошибка при слишком большом файле")
  void updateMuseumOversizeImageTest(ContentJson content, @Token String token) {
    var museum = content.museums().iterator().next();

    MuseumDto request = MuseumDto.builder()
        .id(museum.id())
        .photo(loadImageAsBytes("img/content/oversize.png"))
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> museumClient.withOversizedContent().updateMuseum(request, "Bearer " + token));

    museumClient.assertError(
        400,
        ex,
        "400",
        "Validation error",
        "/api/museum",
        "photo: File size exceeds allowed limit"
    );
  }

  @Test
  @DisplayName("Ошибка при некорректном токене")
  void updateMuseumIncorrectTokenTest() {
    MuseumDto request = MuseumDto.builder()
        .id(UUID.randomUUID())
        .title("Forbidden Update")
        .build();

    HttpException ex = assertThrows(HttpException.class,
        () -> museumClient.updateMuseum(request, "Bearer invalid-token"));

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
  @Content(museums = @Museum(
      country = Country.FRANCE,
      city = "Paris",
      title = "Orsay",
      description = "Impressionist museum",
      photo = "screenshots/local/museums-list/louvre.png"
  ))
  @ApiLogin
  @DisplayName("При передаче пустых полей они не обновляют запись")
  void updateMuseumBlankFieldsShouldNotUpdate(ContentJson content, @Token String token) {
    var museum = content.museums().iterator().next();

    MuseumDto request = MuseumDto.builder()
        .id(museum.id())
        .title(null)
        .description(null)
        .geo(null)
        .photo(null)
        .build();

    MuseumDto response = museumClient.updateMuseum(request, "Bearer " + token);

    assertEquals(museum.id(), response.id());
    assertFalse(StringUtils.isEmpty(response.title()));
    assertFalse(StringUtils.isEmpty(response.description()));
    assertNotNull(response.geo());
    assertNotNull(response.geo().city());
    assertNotNull(response.geo().country());
    assertNotNull(response.photo());
  }
}
