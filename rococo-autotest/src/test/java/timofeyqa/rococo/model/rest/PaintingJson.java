package timofeyqa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record PaintingJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("title")
    String title,
    @JsonProperty("description")
    String description,
    @JsonProperty("artist")
    ArtistJson artist,
    @JsonProperty("museum")
    MuseumJson museum,
    @JsonProperty("content")
    String content) implements ContentImpl {
}
