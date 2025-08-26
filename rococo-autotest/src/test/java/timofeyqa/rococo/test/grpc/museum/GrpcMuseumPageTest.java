package timofeyqa.rococo.test.grpc.museum;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.jupiter.annotation.Museum;
import timofeyqa.rococo.test.grpc.BaseGrpcTest;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("gRPC: Пагинация музеев")
class GrpcMuseumPageTest extends BaseGrpcTest {

  private static final String FILTER_FIELD = "Museum F";

  @Test
  @Content(museums = {
      @Museum(title = "Museum A", description = "Desc D", city = "City A"),
      @Museum(title = "Museum B", description = "Desc C", city = "City B"),
      @Museum(title = "Museum C", description = "Desc B", city = "City C"),
      @Museum(title = "Museum D", description = "Desc A", city = "City D")
  })
  @DisplayName("Получение первой страницы")
  void getFirstPageTest() {
    Pageable request = Pageable.newBuilder()
        .setPage(0)
        .setSize(2)
        .build();

    PageMuseum response = museumStub.getMuseumPage(request);

    assertEquals(2, response.getMuseumsCount());
    assertTrue(response.getTotalElements() >= 4);
    assertTrue(response.getTotalPages() >= 2);
  }

  @Test
  @DisplayName("Фильтрация музеев по названию (contains)")
  @Content(museums = {
      @Museum(title = "Museum E", description = "Desc H", city = "City E"),
      @Museum(title = FILTER_FIELD, description = "Desc G", city = "City F"),
      @Museum(title = "Museum G", description = "Desc F", city = "City G"),
      @Museum(title = "Museum H", description = "Desc E", city = "City H")
  })
  void filterMuseumsTest() {
    Pageable request = Pageable.newBuilder()
        .setPage(0)
        .setSize(10)
        .setFilterField(FILTER_FIELD)
        .build();

    PageMuseum response = museumStub.getMuseumPage(request);

    assertTrue(response.getMuseumsList().stream()
        .allMatch(m -> m.getTitle().contains(FILTER_FIELD)));
  }
}
