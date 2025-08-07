package timofeyqa.rococo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import timofeyqa.rococo.model.PaintingJson;
import timofeyqa.rococo.model.page.RestPage;
import timofeyqa.rococo.service.api.grpc.GrpcPaintingClient;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaintingControllerTest {

  private GrpcPaintingClient paintingClient;
  private PaintingController paintingController;

  @BeforeEach
  void setUp() {
    paintingClient = mock(GrpcPaintingClient.class);
    paintingController = new PaintingController(paintingClient);
  }

  @Test
  void getPainting_withNullId_returnsBadRequest() {
    CompletableFuture<ResponseEntity<PaintingJson>> response = paintingController.getPainting(null);
    ResponseEntity<PaintingJson> entity = response.join();

    assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    verifyNoInteractions(paintingClient);
  }

  @Test
  void getPainting_withEmptyId_returnsBadRequest() {
    CompletableFuture<ResponseEntity<PaintingJson>> response = paintingController.getPainting("");
    ResponseEntity<PaintingJson> entity = response.join();

    assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    verifyNoInteractions(paintingClient);
  }

  @Test
  void getPainting_withValidId_returnsPainting() {
    UUID id = UUID.randomUUID();
    PaintingJson painting = new PaintingJson(id, "Title", "Desc", null, null, null);
    when(paintingClient.getById(id)).thenReturn(CompletableFuture.completedFuture(painting));

    CompletableFuture<ResponseEntity<PaintingJson>> response = paintingController.getPainting(id.toString());
    ResponseEntity<PaintingJson> entity = response.join();

    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(painting, entity.getBody());

    verify(paintingClient, times(1)).getById(id);
  }

  @Test
  void getPainting_whenException_returnsInternalServerError() {
    UUID id = UUID.randomUUID();
    when(paintingClient.getById(id)).thenReturn(CompletableFuture.failedFuture(new RuntimeException("fail")));

    CompletableFuture<ResponseEntity<PaintingJson>> response = paintingController.getPainting(id.toString());
    ResponseEntity<PaintingJson> entity = response.join();

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
    verify(paintingClient, times(1)).getById(id);
  }

  @Test
  void getPaintingByArtist_withNullArtistId_returnsBadRequest() {
    CompletableFuture<ResponseEntity<RestPage<PaintingJson>>> response = paintingController.getPaintingByArtist(PageRequest.of(0, 10), null);
    ResponseEntity<RestPage<PaintingJson>> entity = response.join();

    assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    verifyNoInteractions(paintingClient);
  }

  @Test
  void getPaintingByArtist_withEmptyArtistId_returnsBadRequest() {
    CompletableFuture<ResponseEntity<RestPage<PaintingJson>>> response = paintingController.getPaintingByArtist(PageRequest.of(0, 10), "");
    ResponseEntity<RestPage<PaintingJson>> entity = response.join();

    assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
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

    when(paintingClient.getPaintingByArtist(pageable, artistId)).thenReturn(CompletableFuture.completedFuture(page));

    CompletableFuture<ResponseEntity<RestPage<PaintingJson>>> response = paintingController.getPaintingByArtist(pageable, artistId.toString());
    ResponseEntity<RestPage<PaintingJson>> entity = response.join();

    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(page, entity.getBody());

    verify(paintingClient, times(1)).getPaintingByArtist(pageable, artistId);
  }

  @Test
  void getPaintingByArtist_whenException_returnsInternalServerError() {
    UUID artistId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(0, 10);

    when(paintingClient.getPaintingByArtist(pageable, artistId)).thenReturn(CompletableFuture.failedFuture(new RuntimeException("fail")));

    CompletableFuture<ResponseEntity<RestPage<PaintingJson>>> response = paintingController.getPaintingByArtist(pageable, artistId.toString());
    ResponseEntity<RestPage<PaintingJson>> entity = response.join();

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());

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

    CompletableFuture<ResponseEntity<RestPage<PaintingJson>>> response = paintingController.getAll(pageable, null);
    ResponseEntity<RestPage<PaintingJson>> entity = response.join();

    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(page, entity.getBody());

    verify(paintingClient, times(1)).getPaintingPage(pageable, null);
  }

  @Test
  void getAll_whenException_returnsInternalServerError() {
    Pageable pageable = PageRequest.of(0, 10);

    when(paintingClient.getPaintingPage(pageable, null)).thenReturn(CompletableFuture.failedFuture(new RuntimeException("fail")));

    CompletableFuture<ResponseEntity<RestPage<PaintingJson>>> response = paintingController.getAll(pageable, null);
    ResponseEntity<RestPage<PaintingJson>> entity = response.join();

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());

    verify(paintingClient, times(1)).getPaintingPage(pageable, null);
  }

  @Test
  @Disabled
  void updatePainting_returnsUpdatedPainting() {
//    PaintingJson input = new PaintingJson(UUID.randomUUID(), "Title", "Desc", null, null, null);
//    PaintingJson updated = input.toBuilder().title("Updated Title").build();
//
//    when(paintingClient.updatePainting(input)).thenReturn(updated);
//
//    PaintingJson result = paintingController.updatePainting(input);
//
//    assertEquals(updated, result);
//    verify(paintingClient, times(1)).updatePainting(input);
  }
}
