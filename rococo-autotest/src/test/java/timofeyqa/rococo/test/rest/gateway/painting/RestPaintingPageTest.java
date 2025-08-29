package timofeyqa.rococo.test.rest.gateway.painting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import retrofit2.HttpException;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.jupiter.annotation.Painting;
import timofeyqa.rococo.jupiter.annotation.meta.RestTest;
import timofeyqa.rococo.model.dto.PaintingDto;
import timofeyqa.rococo.model.rest.pageable.RestResponsePage;
import timofeyqa.rococo.service.api.PaintingRestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты запроса GET /painting")
@RestTest
public class RestPaintingPageTest {
  private final PaintingRestClient paintingRestClient = new PaintingRestClient();

  private static final String FILTER_FIELD = "Painting F";

  @Test
  @Content(paintings = {
      @Painting(title = "Painting A", description = "Bio D"),
      @Painting(title = "Painting B", description = "Bio C"),
      @Painting(title = "Painting C", description = "Bio B"),
      @Painting(title = "Painting D", description = "Bio A")
  })
  @DisplayName("Получение первой страницы")
  void getFirstPageTest() {
    RestResponsePage<PaintingDto> response = paintingRestClient.getPage(0, 2, null);

    assertNotNull(response);
    assertEquals(2, response.getSize());
    assertTrue(response.getTotalElements() >= 4);
    assertTrue(response.getTotalPages() >= 2);

    List<PaintingDto> content = response.getContent();
    assertEquals(2, content.size());
    assertFalse(StringUtils.isBlank(content.getFirst().title()));
  }

  @Test
  @Content(paintings = {
      @Painting(title = "Painting E", description = "Bio H"),
      @Painting(title = FILTER_FIELD, description = "Bio G"),
      @Painting(title = "Painting G", description = "Bio F"),
      @Painting(title = "Painting H", description = "Bio E")
  })
  @DisplayName("Фильтрация артистов по имени (contains)")
  void filterPaintingsTest() {
    RestResponsePage<PaintingDto> response = paintingRestClient.getPage(0, 10, FILTER_FIELD);

    assertNotNull(response);
    assertFalse(response.getContent().isEmpty());

    List<PaintingDto> content = response.getContent();

    assertTrue(content.stream()
        .allMatch(a -> a.title().contains(FILTER_FIELD)));
  }

  @Test
  @DisplayName("Ошибка при запросе страницы больше максимально допустимого размера")
  void invalidPageSizeTest() {
    HttpException ex = assertThrows(HttpException.class, () -> paintingRestClient.getPage(0, 11, null));

     paintingRestClient.assertError(
         400,
         ex,
         "400",
         "Constraint violation",
         "/api/painting",
         "getAll.pageable: Page size can't be greater than 10"
     );
  }
}
