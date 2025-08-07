package timofeyqa.rococo.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.data.CountryEntity;
import timofeyqa.rococo.data.repository.CountryRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GrpcGeoServiceTest {

  @Mock
  private CountryRepository countryRepository;

  @Mock
  private StreamObserver<GeoResponse> geoResponseObserver;

  @Mock
  private StreamObserver<GeoListResponse> geoListResponseObserver;

  @Captor
  private ArgumentCaptor<GeoResponse> geoResponseCaptor;

  @Captor
  private ArgumentCaptor<GeoListResponse> geoListResponseCaptor;

  private GrpcGeoService grpcGeoService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    grpcGeoService = new GrpcGeoService(countryRepository);
  }

  @Test
  void getGeo_existingId_returnsGeoResponse() {
    UUID id = UUID.randomUUID();
    CountryEntity country = new CountryEntity();
    country.setId(id);
    country.setName("Russia");

    when(countryRepository.findById(id)).thenReturn(Optional.of(country));

    Uuid request = Uuid.newBuilder().setUuid(id.toString()).build();

    grpcGeoService.getGeo(request, geoResponseObserver);

    verify(geoResponseObserver).onNext(geoResponseCaptor.capture());
    verify(geoResponseObserver).onCompleted();

    GeoResponse response = geoResponseCaptor.getValue();
    assertEquals(id.toString(), response.getId());
    assertEquals("Russia", response.getName());
  }

  @Test
  void getGeo_nonExistingId_throwsEntityNotFoundException() {
    UUID id = UUID.randomUUID();
    when(countryRepository.findById(id)).thenReturn(Optional.empty());

    Uuid request = Uuid.newBuilder().setUuid(id.toString()).build();

    // Проверяем, что выбрасывается исключение (будет обрабатываться grpc глобально)
    assertThrows(IllegalStateException.class, () -> grpcGeoService.getGeo(request, geoResponseObserver));

    verify(geoResponseObserver, never()).onNext(any());
    verify(geoResponseObserver, never()).onCompleted();
  }

  @Test
  void getAll_returnsAllCountries() {
    CountryEntity c1 = new CountryEntity();
    c1.setId(UUID.randomUUID());
    c1.setName("Country1");

    CountryEntity c2 = new CountryEntity();
    c2.setId(UUID.randomUUID());
    c2.setName("Country2");

    when(countryRepository.findAll()).thenReturn(List.of(c1, c2));

    grpcGeoService.getAll(Empty.getDefaultInstance(), geoListResponseObserver);

    verify(geoListResponseObserver).onNext(geoListResponseCaptor.capture());
    verify(geoListResponseObserver).onCompleted();

    GeoListResponse response = geoListResponseCaptor.getValue();
    assertEquals(2, response.getGeoCount());
    assertTrue(response.getGeoList().stream().anyMatch(g -> g.getName().equals("Country1")));
    assertTrue(response.getGeoList().stream().anyMatch(g -> g.getName().equals("Country2")));
  }

  @Test
  void getGeosByUuids_returnsFilteredCountries() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    CountryEntity c1 = new CountryEntity();
    c1.setId(id1);
    c1.setName("Country1");

    CountryEntity c2 = new CountryEntity();
    c2.setId(id2);
    c2.setName("Country2");

    when(countryRepository.findAllByIdIn(List.of(id1, id2))).thenReturn(List.of(c1, c2));

    UuidList request = UuidList.newBuilder()
        .addUuid(Uuid.newBuilder().setUuid(id1.toString()).build())
        .addUuid(Uuid.newBuilder().setUuid(id2.toString()).build())
        .build();

    grpcGeoService.getGeosByUuids(request, geoListResponseObserver);

    verify(geoListResponseObserver).onNext(geoListResponseCaptor.capture());
    verify(geoListResponseObserver).onCompleted();

    GeoListResponse response = geoListResponseCaptor.getValue();
    assertEquals(2, response.getGeoCount());
    assertTrue(response.getGeoList().stream().anyMatch(g -> g.getName().equals("Country1")));
    assertTrue(response.getGeoList().stream().anyMatch(g -> g.getName().equals("Country2")));
  }
}

