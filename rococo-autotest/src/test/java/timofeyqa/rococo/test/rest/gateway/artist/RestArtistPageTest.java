package timofeyqa.rococo.test.rest.gateway.artist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import retrofit2.HttpException;
import timofeyqa.rococo.jupiter.annotation.Artist;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.jupiter.annotation.meta.RestTest;
import timofeyqa.rococo.model.dto.ArtistDto;
import timofeyqa.rococo.model.rest.pageable.RestResponsePage;
import timofeyqa.rococo.service.api.ArtistRestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты запроса GET /artist")
@RestTest
public class RestArtistPageTest {
  private final ArtistRestClient artistClient = new ArtistRestClient();

  private static final String FILTER_FIELD = "Artist F";

  @Test
  @Content(artists = {
      @Artist(name = "Artist A", biography = "Bio D"),
      @Artist(name = "Artist B", biography = "Bio C"),
      @Artist(name = "Artist C", biography = "Bio B"),
      @Artist(name = "Artist D", biography = "Bio A")
  })
  @DisplayName("Получение первой страницы")
  void getFirstPageTest() {
    RestResponsePage<ArtistDto> response = artistClient.getPage(0, 2, null);

    assertNotNull(response);
    assertEquals(2, response.getSize());
    assertTrue(response.getTotalElements() >= 4);
    assertTrue(response.getTotalPages() >= 2);

    List<ArtistDto> content = response.getContent();
    assertEquals(2, content.size());
    assertFalse(StringUtils.isBlank(content.getFirst().name()));
  }

  @Test
  @Content(artists = {
      @Artist(name = "Artist E", biography = "Bio H"),
      @Artist(name = FILTER_FIELD, biography = "Bio G"),
      @Artist(name = "Artist G", biography = "Bio F"),
      @Artist(name = "Artist H", biography = "Bio E")
  })
  @DisplayName("Фильтрация артистов по имени (contains)")
  void filterArtistsTest() {
    RestResponsePage<ArtistDto> response = artistClient.getPage(0, 10, FILTER_FIELD);

    assertNotNull(response);
    assertFalse(response.getContent().isEmpty());

    List<ArtistDto> content = response.getContent();

    assertTrue(content.stream()
        .allMatch(a -> a.name().contains(FILTER_FIELD)));
  }

  @Test
  @DisplayName("Ошибка при запросе страницы больше максимально допустимого размера")
  void invalidPageSizeTest() {
    HttpException ex = assertThrows(HttpException.class, () -> artistClient.getPage(0, 19, null));

     artistClient.assertError(
         400,
         ex,
         "400",
         "Constraint violation",
         "/api/artist",
         "getAll.pageable: Page size can't be greater than 18"
     );
  }
}
