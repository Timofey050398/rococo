package timofeyqa.rococo.test.grpc.painting;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.jupiter.annotation.Painting;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.dto.PaintingDto;
import timofeyqa.rococo.service.PaintingClient;
import timofeyqa.rococo.service.db.PaintingDbClient;
import timofeyqa.rococo.test.grpc.BaseGrpcTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("gRPC: Получение картин по артисту")
class GrpcPaintingByArtistTest extends BaseGrpcTest {

  private final PaintingClient paintingClient = new PaintingDbClient();

  @Test
  @DisplayName("Успешное получение картин артиста")
  @Content(paintings = {
      @Painting(title = "Painting 1",artist = "artistTest"),
      @Painting(title = "Painting 2",artist = "artistTest"),
      @Painting(title = "Painting 3",artist = "artistTest")
  })
  void getPaintingsByArtistSuccessTest(ContentJson content) {
    var artist = content.allArtists().iterator().next();

    GetPaintingsByArtistRequest request = GetPaintingsByArtistRequest.newBuilder()
        .setUuid(Uuid.newBuilder().setUuid(artist.id().toString()))
        .setPageable(Pageable.newBuilder().setPage(0).setSize(10))
        .build();

    PagePainting response = paintingStub.getPaintingsByArtist(request);

    assertFalse(response.getPaintingsList().isEmpty());
    assertTrue(response.getTotalElements() >= response.getPaintingsCount());

    response.getPaintingsList().forEach(p ->
        assertEquals(artist.id().toString(), p.getArtistId())
    );


    List<PaintingDto> expected = paintingClient.findAllByArtistId(artist.id());
    assertEquals(expected.size(), response.getTotalElements());
  }

  @Test
  @DisplayName("INVALID_ARGUMENT при некорректном UUID артиста")
  void getPaintingsByArtistIllegalUuidTest() {
    GetPaintingsByArtistRequest request = GetPaintingsByArtistRequest.newBuilder()
        .setUuid(Uuid.newBuilder().setUuid("abc"))
        .setPageable(Pageable.newBuilder().setPage(0).setSize(10))
        .build();

    StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
        () -> paintingStub.getPaintingsByArtist(request));

    assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().name());
  }

  @Test
  @DisplayName("Пустой результат, если у артиста нет картин")
  @Content(artistCount = 1)
  void getPaintingsByArtistEmptyResultTest(ContentJson content) {
    var artist = content.artists().iterator().next();
    GetPaintingsByArtistRequest request = GetPaintingsByArtistRequest.newBuilder()
        .setUuid(Uuid.newBuilder().setUuid(artist.id().toString()))
        .setPageable(Pageable.newBuilder().setPage(0).setSize(10))
        .build();

    PagePainting response = paintingStub.getPaintingsByArtist(request);

    assertEquals(0, response.getPaintingsCount());
    assertEquals(0, response.getTotalElements());
    assertEquals(0, response.getTotalPages());
  }
}
