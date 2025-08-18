package timofeyqa.rococo.service.api.grpc;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.model.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GrpcPaintingClientTest {

  @Mock
  private RococoPaintingServiceGrpc.RococoPaintingServiceFutureStub paintingStub;

  @Mock
  private GrpcMuseumClient grpcMuseumClient;

  @Mock
  private GrpcArtistClient grpcArtistClient;

  @InjectMocks
  private GrpcPaintingClient grpcPaintingClient;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getById_shouldReturnPaintingWithEnrichedArtistAndMuseum() {
    UUID paintingId = UUID.randomUUID();
    UUID artistId = UUID.randomUUID();
    UUID museumId = UUID.randomUUID();

    // Заглушка ответа gRPC по getPainting
    Painting grpcPainting = Painting.newBuilder()
        .setId(paintingId.toString())
        .setTitle("Painting title")
        .setDescription("Painting description")
        .setArtistId(artistId.toString())
        .setMuseumId(museumId.toString())
        .build();

    ListenableFuture<Painting> grpcFuture = Futures.immediateFuture(grpcPainting);

    when(paintingStub.getPainting(any()))
        .thenReturn(grpcFuture);

    // Заглушка grpcArtistClient.getById
    ArtistJson artistJson = new ArtistJson(artistId, "Artist Name", null, null);
    when(grpcArtistClient.getById(artistId))
        .thenReturn(CompletableFuture.completedFuture(artistJson));

    // Заглушка grpcMuseumClient.getById
    MuseumJson museumJson = new MuseumJson(museumId, "Museum Name", null, null,null);
    when(grpcMuseumClient.getById(museumId))
        .thenReturn(CompletableFuture.completedFuture(museumJson));

    // Выполняем тестируемый метод
    CompletableFuture<PaintingJson> future = grpcPaintingClient.getById(paintingId);

    PaintingJson result = future.join();

    // Проверяем базовые свойства
    assertNotNull(result);
    assertEquals(paintingId, result.id());
    assertEquals("Painting title", result.title());
    assertEquals("Painting description", result.description());

    // Проверяем вложенные объекты artist и museum
    assertNotNull(result.artist());
    assertEquals(artistId, result.artist().id());
    assertEquals("Artist Name", result.artist().name());

    assertNotNull(result.museum());
    assertEquals(museumId, result.museum().id());
    assertEquals("Museum Name", result.museum().title());
  }

  @Test
  void updatePainting_shouldCallBlockingStubAndReturnUpdatedPainting() {
    UUID paintingId = UUID.randomUUID();
    UUID artistId = UUID.randomUUID();
    UUID museumId = UUID.randomUUID();

    PaintingJson inputPainting = PaintingJson.builder()
        .id(paintingId)
        .title("Title")
        .description("Desc")
        .artist(new ArtistJson(artistId, "Artist", null, null))
        .museum(new MuseumJson(museumId, "Museum", null, null, null))
        .build();

    Painting grpcPainting = Painting.newBuilder()
        .setId(paintingId.toString())
        .setTitle("Title")
        .setDescription("Desc")
        .setArtistId(artistId.toString())
        .setMuseumId(museumId.toString())
        .build();

    SettableFuture<Painting> future = SettableFuture.create();
    future.set(grpcPainting);
    when(paintingStub.updatePainting(any())).thenReturn(future);

    doNothing().when(grpcArtistClient).validateChildObject(any());
    doNothing().when(grpcMuseumClient).validateChildObject(any());

    when(grpcArtistClient.getById(artistId)).thenReturn(CompletableFuture.completedFuture(
        new ArtistJson(artistId, "Artist", null, null)
    ));
    when(grpcMuseumClient.getById(museumId)).thenReturn(CompletableFuture.completedFuture(
        new MuseumJson(museumId, "Museum", null, null, null)
    ));

    PaintingJson updated;
    try {
      updated = grpcPaintingClient.updatePainting(inputPainting).get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }

    assertNotNull(updated);
    assertEquals(paintingId, updated.id());
    assertEquals("Title", updated.title());
    assertEquals(artistId, updated.artist().id());
    assertEquals(museumId, updated.museum().id());
  }


}