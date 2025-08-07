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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GrpcMuseumServiceTest {

  @Mock
  private MuseumRepository museumRepository;

  @InjectMocks
  private GrpcMuseumService grpcMuseumService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getMuseum_existingId_returnsMuseum() {
    UUID id = UUID.randomUUID();
    MuseumEntity entity = new MuseumEntity();
    entity.setId(id);
    entity.setTitle("Title");
    entity.setDescription("Description");
    entity.setCity("City");
    entity.setPhoto(new byte[]{1, 2, 3});
    entity.setCountryId(UUID.randomUUID());

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
    MuseumEntity entity1 = new MuseumEntity();
    entity1.setId(UUID.randomUUID());
    entity1.setTitle("Title1");
    entity1.setDescription("Desc1");
    entity1.setCity("City1");
    entity1.setCountryId(UUID.randomUUID());

    MuseumEntity entity2 = new MuseumEntity();
    entity2.setId(UUID.randomUUID());
    entity2.setTitle("Title2");
    entity2.setDescription("Desc2");
    entity2.setCity("City2");
    entity2.setCountryId(UUID.randomUUID());

    List<MuseumEntity> entities = List.of(entity1, entity2);
    Pageable pageable = PageRequest.of(0, 2);
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

    MuseumEntity entity1 = new MuseumEntity();
    entity1.setId(id1);
    entity1.setTitle("Title1");
    entity1.setCountryId(UUID.randomUUID());

    MuseumEntity entity2 = new MuseumEntity();
    entity2.setId(id2);
    entity2.setTitle("Title2");
    entity2.setCountryId(UUID.randomUUID());

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

    MuseumEntity existing = new MuseumEntity();
    existing.setId(id);
    existing.setTitle("OldTitle");
    existing.setDescription("OldDesc");
    existing.setCity("OldCity");
    existing.setPhoto(new byte[]{1, 2});
    existing.setCountryId(UUID.randomUUID());

    when(museumRepository.findById(id)).thenReturn(Optional.of(existing));
    when(museumRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    Museum request = Museum.newBuilder()
        .setId(id.toString())
        .setTitle("NewTitle")
        .setDescription("NewDesc")
        .setCity("NewCity")
        .setPhoto(ByteString.copyFrom(new byte[]{3, 4, 5}))
        .setCountryId(UUID.randomUUID().toString())
        .build();

    StreamObserver<Museum> observer = mock(StreamObserver.class);

    grpcMuseumService.updateMuseum(request, observer);

    ArgumentCaptor<Museum> captor = ArgumentCaptor.forClass(Museum.class);
    verify(observer).onNext(captor.capture());
    verify(observer).onCompleted();

    Museum updated = captor.getValue();

    assertEquals("NewTitle", updated.getTitle());
    assertEquals("NewDesc", updated.getDescription());
    assertEquals("NewCity", updated.getCity());
    assertEquals(request.getPhoto(), updated.getPhoto());
    assertEquals(request.getCountryId(), updated.getCountryId());
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

