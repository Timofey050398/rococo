package timofeyqa.rococo.test.grpc.painting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.jupiter.annotation.Painting;
import timofeyqa.rococo.test.grpc.BaseGrpcTest;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("gRPC: Пагинация картин")
class GrpcPaintingPageTest extends BaseGrpcTest {

  private static final String FILTER_FIELD = "Painting F";

  @Test
  @Content(paintings = {
      @Painting(title = "Painting A", description = "Desc D"),
      @Painting(title = "Painting B", description = "Desc C"),
      @Painting(title = "Painting C", description = "Desc B"),
      @Painting(title = "Painting D", description = "Desc A")
  })
  @DisplayName("Получение первой страницы")
  void getFirstPageTest() {
    Pageable request = Pageable.newBuilder()
        .setPage(0)
        .setSize(2)
        .build();

    PagePainting response = paintingStub.getPaintingsPage(request);

    assertEquals(2, response.getPaintingsCount());
    assertTrue(response.getTotalElements() >= 4);
    assertTrue(response.getTotalPages() >= 2);
  }

  @Test
  @DisplayName("Фильтрация картин по названию (contains)")
  @Content(paintings = {
      @Painting(title = "Painting E", description = "Desc H"),
      @Painting(title = FILTER_FIELD, description = "Desc G"),
      @Painting(title = "Painting G", description = "Desc F"),
      @Painting(title = "Painting H", description = "Desc E")
  })
  void filterPaintingsTest() {
    Pageable request = Pageable.newBuilder()
        .setPage(0)
        .setSize(10)
        .setFilterField(FILTER_FIELD)
        .build();

    PagePainting response = paintingStub.getPaintingsPage(request);

    assertTrue(response.getPaintingsList().stream()
        .allMatch(p -> p.getTitle().contains(FILTER_FIELD)));
  }
}