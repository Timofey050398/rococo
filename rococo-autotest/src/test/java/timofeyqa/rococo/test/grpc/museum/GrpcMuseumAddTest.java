package timofeyqa.rococo.test.grpc.museum;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.data.entity.Country;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.mapper.GrpcMapper;
import timofeyqa.rococo.mapper.MuseumMapper;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.dto.MuseumDto;
import timofeyqa.rococo.service.CountryClient;
import timofeyqa.rococo.service.MuseumClient;
import timofeyqa.rococo.service.db.CountryDbClient;
import timofeyqa.rococo.service.db.MuseumDbClient;
import timofeyqa.rococo.test.grpc.BaseGrpcTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static timofeyqa.rococo.utils.RandomDataUtils.randomDescription;
import static timofeyqa.rococo.utils.RandomDataUtils.randomImage;
import static timofeyqa.rococo.utils.RandomDataUtils.randomName;

@DisplayName("gRPC: Добавление музеев")
class GrpcMuseumAddTest extends BaseGrpcTest {

  private final MuseumClient client = new MuseumDbClient();
  private final CountryClient countryClient = new CountryDbClient();

  @Test
  @DisplayName("Успешное добавление музея")
  @Content
  void addMuseumSuccessTest(ContentJson content) {
    final String title = randomName();
    final byte[] photo = randomImage("museums");
    UUID countryId = countryClient.getByName(Country.random())
        .orElseThrow()
        .id();

    AddMuseumRequest request = AddMuseumRequest.newBuilder()
        .setTitle(title)
        .setDescription(randomDescription())
        .setCity("Paris")
        .setCountryId(countryId.toString())
        .setPhoto(GrpcMapper.INSTANCE.fromByte(photo))
        .build();

    var actual = museumStub.addMuseum(request);
    content.museums().add(MuseumMapper.INSTANCE.fromGrpc(actual));

    MuseumDto expected = client.findByTitle(title)
        .orElseThrow();

    client.compareGrpc(expected, actual);
  }

  @Test
  @DisplayName("Добавление музея с занятым названием")
  @Content(museumCount = 1)
  void addMuseumTitleNotUniqueTest(ContentJson content) {
    UUID countryId = countryClient.getByName(Country.random())
        .orElseThrow()
        .id();
    var museum = content.museums().iterator().next();
    AddMuseumRequest request = AddMuseumRequest.newBuilder()
        .setTitle(museum.title())
        .setDescription(randomDescription())
        .setCountryId(countryId.toString())
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> museumStub.addMuseum(request));

    assertEquals("NOT_FOUND", ex.getStatus().getCode().name());
    assertTrue(ex.getMessage().contains("Title already exists"));
  }

  @Test
  @DisplayName("Ошибка при добавлении музея без названия")
  void addMuseumTitleRequiredTest() {
    UUID countryId = countryClient.getByName(Country.random())
        .orElseThrow()
        .id();
    AddMuseumRequest request = AddMuseumRequest.newBuilder()
        .setTitle("")
        .setDescription("Some description")
        .setCountryId(countryId.toString())
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> museumStub.addMuseum(request));

    assertEquals("NOT_FOUND", ex.getStatus().getCode().name());
    assertTrue(ex.getMessage().contains("Title required"));
  }

  @Test
  @DisplayName("Ошибка при добавлении музея без countryId")
  void addMuseumCountryRequiredTest() {
    AddMuseumRequest request = AddMuseumRequest.newBuilder()
        .setTitle("Louvre")
        .setDescription("Famous museum")
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> museumStub.addMuseum(request));

    assertEquals("NOT_FOUND", ex.getStatus().getCode().name());
    assertTrue(ex.getMessage().contains("Country required"));
  }

  @Test
  @DisplayName("Добавление музея с не существующим countryId")
  void addMuseumCountryNotFoundTest() {
    final String title = randomName();
    final byte[] photo = randomImage("museums");

    AddMuseumRequest request = AddMuseumRequest.newBuilder()
        .setTitle(title)
        .setDescription(randomDescription())
        .setCity("Paris")
        .setCountryId(UUID.randomUUID().toString())
        .setPhoto(GrpcMapper.INSTANCE.fromByte(photo))
        .build();

    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
        () -> museumStub.addMuseum(request));

    assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode());
    assertEquals("Country with provided Id not found",exception.getStatus().getDescription());
  }

  @Test
  @DisplayName("Добавление музея с countryId не UUID")
  void addMuseumCountryIdNotInstanceOfUuidTest() {
    final String title = randomName();
    final byte[] photo = randomImage("museums");

    AddMuseumRequest request = AddMuseumRequest.newBuilder()
        .setTitle(title)
        .setDescription(randomDescription())
        .setCity("Paris")
        .setCountryId("abc")
        .setPhoto(GrpcMapper.INSTANCE.fromByte(photo))
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> museumStub.addMuseum(request));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
  }

  @Test
  @DisplayName("Ошибка при добавлении музея с слишком длинным названием")
  void addMuseumNameTooLongTest() {
    UUID countryId = countryClient.getByName(Country.random())
        .orElseThrow()
        .id();
    AddMuseumRequest request = AddMuseumRequest.newBuilder()
        .setTitle("A".repeat(256))
        .setDescription(randomDescription())
        .setCountryId(countryId.toString())
        .setPhoto(GrpcMapper.INSTANCE.fromByte(randomImage("museums")))
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> museumStub.addMuseum(request));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
    assertEquals("INVALID_ARGUMENT: Validation errors: title size must be between 0 and 255; ",ex.getMessage(),ex.getMessage());
  }

  @Test
  @DisplayName("Ошибка при добавлении музея с слишком длинным названием страны")
  void addMuseumCityTooLongTest() {
    UUID countryId = countryClient.getByName(Country.random())
        .orElseThrow()
        .id();
    AddMuseumRequest request = AddMuseumRequest.newBuilder()
        .setTitle("A".repeat(256))
        .setDescription(randomDescription())
        .setCountryId(countryId.toString())
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> museumStub.addMuseum(request));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
    assertEquals("INVALID_ARGUMENT: Validation errors: title size must be between 0 and 255; ",ex.getMessage(),ex.getMessage());
  }

  @Test
  @DisplayName("Ошибка при добавлении музея с слишком длинным описанием")
  void addMuseumBiographyTooLongTest() {
    UUID countryId = countryClient.getByName(Country.random())
        .orElseThrow()
        .id();
    String longBio = "B".repeat(1001);
    AddMuseumRequest request = AddMuseumRequest.newBuilder()
        .setTitle(randomName())
        .setDescription(longBio)
        .setCountryId(countryId.toString())
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> museumStub.addMuseum(request));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
    assertEquals("INVALID_ARGUMENT: Validation errors: description size must be between 0 and 1000; ",ex.getMessage(),ex.getMessage());
  }
}
