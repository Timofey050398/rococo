package timofeyqa.rococo.model.dto;

import lombok.Builder;
import lombok.ToString;
import timofeyqa.rococo.model.rest.ContentImpl;

import java.util.Set;
import java.util.UUID;

@Builder(toBuilder=true)
public record ArtistDto(
    UUID id,
    String name,
    String biography,
    byte[]  photo,
    @ToString.Exclude
    Set<PaintingDto> paintings)  implements ContentImpl {
}
