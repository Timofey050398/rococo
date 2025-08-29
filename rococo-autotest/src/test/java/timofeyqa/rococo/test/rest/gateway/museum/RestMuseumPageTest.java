package timofeyqa.rococo.test.rest.gateway.museum;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import retrofit2.HttpException;
import timofeyqa.rococo.jupiter.annotation.Museum;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.jupiter.annotation.meta.RestTest;
import timofeyqa.rococo.model.dto.MuseumDto;
import timofeyqa.rococo.model.rest.pageable.RestResponsePage;
import timofeyqa.rococo.service.api.MuseumRestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты запроса GET /museum")
@RestTest
public class RestMuseumPageTest {
  private final MuseumRestClient museumRestClient = new MuseumRestClient();

  private static final String FILTER_FIELD = "Museum F";

  @Test
  @Content(museums = {
      @Museum(title = "Museum A", description = "Bio D",city ="City B"),
      @Museum(title = "Museum B", description = "Bio C"),
      @Museum(title = "Museum C", description = "Bio B"),
      @Museum(title = "Museum D", description = "Bio A")
  })
  @DisplayName("Получение первой страницы")
  void getFirstPageTest() {
    RestResponsePage<MuseumDto> response = museumRestClient.getPage(0, 2, null);

    assertNotNull(response);
    assertEquals(2, response.getSize());
    assertTrue(response.getTotalElements() >= 4);
    assertTrue(response.getTotalPages() >= 2);

    List<MuseumDto> content = response.getContent();
    assertEquals(2, content.size());
    assertFalse(StringUtils.isBlank(content.getFirst().title()));
  }

  @Test
  @Content(museums = {
      @Museum(title = "Museum E", description = "Bio H"),
      @Museum(title = FILTER_FIELD, description = "Bio G"),
      @Museum(title = "Museum G", description = "Bio F"),
      @Museum(title = "Museum H", description = "Bio E")
  })
  @DisplayName("Фильтрация артистов по имени (contains)")
  void filterMuseumsTest() {
    RestResponsePage<MuseumDto> response = museumRestClient.getPage(0, 10, FILTER_FIELD);

    assertNotNull(response);
    assertFalse(response.getContent().isEmpty());

    List<MuseumDto> content = response.getContent();

    assertTrue(content.stream()
        .allMatch(a -> a.title().contains(FILTER_FIELD)));
  }

  @Test
  @DisplayName("Ошибка при запросе страницы больше максимально допустимого размера")
  void invalidPageSizeTest() {
    HttpException ex = assertThrows(HttpException.class, () -> museumRestClient.getPage(0, 11, null));

     museumRestClient.assertError(
         400,
         ex,
         "400",
         "Constraint violation",
         "/api/museum",
         "getAll.pageable: Page size can't be greater than 10"
     );
  }
}
