package timofeyqa.rococo.model;

import timofeyqa.rococo.model.dto.ArtistDto;
import timofeyqa.rococo.model.dto.MuseumDto;
import timofeyqa.rococo.model.dto.PaintingDto;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ContentJson(
    Set<PaintingDto> paintings,
    Set<ArtistDto> artists,
    Set<MuseumDto> museums
) {

  public boolean isEmpty() {
    return paintings.isEmpty() && artists.isEmpty() && museums.isEmpty();
  }

  public Set<ArtistDto> allArtists() {
    return mergeFromPaintings(artists, PaintingDto::artist);
  }

  public Set<MuseumDto> allMuseums() {
    return mergeFromPaintings(museums, PaintingDto::museum);
  }

  private <T> Set<T> mergeFromPaintings(Set<T> base, Function<PaintingDto, T> mapper) {
    Set<T> result = new HashSet<>(base);
    paintings.forEach(p -> result.add(mapper.apply(p)));
    return result
        .stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }
}
