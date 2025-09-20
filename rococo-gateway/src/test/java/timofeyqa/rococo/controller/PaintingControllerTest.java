package timofeyqa.rococo.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import timofeyqa.rococo.model.PaintingJson;
import timofeyqa.rococo.model.page.RestPage;
import timofeyqa.rococo.service.api.grpc.GrpcPaintingClient;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaintingControllerTest {

  @Mock
  private GrpcPaintingClient paintingClient;

  @InjectMocks
  private PaintingController paintingController;

  @Test
  void getPainting_withValidId_returnsOk() {
    UUID id = UUID.randomUUID();
    PaintingJson painting = new PaintingJson(id, "Title", "Desc", null, null, null);
    when(paintingClient.getById(id)).thenReturn(CompletableFuture.completedFuture(painting));

    PaintingJson response = paintingController.getPainting(id.toString()).join();

    assertEquals(painting, response);
    verify(paintingClient, times(1)).getById(id);
  }

  @Test
  void getPainting_whenException_throwsException() {
    UUID id = UUID.randomUUID();
    when(paintingClient.getById(id))
        .thenReturn(CompletableFuture.failedFuture(new RuntimeException("fail")));

    CompletableFuture<PaintingJson> response =
        paintingController.getPainting(id.toString());

    assertThrows(CompletionException.class, response::join);
    verify(paintingClient, times(1)).getById(id);
  }



  @Test
  void getPaintingByArtist_withInvalidUuid_throwsIllegalArgumentException() {
    Pageable pageable = PageRequest.of(0, 10);

    // Некорректный UUID
    assertThrows(IllegalArgumentException.class, () ->
        paintingController.getPaintingByArtist(pageable, "invalid-uuid")
    );

    verifyNoInteractions(paintingClient);
  }

  @Test
  void getPaintingByArtist_withValidArtistId_returnsPagedPaintings() {
    UUID artistId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(0, 10);
    RestPage<PaintingJson> page = new RestPage<>(List.of(
        new PaintingJson(UUID.randomUUID(), "Title1", null, null, null, null),
        new PaintingJson(UUID.randomUUID(), "Title2", null, null, null, null)
    ), pageable, 2);

    when(paintingClient.getPaintingByArtist(pageable, artistId))
        .thenReturn(CompletableFuture.completedFuture(page));

    CompletableFuture<RestPage<PaintingJson>> response =
        paintingController.getPaintingByArtist(pageable, artistId.toString());

    RestPage<PaintingJson> entity = response.join();
    assertEquals(page, entity);

    verify(paintingClient, times(1)).getPaintingByArtist(pageable, artistId);
  }

  @Test
  void getPaintingByArtist_whenException_throwsException() {
    UUID artistId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(0, 10);

    when(paintingClient.getPaintingByArtist(pageable, artistId))
        .thenReturn(CompletableFuture.failedFuture(new RuntimeException("fail")));

    CompletableFuture<RestPage<PaintingJson>> response =
        paintingController.getPaintingByArtist(pageable, artistId.toString());

    assertThrows(CompletionException.class, response::join); // Spring потом превратит это в 500
    verify(paintingClient, times(1)).getPaintingByArtist(pageable, artistId);
  }



  @Test
  void getAll_returnsPagedPaintings() {
    Pageable pageable = PageRequest.of(0, 10);
    RestPage<PaintingJson> page = new RestPage<>(List.of(
        new PaintingJson(UUID.randomUUID(), "Title1", null, null, null, null),
        new PaintingJson(UUID.randomUUID(), "Title2", null, null, null, null)
    ), pageable, 2);

    when(paintingClient.getPaintingPage(pageable, null)).thenReturn(CompletableFuture.completedFuture(page));

    RestPage<PaintingJson> response = paintingController.getAll(pageable, null).join();


    assertEquals(page, response);
    verify(paintingClient, times(1)).getPaintingPage(pageable, null);
  }

  @Test
  void getAll_whenException_throwsException() {
    Pageable pageable = PageRequest.of(0, 10);
    when(paintingClient.getPaintingPage(pageable, null))
        .thenReturn(CompletableFuture.failedFuture(new RuntimeException("fail")));

    CompletableFuture<RestPage<PaintingJson>> response =
        paintingController.getAll(pageable, null);

    assertThrows(CompletionException.class, response::join);
    verify(paintingClient, times(1)).getPaintingPage(pageable, null);
  }


  @Test
  void updatePainting_returnsUpdatedPainting() {
    PaintingJson input = new PaintingJson(UUID.randomUUID(), "Title", "Desc", null, null, null);
    PaintingJson updated = input.toBuilder().title("Updated Title").build();

    // мокируем CompletableFuture
    when(paintingClient.updatePainting(input)).thenReturn(CompletableFuture.completedFuture(updated));

    // вызываем контроллер
    CompletableFuture<PaintingJson> response = paintingController.updatePainting(input);
    PaintingJson entity = response.join();

    // проверяем результат

    assertEquals(updated, entity);

    verify(paintingClient, times(1)).updatePainting(input);
  }

}
