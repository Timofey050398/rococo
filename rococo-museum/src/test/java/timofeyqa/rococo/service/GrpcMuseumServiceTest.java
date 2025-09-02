package timofeyqa.rococo.service;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Pageable;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.data.MuseumEntity;
import timofeyqa.rococo.data.repository.MuseumRepository;
import timofeyqa.rococo.mappers.MuseumMapper;
import timofeyqa.rococo.mappers.MuseumPatcher;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
class GrpcMuseumServiceTest {

  @Mock
  private MuseumRepository museumRepository;

  @Mock
  private MuseumMapper museumMapper;

  @Mock
  private MuseumPatcher museumPatcher;

  @InjectMocks
  private GrpcMuseumService grpcMuseumService;

  @Mock
  private RococoGeoServiceGrpc.RococoGeoServiceBlockingStub geoBlockingStub;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getMuseum_existingId_returnsMuseum() {
    UUID id = UUID.randomUUID();
    MuseumEntity entity = new MuseumEntity()
        .setId(id)
        .setTitle("Title")
        .setDescription("Description")
        .setCity("City")
        .setPhoto(new byte[]{1, 2, 3})
        .setCountryId(UUID.randomUUID());

    when(museumRepository.findById(id)).thenReturn(Optional.of(entity));

    StreamObserver<Museum> observer = mock(StreamObserver.class);

    grpcMuseumService.getMuseum(Uuid.newBuilder().setUuid(id.toString()).build(), observer);

    ArgumentCaptor<Museum> captor = ArgumentCaptor.forClass(Museum.class);
    verify(observer).onNext(captor.capture());
    verify(observer).onCompleted();

    Museum museum = captor.getValue();
    assertEquals(id.toString(), museum.getId());
    assertEquals("Title", museum.getTitle());
    assertEquals("Description", museum.getDescription());
    assertEquals("City", museum.getCity());
    assertEquals(ByteString.copyFrom(new byte[]{1, 2, 3}), museum.getPhoto());
    assertEquals(entity.getCountryId().toString(), museum.getCountryId());
  }

  @Test
  void getMuseum_notFound_throws() {
    UUID id = UUID.randomUUID();
    when(museumRepository.findById(id)).thenReturn(Optional.empty());
    StreamObserver<Museum> observer = mock(StreamObserver.class);

    IllegalStateException ex = assertThrows(IllegalStateException.class,
        () -> grpcMuseumService.getMuseum(Uuid.newBuilder().setUuid(id.toString()).build(), observer));

    assertTrue(ex.getMessage().contains("Museum not found"));
    verify(observer, never()).onNext(any());
    verify(observer, never()).onCompleted();
  }

  @Test
  void getMuseumPage_returnsPage() {
    MuseumEntity entity1 = new MuseumEntity()
        .setId(UUID.randomUUID())
        .setTitle("Title1")
        .setDescription("Desc1")
        .setCity("City1")
        .setCountryId(UUID.randomUUID());

    MuseumEntity entity2 = new MuseumEntity()
        .setId(UUID.randomUUID())
        .setTitle("Title2")
        .setDescription("Desc2")
        .setCity("City2")
        .setCountryId(UUID.randomUUID());

    List<MuseumEntity> entities = List.of(entity1, entity2);
    Pageable pageable = PageRequest.of(0, 2, Sort.by("title").ascending());
    Page<MuseumEntity> page = new PageImpl<>(entities, pageable, 2);

    when(museumRepository.findAll(pageable)).thenReturn(page);

    StreamObserver<PageMuseum> observer = mock(StreamObserver.class);

    grpcMuseumService.getMuseumPage(timofeyqa.grpc.rococo.Pageable.newBuilder().setPage(0).setSize(2).build(), observer);

    ArgumentCaptor<PageMuseum> captor = ArgumentCaptor.forClass(PageMuseum.class);
    verify(observer).onNext(captor.capture());
    verify(observer).onCompleted();

    PageMuseum response = captor.getValue();
    assertEquals(1, response.getTotalPages());
    assertEquals(2, response.getTotalElements());
    assertEquals(2, response.getMuseumsCount());
    assertEquals(entity1.getId().toString(), response.getMuseums(0).getId());
    assertEquals(entity2.getId().toString(), response.getMuseums(1).getId());
  }

  @Test
  void getMuseumsByUuids_returnsList() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    MuseumEntity entity1 = new MuseumEntity()
        .setId(id1)
        .setTitle("Title1")
        .setCountryId(UUID.randomUUID());

    MuseumEntity entity2 = new MuseumEntity()
        .setId(id2)
        .setTitle("Title2")
        .setCountryId(UUID.randomUUID());

    List<UUID> ids = List.of(id1, id2);
    List<MuseumEntity> entities = List.of(entity1, entity2);

    when(museumRepository.findAllByIdIn(ids)).thenReturn(entities);

    UuidList request = UuidList.newBuilder()
        .addUuid(Uuid.newBuilder().setUuid(id1.toString()))
        .addUuid(Uuid.newBuilder().setUuid(id2.toString()))
        .build();

    StreamObserver<MuseumList> observer = mock(StreamObserver.class);

    grpcMuseumService.getMuseumsByUuids(request, observer);

    ArgumentCaptor<MuseumList> captor = ArgumentCaptor.forClass(MuseumList.class);
    verify(observer).onNext(captor.capture());
    verify(observer).onCompleted();

    MuseumList response = captor.getValue();
    assertEquals(2, response.getMuseumsCount());
    assertEquals(id1.toString(), response.getMuseums(0).getId());
    assertEquals(id2.toString(), response.getMuseums(1).getId());
  }

  @Test
  void updateMuseum_updatesAndReturns() {
    UUID id = UUID.randomUUID();

    // Существующая сущность
    MuseumEntity existing = new MuseumEntity()
        .setId(id)
        .setTitle("OldTitle")
        .setDescription("OldDesc")
        .setCity("OldCity")
        .setPhoto(new byte[]{1, 2})
        .setCountryId(UUID.randomUUID());

    // Моки репозитория
    when(museumRepository.findById(id)).thenReturn(Optional.of(existing));
    when(museumRepository.findByTitle(any())).thenReturn(Optional.empty());
    when(museumRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    // Мок mapper: преобразует GRPC в сущность
    MuseumEntity mappedEntity = new MuseumEntity()
        .setId(id)
        .setTitle("NewTitle")
        .setDescription("NewDesc")
        .setCity("NewCity")
        .setPhoto(new byte[]{3, 4, 5})
        .setCountryId(UUID.randomUUID());
    when(museumMapper.addEntityFromMuseum(any(Museum.class))).thenReturn(mappedEntity);

    // Мок patcher: применяет изменения к существующей сущности
    doAnswer(invocation -> {
      Museum source = invocation.getArgument(0);
      MuseumEntity target = invocation.getArgument(1);
      target.setTitle(source.getTitle());
      target.setDescription(source.getDescription());
      target.setCity(source.getCity());
      target.setPhoto(source.getPhoto().toByteArray());
      target.setCountryId(UUID.fromString(source.getCountryId()));
      return null;
    }).when(museumPatcher).patch(any(Museum.class), any(MuseumEntity.class), any(MuseumMapper.class));

    // GRPC-запрос
    Museum request = Museum.newBuilder()
        .setId(id.toString())
        .setTitle("NewTitle")
        .setDescription("NewDesc")
        .setCity("NewCity")
        .setPhoto(ByteString.copyFrom(new byte[]{3, 4, 5}))
        .setCountryId(mappedEntity.getCountryId().toString())
        .build();

    when(geoBlockingStub.getGeo(any(Uuid.class))).thenReturn(
        GeoResponse.newBuilder()
                .setId(mappedEntity.getCountryId().toString())
                .setName("TestCountry")
                .build()
    );

    StreamObserver<Museum> observer = mock(StreamObserver.class);

    // Вызов сервиса
    grpcMuseumService.updateMuseum(request, observer);

    // Проверка ответа
    ArgumentCaptor<Museum> captor = ArgumentCaptor.forClass(Museum.class);
    verify(observer).onNext(captor.capture());
    verify(observer).onCompleted();

    Museum updated = captor.getValue();
    assertEquals("NewTitle", updated.getTitle());
    assertEquals("NewDesc", updated.getDescription());
    assertEquals("NewCity", updated.getCity());
    assertArrayEquals(new byte[]{3, 4, 5}, updated.getPhoto().toByteArray());
    assertEquals(mappedEntity.getCountryId().toString(), updated.getCountryId());
  }


  @Test
  void updateMuseum_notFound_throws() {
    UUID id = UUID.randomUUID();
    when(museumRepository.findById(id)).thenReturn(Optional.empty());

    Museum request = Museum.newBuilder()
        .setId(id.toString())
        .build();

    StreamObserver<Museum> observer = mock(StreamObserver.class);

    IllegalStateException ex = assertThrows(IllegalStateException.class,
        () -> grpcMuseumService.updateMuseum(request, observer));

    assertTrue(ex.getMessage().contains("Museum not found"));
    verify(observer, never()).onNext(any());
    verify(observer, never()).onCompleted();
  }
}

