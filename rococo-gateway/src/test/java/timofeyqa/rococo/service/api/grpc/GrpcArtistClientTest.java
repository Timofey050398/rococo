package timofeyqa.rococo.service.api.grpc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.model.ArtistJson;
import timofeyqa.rococo.model.page.RestPage;

import static timofeyqa.rococo.mappers.PageableMapper.toGrpcPageable;

class GrpcArtistClientTest {

  @Mock
  private RococoArtistServiceGrpc.RococoArtistServiceFutureStub artistStub;

  @Mock
  private RococoArtistServiceGrpc.RococoArtistServiceBlockingStub artistBlockingStub;

  @InjectMocks
  private GrpcArtistClient grpcArtistClient;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getById_withValidId_returnsArtistJson() {
    UUID id = UUID.randomUUID();
    Artist grpcArtist = Artist.newBuilder()
        .setId(id.toString())
        .setName("Test Artist")
        .setBiography("Biography")
        .build();

    ListenableFuture<Artist> listenableFuture = Futures.immediateFuture(grpcArtist);

    when(artistStub.getArtist(any(Uuid.class)))
        .thenReturn(listenableFuture);

    CompletableFuture<ArtistJson> future = grpcArtistClient.getById(id);
    ArtistJson artistJson = future.join();

    assertEquals(id, artistJson.id());
    assertEquals("Test Artist", artistJson.name());
  }

  @Test
  void getArtistPage_returnsRestPage() {
    Pageable pageable = PageRequest.of(0, 10);
    timofeyqa.grpc.rococo.Pageable grpcPageable = toGrpcPageable(pageable,null);


    PageArtistResponse response = PageArtistResponse.newBuilder()
        // Добавьте необходимые данные в response, если нужно
        .build();

    ListenableFuture<PageArtistResponse> listenableFuture = Futures.immediateFuture(response);

    when(artistStub.getArtistPage(eq(grpcPageable)))
        .thenReturn(listenableFuture);

    CompletableFuture<RestPage<ArtistJson>> future = grpcArtistClient.getArtistPage(pageable, null);
    RestPage<ArtistJson> page = future.join();

    assertNotNull(page);
  }

  @Test
  void getArtistsByIds_withEmptyList_returnsEmptyList() {
    CompletableFuture<List<ArtistJson>> future = grpcArtistClient.getArtistsByIds(List.of());
    List<ArtistJson> result = future.join();
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void getArtistsByIds_withIds_returnsList() {
    UUID id = UUID.randomUUID();
    Artist grpcArtist = Artist.newBuilder()
        .setId(id.toString())
        .setName("Artist")
        .build();

    ArtistList response = ArtistList.newBuilder()
        .addArtists(grpcArtist)
        .build();

    ListenableFuture<ArtistList> listenableFuture = Futures.immediateFuture(response);

    when(artistStub.getArtistsByUuids(any()))
        .thenReturn(listenableFuture);

    CompletableFuture<List<ArtistJson>> future = grpcArtistClient.getArtistsByIds(List.of(id));
    List<ArtistJson> list = future.join();

    assertEquals(1, list.size());
    assertEquals(id, list.getFirst().id());
  }


  @Test
  void updateArtist_callsBlockingStub_andReturnsArtistJson() {
    UUID id = UUID.randomUUID();
    Artist grpcArtist = Artist.newBuilder()
        .setId(id.toString())
        .setName("Artist")
        .build();

    ArtistJson artistJson = new ArtistJson(id, "Artist", null, null);

    when(artistBlockingStub.updateArtist(any()))
        .thenReturn(grpcArtist);

    ArtistJson updated = grpcArtistClient.updateArtist(artistJson);

    assertEquals(id, updated.id());
    assertEquals("Artist", updated.name());
  }
}
