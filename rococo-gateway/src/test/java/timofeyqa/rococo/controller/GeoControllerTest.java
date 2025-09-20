package timofeyqa.rococo.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import timofeyqa.rococo.model.CountryJson;
import timofeyqa.rococo.model.page.RestPage;
import timofeyqa.rococo.service.api.grpc.GrpcGeoClient;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeoControllerTest {

  @Mock
  private GrpcGeoClient grpcGeoClient;

  @InjectMocks
  private GeoController geoController;

  @Test
  void allCountries_returnsPagedResults() {
    List<CountryJson> countries = List.of(
        new CountryJson(UUID.randomUUID(), "Country1"),
        new CountryJson(UUID.randomUUID(), "Country2"),
        new CountryJson(UUID.randomUUID(), "Country3")
    );

    when(grpcGeoClient.allCountries()).thenReturn(countries);

    Pageable pageable = PageRequest.of(0, 2); // первая страница, размер 2
    RestPage<CountryJson> page = geoController.allCountries(pageable);

    assertThat(page.getContent()).hasSize(2);
    assertThat(page.getTotalElements()).isEqualTo(3);
    assertThat(page.getPageable().getPageNumber()).isEqualTo(0);
  }

  @Test
  void allCountries_returnsLastPageWithFewerItems() {
    List<CountryJson> countries = List.of(
        new CountryJson(UUID.randomUUID(), "Country1"),
        new CountryJson(UUID.randomUUID(), "Country2"),
        new CountryJson(UUID.randomUUID(), "Country3")
    );

    when(grpcGeoClient.allCountries()).thenReturn(countries);

    Pageable pageable = PageRequest.of(1, 2); // вторая страница, размер 2
    RestPage<CountryJson> page = geoController.allCountries(pageable);

    assertThat(page.getContent()).hasSize(1);
    assertThat(page.getTotalElements()).isEqualTo(3);
    assertThat(page.getPageable().getPageNumber()).isEqualTo(1);
  }

  @Test
  void allCountries_withEmptyList_returnsEmptyPage() {
    when(grpcGeoClient.allCountries()).thenReturn(List.of());

    Pageable pageable = PageRequest.of(0, 2);
    RestPage<CountryJson> page = geoController.allCountries(pageable);

    assertThat(page.getContent()).isEmpty();
    assertThat(page.getTotalElements()).isEqualTo(0);
  }

  @Test
  void allCountries_withFromIndexGreaterThanSize_returnsEmptyPage() {
    List<CountryJson> countries = List.of(
        new CountryJson(UUID.randomUUID(), "Country1")
    );

    when(grpcGeoClient.allCountries()).thenReturn(countries);

    Pageable pageable = PageRequest.of(10, 5); // сдвиг выходит за пределы списка
    RestPage<CountryJson> page = geoController.allCountries(pageable);

    assertThat(page.getContent()).isEmpty();
    assertThat(page.getTotalElements()).isEqualTo(1);
  }
}

