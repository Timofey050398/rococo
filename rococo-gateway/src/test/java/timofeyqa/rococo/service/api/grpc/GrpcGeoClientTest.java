package timofeyqa.rococo.service.api.grpc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.google.protobuf.Empty;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import timofeyqa.grpc.rococo.GeoResponse;
import timofeyqa.grpc.rococo.GeoListResponse;
import timofeyqa.grpc.rococo.RococoGeoServiceGrpc;
import timofeyqa.rococo.ex.BadRequestException;
import timofeyqa.rococo.model.CountryJson;
import timofeyqa.rococo.model.GeoJson;
import timofeyqa.rococo.model.MuseumJson;

import java.util.*;
import java.util.concurrent.CompletableFuture;

class GrpcGeoClientTest {

  @Mock
  private RococoGeoServiceGrpc.RococoGeoServiceBlockingStub blockingStub;

  @Mock
  private RococoGeoServiceGrpc.RococoGeoServiceFutureStub futureStub;

  @InjectMocks
  private GrpcGeoClient grpcGeoClient;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void allCountries_returnsListOfCountries() {
    GeoResponse geo1 = GeoResponse.newBuilder().setId(UUID.randomUUID().toString()).setName("Country1").build();
    GeoResponse geo2 = GeoResponse.newBuilder().setId(UUID.randomUUID().toString()).setName("Country2").build();

    GeoListResponse response = GeoListResponse.newBuilder()
        .addGeo(geo1)
        .addGeo(geo2)
        .build();

    when(blockingStub.getAll(Empty.getDefaultInstance())).thenReturn(response);

    List<CountryJson> countries = grpcGeoClient.allCountries();

    assertEquals(2, countries.size());
    assertEquals("Country1", countries.get(0).name());
    assertEquals("Country2", countries.get(1).name());
  }

  @Test
  void allCountries_whenGrpcThrows_throwsResponseStatusException() {
    when(blockingStub.getAll(any())).thenThrow(new StatusRuntimeException(io.grpc.Status.UNAVAILABLE));

    var ex = assertThrows(org.springframework.web.server.ResponseStatusException.class,
        () -> grpcGeoClient.allCountries());

    assertTrue(ex.getMessage().contains("The gRPC operation was cancelled"));
  }

  @Test
  void getById_returnsCountryJson() throws Exception {
    UUID id = UUID.randomUUID();
    GeoResponse grpcResponse = GeoResponse.newBuilder()
        .setId(id.toString())
        .setName("TestCountry")
        .build();

    when(futureStub.getGeo(any())).thenReturn(com.google.common.util.concurrent.Futures.immediateFuture(grpcResponse));

    CompletableFuture<CountryJson> future = grpcGeoClient.getById(id);

    CountryJson country = future.get();

    assertNotNull(country);
    assertEquals(id, country.id());
    assertEquals("TestCountry", country.name());
  }

  @Test
  void getById_nullId_returnsCompletedFutureWithNull() throws Exception {
    CompletableFuture<CountryJson> future = grpcGeoClient.getById(null);
    assertNull(future.get());
  }

  @Test
  void getMuseumGeo_returnsGeoJsonWithUpdatedCountry() throws Exception {
    UUID countryId = UUID.randomUUID();
    CountryJson inputCountry = new CountryJson(countryId, "OldName");
    GeoJson inputGeo = GeoJson.builder().country(inputCountry).build();
    MuseumJson museum = MuseumJson.builder().geo(inputGeo).build();

    GeoResponse grpcResponse = GeoResponse.newBuilder()
        .setId(countryId.toString())
        .setName("NewCountryName")
        .build();

    when(futureStub.getGeo(any())).thenReturn(com.google.common.util.concurrent.Futures.immediateFuture(grpcResponse));

    CompletableFuture<GeoJson> future = grpcGeoClient.getMuseumGeo(museum);
    GeoJson updatedGeo = future.get();

    assertNotNull(updatedGeo);
    assertNotNull(updatedGeo.country());
    assertEquals(countryId, updatedGeo.country().id());
    assertEquals("NewCountryName", updatedGeo.country().name());
  }

  @Test
  void getMuseumGeo_whenNoGeo_returnsCompletedFutureWithNull() throws Exception {
    MuseumJson museum = MuseumJson.builder().geo(null).build();

    CompletableFuture<GeoJson> future = grpcGeoClient.getMuseumGeo(museum);

    assertNull(future.get());
  }

  @Test
  void getCountriesByIds_returnsList() throws Exception {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    GeoResponse grpc1 = GeoResponse.newBuilder().setId(id1.toString()).setName("C1").build();
    GeoResponse grpc2 = GeoResponse.newBuilder().setId(id2.toString()).setName("C2").build();

    GeoListResponse response = GeoListResponse.newBuilder().addGeo(grpc1).addGeo(grpc2).build();

    when(futureStub.getGeosByUuids(any())).thenReturn(com.google.common.util.concurrent.Futures.immediateFuture(response));

    CompletableFuture<List<CountryJson>> future = grpcGeoClient.getCountriesByIds(List.of(id1, id2));
    List<CountryJson> countries = future.get();

    assertEquals(2, countries.size());
    assertEquals("C1", countries.get(0).name());
    assertEquals("C2", countries.get(1).name());
  }

  @Test
  void getCountriesByIds_emptyIds_returnsEmptyList() throws Exception {
    CompletableFuture<List<CountryJson>> future = grpcGeoClient.getCountriesByIds(List.of());
    List<CountryJson> countries = future.get();
    assertTrue(countries.isEmpty());
  }

  @Test
  @Disabled
  void validateCountry_validatesSuccessfully() {
    UUID id = UUID.randomUUID();
    CountryJson country = new CountryJson(id, "CorrectName");

    GeoResponse grpcResponse = GeoResponse.newBuilder().setId(id.toString()).setName("CorrectName").build();
    when(blockingStub.getGeo(any())).thenReturn(grpcResponse);

    assertDoesNotThrow(() -> grpcGeoClient.validateCountry(country));
  }

  @Test
  @Disabled
  void validateCountry_throwsBadRequestException_whenNameMismatch() {
    UUID id = UUID.randomUUID();
    CountryJson country = new CountryJson(id, "WrongName");

    GeoResponse grpcResponse = GeoResponse.newBuilder().setId(id.toString()).setName("ActualName").build();
    when(blockingStub.getGeo(any())).thenReturn(grpcResponse);

    BadRequestException ex = assertThrows(BadRequestException.class, () -> grpcGeoClient.validateCountry(country));
    assertTrue(ex.getMessage().contains("Country with provided combination of id and name does not exist"));
  }

  @Test
  void validateCountry_nullCountry_doesNothing() {
    assertDoesNotThrow(() -> grpcGeoClient.validateCountry(null));
  }

  @Test
  void validateCountry_nullId_doesNothing() {
    CountryJson country = new CountryJson(null, "SomeName");
    assertDoesNotThrow(() -> grpcGeoClient.validateCountry(country));
  }
}
