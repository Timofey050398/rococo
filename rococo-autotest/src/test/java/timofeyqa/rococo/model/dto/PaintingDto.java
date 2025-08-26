package timofeyqa.rococo.model.dto;

import lombok.Builder;
import timofeyqa.rococo.model.rest.ContentImpl;

import java.util.UUID;

@Builder(toBuilder = true)
public record PaintingDto(
    UUID id,
    String title,
    String description,
    ArtistDto artist,
    MuseumDto museum,
    byte[] content) implements ContentImpl {
}
