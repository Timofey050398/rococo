package timofeyqa.rococo.model.dto;

import io.qameta.allure.Param;
import io.qameta.allure.model.Parameter;
import lombok.Builder;
import timofeyqa.rococo.model.rest.ContentImpl;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Builder(toBuilder = true)
public record PaintingDto(
    UUID id,
    String title,
    String description,
    ArtistDto artist,
    MuseumDto museum,
    @Param(mode = Parameter.Mode.MASKED)
    byte[] content) implements ContentImpl {

  public void compare(PaintingDto expected) {
    assertNotNull(expected);
    assertAll(
        () -> assertEquals(expected.id,id,"id not equals"),
        () -> assertEquals(expected.title,title,"title not equals"),
        () -> assertEquals(expected.description,description,"description not equals"),
        () -> assertArrayEquals(expected.content, content,"content not equals"),
        () -> {
          if (museum != null) {
            museum.compare(expected.museum);
          } else {
            assertNull(expected.museum,"museum not equals");
          }
        },
        () -> {
          if (artist != null) {
            artist.compare(expected.artist);
          } else {
            assertNull(expected.artist,"artist not equals");
          }
        }
    );
  }
}
