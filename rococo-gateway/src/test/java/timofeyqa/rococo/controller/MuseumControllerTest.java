package timofeyqa.rococo.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import timofeyqa.rococo.model.MuseumJson;
import timofeyqa.rococo.model.page.RestPage;
import timofeyqa.rococo.service.api.grpc.GrpcMuseumClient;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MuseumControllerTest {

  @Mock
  private GrpcMuseumClient museumClient;

  @InjectMocks
  private MuseumController museumController;

  @Test
  void getMuseum_withValidId_returnsMuseum() {
    UUID id = UUID.randomUUID();
    MuseumJson expectedMuseum = new MuseumJson(id, "Title", "Desc", "photoBase64", null);
    when(museumClient.getById(id)).thenReturn(CompletableFuture.completedFuture(expectedMuseum));

    CompletableFuture<MuseumJson> response = museumController.getMuseum(id.toString());

    MuseumJson entity = response.join();
    assertEquals(expectedMuseum, entity);

    verify(museumClient, times(1)).getById(id);
  }

  @Test
  void getMuseum_callsClient() {
    UUID id = UUID.randomUUID();
    MuseumJson museum = mock(MuseumJson.class);
    when(museumClient.getById(id)).thenReturn(CompletableFuture.completedFuture(museum));

    CompletableFuture<MuseumJson> response = museumController.getMuseum(id.toString());

    assertEquals(museum, response.join());
    verify(museumClient, times(1)).getById(id);
  }


  @Test
  void getAll_returnsPagedMuseumList() {
    Pageable pageable = PageRequest.of(0, 10);
    RestPage<MuseumJson> page = new RestPage<>(List.of(
        new MuseumJson(UUID.randomUUID(), "Title1", null, null, null),
        new MuseumJson(UUID.randomUUID(), "Title2", null, null, null)
    ), pageable, 2);

    when(museumClient.getMuseumPage(pageable, null)).thenReturn(CompletableFuture.completedFuture(page));

    CompletableFuture<RestPage<MuseumJson>> response = museumController.getAll(pageable, null);

    RestPage<MuseumJson> entity = response.join();
    assertEquals(page, entity);

    verify(museumClient, times(1)).getMuseumPage(pageable, null);
  }

  @Test
  void getAll_whenException_throwsCompletionException() {
    Pageable pageable = PageRequest.of(0, 10);
    when(museumClient.getMuseumPage(pageable, null))
        .thenReturn(CompletableFuture.failedFuture(new RuntimeException("fail")));

    CompletableFuture<RestPage<MuseumJson>> response = museumController.getAll(pageable, null);

    CompletionException ex = assertThrows(CompletionException.class, response::join);
    assertInstanceOf(RuntimeException.class, ex.getCause());
    assertEquals("fail", ex.getCause().getMessage());

    verify(museumClient, times(1)).getMuseumPage(pageable, null);
  }

  @Test
  void updateMuseum_callsClientAndReturnsResult() {
    MuseumJson input = new MuseumJson(UUID.randomUUID(), "Title", "Desc", null, null);
    MuseumJson updated = input.toBuilder().title("Updated").build();

    // Мокируем клиент на возврат MuseumJson, без ResponseEntity
    when(museumClient.updateMuseum(input))
        .thenReturn(CompletableFuture.completedFuture(updated));

    MuseumJson response = museumController.updateMuseum(input).join();

    assertEquals(updated, response);

    verify(museumClient, times(1)).updateMuseum(input);
  }

}
