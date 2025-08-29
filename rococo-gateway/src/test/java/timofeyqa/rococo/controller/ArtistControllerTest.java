package timofeyqa.rococo.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import timofeyqa.rococo.ex.BadRequestException;
import timofeyqa.rococo.model.ArtistJson;
import timofeyqa.rococo.model.page.RestPage;
import timofeyqa.rococo.service.api.grpc.GrpcArtistClient;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistControllerTest {

  @Mock
  private GrpcArtistClient artistClient;

  @InjectMocks
  private ArtistController artistController;

  @Test
  void getArtistWithValidIdReturnsArtist() {
    UUID id = UUID.randomUUID();
    ArtistJson expectedArtist = new ArtistJson(id, "Name", "Bio", null);

    when(artistClient.getById(id))
        .thenReturn(CompletableFuture.completedFuture(expectedArtist));

    CompletableFuture<ArtistJson> futureResponse = artistController.getArtist(id.toString());

    ArtistJson response = futureResponse.join();
    
    assertThat(response).isEqualTo(expectedArtist);
  }


  @Test
  void getArtistWhenExceptionThrownPropagatesCompletionException() {
    UUID id = UUID.randomUUID();

    when(artistClient.getById(id))
        .thenReturn(CompletableFuture.failedFuture(new RuntimeException("fail")));

    CompletableFuture<ArtistJson> futureResponse = artistController.getArtist(id.toString());

    assertThatThrownBy(futureResponse::join)
        .isInstanceOf(CompletionException.class)
        .hasCauseInstanceOf(RuntimeException.class)
        .hasRootCauseMessage("fail");
  }

  @Test
  void getAllReturnsPageOfArtists() {
    Pageable pageable = PageRequest.of(0, 10);
    ArtistJson artist = new ArtistJson(UUID.randomUUID(), "Name", "Bio", null);
    RestPage<ArtistJson> page = new RestPage<>(List.of(artist), pageable, 1);

    when(artistClient.getArtistPage(pageable,null))
        .thenReturn(CompletableFuture.completedFuture(page));

    CompletableFuture<RestPage<ArtistJson>> futureResponse = artistController.getAll(pageable,null);

    RestPage<ArtistJson> response = futureResponse.join();

    assertThat(response).isEqualTo(page);
  }

  @Test
  void getAllWhenExceptionThrownReturnsStatus500() {
    Pageable pageable = PageRequest.of(0, 10);

    when(artistClient.getArtistPage(pageable, null))
        .thenReturn(CompletableFuture.failedFuture(new RuntimeException("fail")));

    CompletableFuture<RestPage<ArtistJson>> futureResponse = artistController.getAll(pageable, null);

    assertThatThrownBy(futureResponse::join)
        .isInstanceOf(CompletionException.class)
        .hasCauseInstanceOf(RuntimeException.class)
        .hasRootCauseMessage("fail");
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
