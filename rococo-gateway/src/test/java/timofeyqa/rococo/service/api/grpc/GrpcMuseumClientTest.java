package timofeyqa.rococo.service.api.grpc;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.model.GeoJson;
import timofeyqa.rococo.model.MuseumJson;
import timofeyqa.rococo.model.CountryJson;
import timofeyqa.rococo.model.page.RestPage;

import java.util.*;
import java.util.concurrent.CompletableFuture;

class GrpcMuseumClientTest {

  @Mock
  private RococoMuseumServiceGrpc.RococoMuseumServiceFutureStub museumStub;
  @Mock
  private GrpcGeoClient grpcGeoClient;

  @InjectMocks
  private GrpcMuseumClient client;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getById_validId_returnsMuseumWithGeo() {
    UUID id = UUID.randomUUID();
    Museum grpcMuseum = Museum.newBuilder()
        .setId(id.toString())
        .setTitle("title")
        .setDescription("desc")
        .setCity("city")
        .setCountryId(UUID.randomUUID().toString())
        .build();

    MuseumJson museumJson = MuseumJson.builder()
        .id(id)
        .title("title")
        .description("desc")
        .geo(GeoJson.builder().city("city").country(null).build())
        .build();

    MuseumJson museumWithGeo = museumJson.toBuilder()
        .geo(GeoJson.builder()
            .city("city")
            .country(CountryJson.builder()
                .id(UUID.randomUUID())
                .name("countryName")
                .build())
            .build())
        .build();

    when(museumStub.getMuseum(any(Uuid.class))).thenReturn(
        com.google.common.util.concurrent.Futures.immediateFuture(grpcMuseum)
    );

    when(grpcGeoClient.getMuseumGeo(any(MuseumJson.class)))
        .thenReturn(CompletableFuture.completedFuture(museumWithGeo.geo()));

    CompletableFuture<MuseumJson> future = client.getById(id);

    MuseumJson result = future.join();

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(id);
    assertThat(result.geo().city()).isEqualTo("city");
    assertEquals(result.geo().country(),museumWithGeo.geo().country());

    verify(museumStub).getMuseum(any(Uuid.class));
    verify(grpcGeoClient).getMuseumGeo(any(MuseumJson.class));
  }

  @Test
  void getMuseumPage_returnsEnrichedPage() {
    Pageable pageable = PageRequest.of(0, 10);

    Museum grpcMuseum = Museum.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setTitle("title")
        .setDescription("desc")
        .setCity("city")
        .setCountryId(UUID.randomUUID().toString())
        .build();

    PageMuseum pageMuseum = PageMuseum.newBuilder()
        .addMuseums(grpcMuseum)
        .setTotalElements(1)
        .setTotalPages(1)
        .build();

    when(museumStub.getMuseumPage(any()))
        .thenReturn(com.google.common.util.concurrent.Futures.immediateFuture(pageMuseum));
    when(grpcGeoClient.getCountriesByIds(anyList()))
        .thenReturn(CompletableFuture.completedFuture(List.of(
            CountryJson.builder()
                .id(UUID.fromString(grpcMuseum.getCountryId()))
                .name("countryName")
                .build()
        )));

    CompletableFuture<RestPage<MuseumJson>> future = client.getMuseumPage(pageable, null);

    RestPage<MuseumJson> page = future.join();

    assertThat(page.getContent()).hasSize(1);
    MuseumJson enrichedMuseum = page.getContent().getFirst();
    assertThat(enrichedMuseum.geo().country().name()).isEqualTo("countryName");

    verify(museumStub).getMuseumPage(any());
    verify(grpcGeoClient).getCountriesByIds(anyList());
  }

  @Test
  void getMuseumsByIds_emptyList_returnsEmptyList() {
    CompletableFuture<List<MuseumJson>> future = client.getMuseumsByIds(Collections.emptyList());
    List<MuseumJson> result = future.join();
    assertThat(result).isEmpty();
    verifyNoInteractions(museumStub);
  }

  @Test
  void getMuseumsByIds_returnsEnrichedMuseums() {
    UUID museumId = UUID.randomUUID();
    UUID countryId = UUID.randomUUID();

    Museum grpcMuseum = Museum.newBuilder()
        .setId(museumId.toString())
        .setCountryId(countryId.toString())
        .build();

    MuseumList grpcList = MuseumList.newBuilder()
        .addMuseums(grpcMuseum)
        .build();

    when(museumStub.getMuseumsByUuids(any()))
        .thenReturn(com.google.common.util.concurrent.Futures.immediateFuture(grpcList));

    when(grpcGeoClient.getCountriesByIds(List.of(countryId)))
        .thenReturn(CompletableFuture.completedFuture(List.of(
            CountryJson.builder()
                .id(countryId)
                .name("CountryName")
                .build()
        )));

    CompletableFuture<List<MuseumJson>> future = client.getMuseumsByIds(List.of(museumId));

    List<MuseumJson> result = future.join();

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().geo().country().name()).isEqualTo("CountryName");

    verify(museumStub).getMuseumsByUuids(any());
    verify(grpcGeoClient).getCountriesByIds(anyList());
  }

  @Test
  void updateMuseum_callsBlockingStubAndValidatesCountry() {
    UUID countryId = UUID.randomUUID();
    MuseumJson input = MuseumJson.builder()
        .id(UUID.randomUUID())
        .geo(GeoJson.builder()
            .country(CountryJson.builder()
                .id(countryId)
                .name("countryName")
                .build())
            .build())
        .build();

    // grpc-ответ
    Museum grpcResponse = Museum.newBuilder()
        .setId(input.id().toString())
        .build();

    // Мокаем ListenableFuture для updateMuseum
    ListenableFuture<Museum> grpcFuture = Futures.immediateFuture(grpcResponse);
    when(museumStub.updateMuseum(any())).thenReturn(grpcFuture);

    // Мокаем grpcGeoClient.getMuseumGeo, чтобы он возвращал CompletableFuture
    when(grpcGeoClient.getMuseumGeo(any()))
        .thenReturn(CompletableFuture.completedFuture(input.geo()));

    // Вызов метода
    CompletableFuture<MuseumJson> future = client.updateMuseum(input);
    MuseumJson result = future.join();

    // Проверки
    assertThat(result.id()).isEqualTo(input.id());
    verify(grpcGeoClient).validateCountry(input.geo().country());
    verify(museumStub).updateMuseum(any());
    verify(grpcGeoClient).getMuseumGeo(any()); // Проверяем, что вызов был
  }

}
