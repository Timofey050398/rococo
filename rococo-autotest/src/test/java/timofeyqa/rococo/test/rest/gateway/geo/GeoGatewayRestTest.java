package timofeyqa.rococo.test.rest.gateway.geo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.meta.RestTest;
import timofeyqa.rococo.model.rest.CountryJson;
import timofeyqa.rococo.model.rest.pageable.RestResponsePage;
import timofeyqa.rococo.service.api.GeoRestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Gateway geo rest api")
@RestTest
class GeoGatewayRestTest {

  private final GeoRestClient geoClient = new GeoRestClient();

  @Test
  @DisplayName("Поиск стран: успешный ответ")
  void successgetCountryPageTest() {
    RestResponsePage<CountryJson> response = geoClient.getCountryPage(0, 5);

    assertNotNull(response);
    List<CountryJson> countries = response.getContent();
    assertFalse(countries.isEmpty(), "countries list is empty");


    CountryJson country = countries.getFirst();
    assertAll(
        () -> assertNotNull(country.id(), "id is null"),
        () -> assertNotNull(country.name(), "name is null"),
        () -> assertFalse(country.name().isBlank(), "name is blank")
    );
  }

  @Test
  @DisplayName("Некорректные параметры пагинации возвращают дефолтные значения")
  void invalidPaginationTest() {
    var page = geoClient.getCountryPage(-1, -10);

    assertEquals(0, page.getPageable().getPageNumber());
    assertEquals(10, page.getPageable().getPageSize());
  }

  @Test
  @DisplayName("Поля пагинации корректные")
  void paginationFieldsTest() {
    RestResponsePage<CountryJson> response = geoClient.getCountryPage(0, 2);

    assertAll(
        () -> assertEquals(0, response.getNumber(), "wrong page number"),
        () -> assertEquals(2, response.getSize(), "wrong page size"),
        () -> assertTrue(response.getTotalElements() > 0, "totalElements must be > 0"),
        () -> assertTrue(response.getTotalPages() >= 1, "totalPages must be >= 1")
    );
  }
}
