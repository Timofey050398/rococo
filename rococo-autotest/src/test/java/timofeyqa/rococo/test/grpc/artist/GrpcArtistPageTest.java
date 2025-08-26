package timofeyqa.rococo.test.grpc.artist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.grpc.rococo.PageArtistResponse;
import timofeyqa.grpc.rococo.Pageable;
import timofeyqa.rococo.jupiter.annotation.Artist;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.test.grpc.BaseGrpcTest;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("gRPC: Пагинация артистов")
class GrpcArtistPageTest extends BaseGrpcTest {

  private static final String FILTER_FIELD = "Artist F";

  @Test
  @Content(artists = {
      @Artist(name = "Artist A", biography = "Bio D"),
      @Artist(name = "Artist B", biography = "Bio C"),
      @Artist(name = "Artist C", biography = "Bio B"),
      @Artist(name = "Artist D", biography = "Bio A")
  })
  @DisplayName("Получение первой страницы")
  void getFirstPageTest() {
    Pageable request = Pageable.newBuilder()
        .setPage(0)
        .setSize(2)
        .build();

    PageArtistResponse response = artistStub.getArtistPage(request);

    assertEquals(2, response.getArtistsCount());
    assertTrue(response.getTotalElements() >= 4);
    assertTrue(response.getTotalPages() >= 2);
  }

  @Test
  @DisplayName("Фильтрация артистов по имени (contains)")  @Content(artists = {
      @Artist(name = "Artist E", biography = "Bio H"),
      @Artist(name = FILTER_FIELD, biography = "Bio G"),
      @Artist(name = "Artist G", biography = "Bio F"),
      @Artist(name = "Artist H", biography = "Bio E")
  })
  void filterArtistsTest() {
    Pageable request = Pageable.newBuilder()
        .setPage(0)
        .setSize(10)
        .setFilterField(FILTER_FIELD)
        .build();

    PageArtistResponse response = artistStub.getArtistPage(request);

    assertTrue(response.getArtistsList().stream()
        .allMatch(a -> a.getName().contains(FILTER_FIELD)));
  }
}
