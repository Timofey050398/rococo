package timofeyqa.rococo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import timofeyqa.grpc.rococo.Artist;
import timofeyqa.grpc.rococo.PageArtistResponse;
import timofeyqa.grpc.rococo.Pageable;
import timofeyqa.grpc.rococo.Uuid;
import timofeyqa.grpc.rococo.UuidList;
import timofeyqa.grpc.rococo.ArtistList;
import timofeyqa.rococo.data.ArtistEntity;
import timofeyqa.rococo.data.repository.ArtistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import timofeyqa.rococo.mappers.ArtistMapper;
import timofeyqa.rococo.mappers.GrpcMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

class GrpcArtistServiceTest {

  private ArtistRepository artistRepository;
  private GrpcArtistService grpcArtistService;
  private StreamObserver<Artist> artistObserver;
  private StreamObserver<PageArtistResponse> pageObserver;
  private StreamObserver<ArtistList> artistListObserver;
  private GrpcMapper grpcMapper;
  private ArtistMapper artistMapper;

  @BeforeEach
  void setup() {
    artistRepository = mock(ArtistRepository.class);
    artistMapper = mock(ArtistMapper.class);
    grpcMapper = mock(GrpcMapper.class);
    grpcArtistService = new GrpcArtistService(artistRepository, artistMapper, grpcMapper);
    artistObserver = mock(StreamObserver.class);
    pageObserver = mock(StreamObserver.class);
    artistListObserver = mock(StreamObserver.class);
  }

  @Test
  void getArtist_existingId_returnsArtist() {
    UUID id = UUID.randomUUID();
    ArtistEntity entity = new ArtistEntity();
    entity.setId(id);
    entity.setName("Artist Name");
    entity.setBiography("Bio");
    entity.setPhoto(new byte[]{1,2,3});

    when(artistRepository.findById(id)).thenReturn(Optional.of(entity));

    Uuid request = Uuid.newBuilder().setUuid(id.toString()).build();

    grpcArtistService.getArtist(request, artistObserver);

    ArgumentCaptor<Artist> captor = ArgumentCaptor.forClass(Artist.class);
    verify(artistObserver).onNext(captor.capture());
    verify(artistObserver).onCompleted();

    Artist response = captor.getValue();
    assertEquals(id.toString(), response.getId());
    assertEquals("Artist Name", response.getName());
    assertEquals("Bio", response.getBiography());
    assertArrayEquals(new byte[]{1,2,3}, response.getPhoto().toByteArray());
  }

  @Test
  void getArtist_nonExistingId_throwsException() {
    UUID id = UUID.randomUUID();
    when(artistRepository.findById(id)).thenReturn(Optional.empty());
    Uuid request = Uuid.newBuilder().setUuid(id.toString()).build();

    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> grpcArtistService.getArtist(request, artistObserver));

    assertTrue(exception.getMessage().contains("Artist not found"));
    verifyNoInteractions(artistObserver);
  }

  @Test
  void getArtistPage_returnsPage() {
    ArtistEntity entity1 = new ArtistEntity();
    entity1.setId(UUID.randomUUID());
    entity1.setName("A1");
    entity1.setBiography("Bio1");
    entity1.setPhoto(null);

    ArtistEntity entity2 = new ArtistEntity();
    entity2.setId(UUID.randomUUID());
    entity2.setName("A2");
    entity2.setBiography("Bio2");
    entity2.setPhoto(null);

    List<ArtistEntity> entities = List.of(entity1, entity2);
    Page<ArtistEntity> page = new PageImpl<>(entities, PageRequest.of(0, 2), 10);

    when(artistRepository.findAll(PageRequest.of(0, 2))).thenReturn(page);

    Pageable request = Pageable.newBuilder().setPage(0).setSize(2).build();

    grpcArtistService.getArtistPage(request, pageObserver);

    ArgumentCaptor<PageArtistResponse> captor = ArgumentCaptor.forClass(PageArtistResponse.class);
    verify(pageObserver).onNext(captor.capture());
    verify(pageObserver).onCompleted();

    PageArtistResponse response = captor.getValue();
    assertEquals(10, response.getTotalElements());
    assertEquals(5, response.getTotalPages()); // 10 / 2 = 5 pages
    assertEquals(2, response.getArtistsCount());
  }

  @Test
  void getArtistsByUuids_returnsArtists() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    ArtistEntity entity1 = new ArtistEntity();
    entity1.setId(id1);
    entity1.setName("Name1");
    entity1.setBiography("Bio1");

    ArtistEntity entity2 = new ArtistEntity();
    entity2.setId(id2);
    entity2.setName("Name2");
    entity2.setBiography("Bio2");

    when(artistRepository.findAllByIdIn(List.of(id1, id2))).thenReturn(List.of(entity1, entity2));

    Uuid uuid1 = Uuid.newBuilder().setUuid(id1.toString()).build();
    Uuid uuid2 = Uuid.newBuilder().setUuid(id2.toString()).build();

    UuidList request = UuidList.newBuilder().addUuid(uuid1).addUuid(uuid2).build();

    grpcArtistService.getArtistsByUuids(request, artistListObserver);

    ArgumentCaptor<ArtistList> captor = ArgumentCaptor.forClass(ArtistList.class);
    verify(artistListObserver).onNext(captor.capture());
    verify(artistListObserver).onCompleted();

    ArtistList response = captor.getValue();
    assertEquals(2, response.getArtistsCount());
    assertTrue(response.getArtistsList().stream().anyMatch(a -> a.getId().equals(id1.toString())));
    assertTrue(response.getArtistsList().stream().anyMatch(a -> a.getId().equals(id2.toString())));
  }

  @Test
  void updateArtist_updatesFields() {
    UUID id = UUID.randomUUID();
    ArtistEntity existing = new ArtistEntity();
    existing.setId(id);
    existing.setName("Old Name");
    existing.setBiography("Old Bio");
    existing.setPhoto(new byte[]{1, 1, 1});

    when(artistRepository.findById(id)).thenReturn(Optional.of(existing));
    when(artistRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    Artist request = Artist.newBuilder()
        .setId(id.toString())
        .setName("New Name")
        .setBiography("New Bio")
        .setPhoto(com.google.protobuf.ByteString.copyFrom(new byte[]{2, 2, 2}))
        .build();

    grpcArtistService.updateArtist(request, artistObserver);

    ArgumentCaptor<Artist> captor = ArgumentCaptor.forClass(Artist.class);
    verify(artistObserver).onNext(captor.capture());
    verify(artistObserver).onCompleted();

    Artist updated = captor.getValue();
    assertEquals("New Name", updated.getName());
    assertEquals("New Bio", updated.getBiography());
    assertArrayEquals(new byte[]{2, 2, 2}, updated.getPhoto().toByteArray());
  }

  @Test
  void updateArtist_artistNotFound_throwsException() {
    UUID id = UUID.randomUUID();
    when(artistRepository.findById(id)).thenReturn(Optional.empty());

    Artist request = Artist.newBuilder().setId(id.toString()).build();

    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> grpcArtistService.updateArtist(request, artistObserver));

    assertTrue(exception.getMessage().contains("Artist not found"));
    verifyNoInteractions(artistObserver);
  }
}

