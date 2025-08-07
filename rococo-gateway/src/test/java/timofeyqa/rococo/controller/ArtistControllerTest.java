package timofeyqa.rococo.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import timofeyqa.rococo.model.ArtistJson;
import timofeyqa.rococo.model.page.RestPage;
import timofeyqa.rococo.service.api.grpc.GrpcArtistClient;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ArtistControllerTest {

  @Mock
  private GrpcArtistClient artistClient;

  @InjectMocks
  private ArtistController artistController;

  @Test
  void getArtist_withValidId_returnsArtist() {
    UUID id = UUID.randomUUID();
    ArtistJson expectedArtist = new ArtistJson(id, "Name", "Bio", null);

    when(artistClient.getById(id))
        .thenReturn(CompletableFuture.completedFuture(expectedArtist));

    CompletableFuture<ResponseEntity<ArtistJson>> futureResponse = artistController.getArtist(id.toString());

    ResponseEntity<ArtistJson> response = futureResponse.join();

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).isEqualTo(expectedArtist);
  }

  @Test
  void getArtist_withEmptyId_returnsBadRequest() {
    CompletableFuture<ResponseEntity<ArtistJson>> futureResponse = artistController.getArtist("");

    ResponseEntity<ArtistJson> response = futureResponse.join();

    assertThat(response.getStatusCode().value()).isEqualTo(400);
  }

  @Test
  void getArtist_whenExceptionThrown_returnsStatus500() {
    UUID id = UUID.randomUUID();

    when(artistClient.getById(id))
        .thenReturn(CompletableFuture.failedFuture(new RuntimeException("fail")));

    CompletableFuture<ResponseEntity<ArtistJson>> futureResponse = artistController.getArtist(id.toString());

    ResponseEntity<ArtistJson> response = futureResponse.join();

    assertThat(response.getStatusCode().value()).isEqualTo(500);
  }

  @Test
  void getAll_returnsPageOfArtists() {
    Pageable pageable = PageRequest.of(0, 10);
    ArtistJson artist = new ArtistJson(UUID.randomUUID(), "Name", "Bio", null);
    RestPage<ArtistJson> page = new RestPage<>(List.of(artist), pageable, 1);

    when(artistClient.getArtistPage(pageable,null))
        .thenReturn(CompletableFuture.completedFuture(page));

    CompletableFuture<ResponseEntity<RestPage<ArtistJson>>> futureResponse = artistController.getAll(pageable,null);

    ResponseEntity<RestPage<ArtistJson>> response = futureResponse.join();

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).isEqualTo(page);
  }

  @Test
  void getAll_whenExceptionThrown_returnsStatus500() {
    Pageable pageable = PageRequest.of(0, 10);

    when(artistClient.getArtistPage(pageable, null))
        .thenReturn(CompletableFuture.failedFuture(new RuntimeException("fail")));

    CompletableFuture<ResponseEntity<RestPage<ArtistJson>>> futureResponse = artistController.getAll(pageable,null);

    ResponseEntity<RestPage<ArtistJson>> response = futureResponse.join();

    assertThat(response.getStatusCode().value()).isEqualTo(500);
  }

  @Test
  void updateArtist_returnsUpdatedArtist() {
    ArtistJson input = new ArtistJson(UUID.randomUUID(), "Name", "Bio", null);
    ArtistJson updated = new ArtistJson(input.id(), input.name(), input.biography(), input.photo());

    when(artistClient.updateArtist(input)).thenReturn(updated);

    ArtistJson result = artistController.updateArtist(input);

    assertThat(result).isEqualTo(updated);
    verify(artistClient).updateArtist(input);
  }
}
