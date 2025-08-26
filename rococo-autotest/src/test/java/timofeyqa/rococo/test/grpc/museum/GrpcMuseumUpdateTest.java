package timofeyqa.rococo.test.grpc.museum;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.data.entity.Country;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.mapper.GrpcMapper;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.dto.MuseumDto;
import timofeyqa.rococo.service.CountryClient;
import timofeyqa.rococo.service.MuseumClient;
import timofeyqa.rococo.service.db.CountryDbClient;
import timofeyqa.rococo.service.db.MuseumDbClient;
import timofeyqa.rococo.test.grpc.BaseGrpcTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static timofeyqa.rococo.utils.RandomDataUtils.*;

@DisplayName("gRPC: Обновление музеев")
class GrpcMuseumUpdateTest extends BaseGrpcTest {

  private final MuseumClient museumClient = new MuseumDbClient();
  private final CountryClient countryClient = new CountryDbClient();
  
  @Test
  @DisplayName("Успешное обновление музея")
  @Content(museumCount = 1)
  void updateMuseumSuccessTest(ContentJson content) {
    var museum = content.museums().iterator().next();
    var newPhoto = GrpcMapper.INSTANCE.fromByte(new byte[]{1, 2, 3});
    String newTitle = randomName();
    String newDescription = randomDescription();
    String newCity = randomCity();
    UUID countryId = countryClient.getByName(Country.random())
        .orElseThrow()
        .id();

    var updated = museumStub.updateMuseum(
        Museum.newBuilder()
            .setId(museum.id().toString())
            .setTitle(newTitle)
            .setDescription(newDescription)
            .setPhoto(newPhoto)
            .setCity(newCity)
            .setCountryId(countryId.toString())
            .build()
    );

    assertAll(()-> {
      museumClient.compareGrpc(
          museumClient.findAllById(List.of(museum.id())).getFirst(),
          updated
      );

      assertEquals(countryId.toString(), updated.getCountryId());
      assertEquals(newCity,updated.getCity());
      assertEquals(newTitle, updated.getTitle());
      assertEquals(newDescription, updated.getDescription());
      assertEquals(newPhoto, updated.getPhoto());
    });
  }

  @Test
  @DisplayName("NOT_FOUND при обновлении несуществующего музея")
  void updateMuseumNotFoundTest() {
    Museum request = Museum.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setTitle("Ghost Museum")
        .setDescription("No one knows it")
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> museumStub.updateMuseum(request));

    assertEquals("NOT_FOUND", ex.getStatus().getCode().name());
  }

  @Test
  @DisplayName("Ошибка при обновлении: дубликат title")
  @Content(museumCount = 2)
  void updateMuseumDuplicateNameTest(ContentJson content) {
    var it = content.museums().iterator();
    MuseumDto first = it.next();
    MuseumDto second = it.next();

    Museum conflict = Museum.newBuilder()
        .setId(second.id().toString())
        .setTitle(first.title())
        .setDescription(randomDescription())
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> museumStub.updateMuseum(conflict));

    assertEquals("NOT_FOUND", ex.getStatus().getCode().name());
    assertTrue(ex.getMessage().contains("Title already exists: "+first.title()));
  }

  @Test
  @DisplayName("Изменение музея с не существующим countryId")
  @Content(museumCount = 1)
  void updateMuseumCountryNotFoundTest(ContentJson content) {
    Museum request = Museum.newBuilder()
        .setId(content.museums().iterator().next().id().toString())
        .setCountryId(UUID.randomUUID().toString())
        .build();

    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
        () -> museumStub.updateMuseum(request));

    assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode());
    assertEquals("Country with provided Id not found",exception.getStatus().getDescription());
  }

  @Test
  @DisplayName("Изменение музея с countryId не UUID")
  @Content(museumCount = 1)
  void updateMuseumCountryIdNotInstanceOfUuidTest(ContentJson content) {
    Museum request = Museum.newBuilder()
        .setId(content.museums().iterator().next().id().toString())
        .setCountryId("abc")
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> museumStub.updateMuseum(request));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
  }

  @Test
  @DisplayName("Ошибка при изменении музея с слишком длинным названием")
  @Content(museumCount = 1)
  void updateMuseumNameTooLongTest(ContentJson content) {
    Museum request = Museum.newBuilder()
        .setId(content.museums().iterator().next().id().toString())
        .setTitle("A".repeat(256))
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> museumStub.updateMuseum(request));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
    assertEquals("INVALID_ARGUMENT: Validation errors: title size must be between 0 and 255; ",ex.getMessage(),ex.getMessage());
  }

  @Test
  @DisplayName("Ошибка при изменении музея с слишком длинным названием страны")
  @Content(museumCount = 1)
  void updateMuseumCityTooLongTest(ContentJson content) {
    Museum request = Museum.newBuilder()
        .setId(content.museums().iterator().next().id().toString())
        .setTitle("A".repeat(256))
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> museumStub.updateMuseum(request));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
    assertEquals("INVALID_ARGUMENT: Validation errors: title size must be between 0 and 255; ",ex.getMessage(),ex.getMessage());
  }

  @Test
  @DisplayName("Ошибка при изменении музея с слишком длинным описанием")
  @Content(museumCount = 1)
  void updateMuseumBiographyTooLongTest(ContentJson content) {
    String longBio = "B".repeat(1001);
    Museum request = Museum.newBuilder()
        .setId(content.museums().iterator().next().id().toString())
        .setDescription(longBio)
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> museumStub.updateMuseum(request));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
    assertEquals("INVALID_ARGUMENT: Validation errors: description size must be between 0 and 1000; ",ex.getMessage(),ex.getMessage());
  }

  @Test
  @DisplayName("При передаче пустых полей, поля не обновляются")
  @Content(museumCount = 1)
  void updateMuseumBlankFieldsShouldNotUpdated(ContentJson content) {
    var museum = content.museums().iterator().next();

    assertAll(()->{
      assertFalse(StringUtils.isEmpty(museum.title()));
      assertFalse(StringUtils.isEmpty(museum.description()));
      assertFalse(StringUtils.isEmpty(museum.geo().city()));
      assertNotNull(museum.geo().country().id());
      assertFalse(ArrayUtils.isEmpty(museum.photo()));
    });

    Museum request = Museum.newBuilder()
        .setId(museum.id().toString())
        .setTitle("")
        .setDescription("")
        .setCity("")
        .setCountryId("")
        .setPhoto(ByteString.EMPTY)
        .build();

    var response = museumStub.updateMuseum(request);

    assertAll(()-> {
      assertEquals(request.getId(), response.getId());
      assertFalse(StringUtils.isEmpty(response.getTitle()));
      assertFalse(StringUtils.isEmpty(response.getDescription()));
      assertFalse(StringUtils.isEmpty(response.getCity()));
      assertFalse(StringUtils.isEmpty(response.getCountryId()));
      assertFalse(response.getPhoto().isEmpty());
    });
  }
}
