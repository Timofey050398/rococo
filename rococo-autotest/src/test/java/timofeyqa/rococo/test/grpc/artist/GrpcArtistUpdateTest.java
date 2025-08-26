package timofeyqa.rococo.test.grpc.artist;

import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.grpc.rococo.Artist;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.mapper.GrpcMapper;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.dto.ArtistDto;
import timofeyqa.rococo.service.ArtistClient;
import timofeyqa.rococo.service.db.ArtistDbClient;
import timofeyqa.rococo.test.grpc.BaseGrpcTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static timofeyqa.rococo.utils.RandomDataUtils.*;

@DisplayName("gRPC: Обновление артистов")
class GrpcArtistUpdateTest extends BaseGrpcTest {

  private final ArtistClient artistClient = new ArtistDbClient();

  @Test
  @DisplayName("Успешное обновление артиста")
  @Content(artistCount = 1)
  void updateArtistSuccessTest(ContentJson content) {
    var artist = content.artists().iterator().next();
    ByteString newPhoto = GrpcMapper.INSTANCE.fromByte(randomImage("artists"));
    String newName = randomName();
    String newBiography = randomDescription();
    var updated = artistStub.updateArtist(
        Artist.newBuilder()
            .setId(artist.id().toString())
            .setName(newName)
            .setBiography(newBiography)
            .setPhoto(newPhoto)
            .build()
    );

    assertAll(()-> {
      artistClient.compareGrpc(
          artistClient.findAllById(List.of(artist.id())).getFirst(),
          updated
      );
      assertEquals(newName, updated.getName());
      assertEquals(newBiography, updated.getBiography());
      assertEquals(newPhoto, updated.getPhoto());
    });
  }

  @Test
  @DisplayName("NOT_FOUND при обновлении несуществующего артиста")
  void updateArtistNotFoundTest() {
    Artist request = Artist.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setName("Ghost Artist")
        .setBiography("No one knows him")
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> artistStub.updateArtist(request));

    assertEquals("NOT_FOUND", ex.getStatus().getCode().name());
  }

  @Test
  @DisplayName("Ошибка при обновлении: дубликат имени")
  @Content(artistCount = 2)
  void updateArtistDuplicateNameTest(ContentJson content) {
    var it = content.artists().iterator();
    ArtistDto first = it.next();
    ArtistDto second = it.next();

    Artist conflict = Artist.newBuilder()
        .setId(second.id().toString())
        .setName(first.name())
        .setBiography("bio B updated")
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> artistStub.updateArtist(conflict));

    assertEquals("NOT_FOUND", ex.getStatus().getCode().name());
    assertTrue(ex.getMessage().contains("Name already exists"));
  }



  @Test
  @DisplayName("Ошибка при обновлении артиста с слишком длинным именем")
  @Content(artistCount = 1)
  void editArtistNameTooLongTest(ContentJson content) {
    Artist conflict = Artist.newBuilder()
        .setId(content.artists().iterator().next().id().toString())
        .setName("a".repeat(260))
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> artistStub.updateArtist(conflict));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
    assertEquals("INVALID_ARGUMENT: Validation errors: name size must be between 0 and 255; ",ex.getMessage(),ex.getMessage());
  }

  @Test
  @DisplayName("Ошибка при обновлении артиста с слишком длинной биографией")
  @Content(artistCount = 1)
  void editArtistBiographyTooLongTest(ContentJson content) {
    String longBio = "B".repeat(2001);
    Artist conflict = Artist.newBuilder()
        .setId(content.artists().iterator().next().id().toString())
        .setBiography(longBio)
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> artistStub.updateArtist(conflict));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
    assertEquals("INVALID_ARGUMENT: Validation errors: biography size must be between 0 and 2000; ",ex.getMessage(),ex.getMessage());
  }

  @Test
  @DisplayName("При передаче пустых полей, поля не обновляются")
  @Content(artistCount = 1)
  void editArtistBlankFieldsShouldNotUpdated(ContentJson content) {
    var artist = content.artists().iterator().next();

    assertAll(()->{
      assertFalse(StringUtils.isEmpty(artist.name()));
      assertFalse(StringUtils.isEmpty(artist.biography()));
      assertFalse(ArrayUtils.isEmpty(artist.photo()));
    });

    Artist request = Artist.newBuilder()
        .setId(artist.id().toString())
        .setName("")
        .setBiography("")
        .setPhoto(ByteString.EMPTY)
        .build();

    var response = artistStub.updateArtist(request);

    assertAll(()-> {
      assertEquals(request.getId(), response.getId());
      assertFalse(StringUtils.isEmpty(response.getName()));
      assertFalse(StringUtils.isEmpty(response.getBiography()));
      assertFalse(response.getPhoto().isEmpty());
    });
  }

}