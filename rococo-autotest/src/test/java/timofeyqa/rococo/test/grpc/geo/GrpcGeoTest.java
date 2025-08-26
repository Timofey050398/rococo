package timofeyqa.rococo.test.grpc.geo;

import com.google.protobuf.Empty;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.grpc.rococo.GeoListResponse;
import timofeyqa.grpc.rococo.GeoResponse;
import timofeyqa.grpc.rococo.Uuid;
import timofeyqa.grpc.rococo.UuidList;
import timofeyqa.rococo.data.entity.Country;
import timofeyqa.rococo.mapper.GrpcMapper;
import timofeyqa.rococo.model.rest.CountryJson;
import timofeyqa.rococo.service.db.CountryDbClient;
import timofeyqa.rococo.test.grpc.BaseGrpcTest;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Тесты grpc-сервиса geo")
class GrpcGeoTest extends BaseGrpcTest {

  private static final List<CountryJson> list = new CountryDbClient().getAllCountries();
  private final CountryJson france = list.stream()
      .filter(country -> country.name().equals(Country.FRANCE.getName()))
      .findFirst()
      .orElseThrow();
  private final UUID spainId = list.stream()
      .filter(country -> country.name().equals(Country.SPAIN.getName()))
      .findFirst()
      .orElseThrow()
      .id();
  private final GrpcMapper grpcMapper = GrpcMapper.INSTANCE;

  @Test
  @DisplayName("Корректное получение гео по UUID (France)")
  void correctGetGeoTest() {
    Uuid request = grpcMapper.toGrpcUuid(france.id());
    GeoResponse response = geoStub.getGeo(request);

    assertEquals(france.id().toString(), response.getId());
    assertEquals(france.name(), response.getName());
  }

  @Test
  @DisplayName("NOT_FOUND при запросе гео по случайному UUID")
  void geoByUuidNotFoundTest() {
    Uuid request = grpcMapper.toGrpcUuid(UUID.randomUUID());
    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> geoStub.getGeo(request));

    assertEquals("NOT_FOUND", ex.getStatus().getCode().name());
  }

  @Test
  @DisplayName("INVALID_ARGUMENT при запросе гео с некорректным UUID")
  void geoByUuidIllegalUuidTest() {
    Uuid request = Uuid.newBuilder().setUuid("abc").build();
    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> geoStub.getGeo(request));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
  }

  @Test
  @DisplayName("Корректное получение списка всех гео (отсортирован по имени)")
  void correctGetAllTest() {
    GeoListResponse response = geoStub.getAll(Empty.newBuilder().build());

    List<GeoResponse> actual = response.getGeoList();
    List<GeoResponse> expected = new ArrayList<>(actual);
    expected.sort(Comparator.comparing(GeoResponse::getName));
    assertEquals(list.size(), actual.size());
    assertEquals(actual, expected);
  }

  @Test
  @DisplayName("Корректное получение гео по списку UUID (France, Spain)")
  void correctGetGeosByUuidsTest() {
    UuidList request = grpcMapper.toGrpcUuidList(List.of(france.id(), spainId));

    GeoListResponse response = geoStub.getGeosByUuids(request);

    Set<CountryJson> actual = response.getGeoList()
        .stream()
        .map(geoList -> new CountryJson(UUID.fromString(geoList.getId()), geoList.getName()))
        .collect(Collectors.toSet());

    Set<CountryJson> expected = list.stream()
        .filter(country -> country.id().equals(france.id()) || country.id().equals(spainId))
        .collect(Collectors.toSet());

    assertEquals(actual, expected);
  }

  @Test
  @DisplayName("Частичное совпадение: найдено только одно гео по списку UUID")
  void getGeosByUuidsPartialFoundTest() {
    UuidList request = grpcMapper.toGrpcUuidList(List.of(france.id(), UUID.randomUUID()));

    GeoListResponse response = geoStub.getGeosByUuids(request);

    assertEquals(1, response.getGeoCount());
    assertEquals("Франция", response.getGeoList().getFirst().getName());
  }

  @Test
  @DisplayName("INVALID_ARGUMENT при передаче списка UUID с некорректным значением")
  void getGeosByUuidsIllegalUuidTest() {
    UuidList request = UuidList.newBuilder()
        .addUuid(grpcMapper.toGrpcUuid(france.id()))
        .addUuid(Uuid.newBuilder().setUuid("abcd"))
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> geoStub.getGeosByUuids(request));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
  }
}
