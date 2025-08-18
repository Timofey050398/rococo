package timofeyqa.rococo.model;

import timofeyqa.rococo.model.rest.ArtistJson;
import timofeyqa.rococo.model.rest.MuseumJson;
import timofeyqa.rococo.model.rest.PaintingJson;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record ContentJson(
    Set<PaintingJson> paintings,
    Set<ArtistJson> artists,
    Set<MuseumJson> museums
) {

  public boolean isEmpty() {
    return paintings.isEmpty() && artists.isEmpty() && museums.isEmpty();
  }

  public Set<ArtistJson> allArtists() {
    return mergeFromPaintings(artists, PaintingJson::artist);
  }

  public Set<MuseumJson> allMuseums() {
    return mergeFromPaintings(museums, PaintingJson::museum);
  }

  private <T> Set<T> mergeFromPaintings(Set<T> base, java.util.function.Function<PaintingJson, T> mapper) {
    Set<T> result = new HashSet<>(base);
    paintings.forEach(p -> result.add(mapper.apply(p)));
    return result
        .stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }
}
