package timofeyqa.rococo.model.dto;

import lombok.Builder;
import lombok.ToString;
import timofeyqa.rococo.model.rest.ContentImpl;
import timofeyqa.rococo.model.rest.GeoJson;

import java.util.*;

@Builder(toBuilder = true)
public record MuseumDto(
    UUID id,
    String title,
    String description,
    byte[] photo,
    GeoJson geo,
    @ToString.Exclude
    Set<PaintingDto> paintings) implements ContentImpl {

}