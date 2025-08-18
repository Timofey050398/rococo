package timofeyqa.rococo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import timofeyqa.rococo.ex.BadRequestException;
import timofeyqa.rococo.model.PaintingJson;
import timofeyqa.rococo.model.page.RestPage;
import timofeyqa.rococo.service.api.grpc.GrpcPaintingClient;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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
  void getPainting_withEmptyId_throwsBadRequest() {
    assertThrows(BadRequestException.class, () -> paintingController.getPainting(""));
    verifyNoInteractions(paintingClient);
  }


  @Test
  void getPainting_withValidId_returnsOk() {
    UUID id = UUID.randomUUID();
    PaintingJson painting = new PaintingJson(id, "Title", "Desc", null, null, null);
    when(paintingClient.getById(id)).thenReturn(CompletableFuture.completedFuture(painting));

    ResponseEntity<PaintingJson> response = paintingController.getPainting(id.toString()).join();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(painting, response.getBody());
    verify(paintingClient, times(1)).getById(id);
  }

  @Test
  void getPainting_whenException_throwsException() {
    UUID id = UUID.randomUUID();
    when(paintingClient.getById(id))
        .thenReturn(CompletableFuture.failedFuture(new RuntimeException("fail")));

    CompletableFuture<ResponseEntity<PaintingJson>> response =
        paintingController.getPainting(id.toString());

    assertThrows(CompletionException.class, response::join);
    verify(paintingClient, times(1)).getById(id);
  }


  @Test
  void getPaintingByArtist_withEmptyArtistId_throwsBadRequest() {
    Pageable pageable = PageRequest.of(0, 10);

    // Пустая строка PathVariable
    assertThrows(BadRequestException.class, () ->
        paintingController.getPaintingByArtist(pageable, "")
    );

    verifyNoInteractions(paintingClient);
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

    CompletableFuture<ResponseEntity<RestPage<PaintingJson>>> response =
        paintingController.getPaintingByArtist(pageable, artistId.toString());

    ResponseEntity<RestPage<PaintingJson>> entity = response.join();
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(page, entity.getBody());

    verify(paintingClient, times(1)).getPaintingByArtist(pageable, artistId);
  }

  @Test
  void getPaintingByArtist_whenException_throwsException() {
    UUID artistId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(0, 10);

    when(paintingClient.getPaintingByArtist(pageable, artistId))
        .thenReturn(CompletableFuture.failedFuture(new RuntimeException("fail")));

    CompletableFuture<ResponseEntity<RestPage<PaintingJson>>> response =
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

    ResponseEntity<RestPage<PaintingJson>> response = paintingController.getAll(pageable, null).join();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(page, response.getBody());
    verify(paintingClient, times(1)).getPaintingPage(pageable, null);
  }

  @Test
  void getAll_whenException_throwsException() {
    Pageable pageable = PageRequest.of(0, 10);
    when(paintingClient.getPaintingPage(pageable, null))
        .thenReturn(CompletableFuture.failedFuture(new RuntimeException("fail")));

    CompletableFuture<ResponseEntity<RestPage<PaintingJson>>> response =
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
    CompletableFuture<ResponseEntity<PaintingJson>> response = paintingController.updatePainting(input);
    ResponseEntity<PaintingJson> entity = response.join();

    // проверяем результат
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(updated, entity.getBody());

    verify(paintingClient, times(1)).updatePainting(input);
  }

}
