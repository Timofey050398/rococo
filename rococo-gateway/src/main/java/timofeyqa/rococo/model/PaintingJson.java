package timofeyqa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import timofeyqa.rococo.validation.FileSize;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
@Jacksonized
public record PaintingJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("title")
    @Size(max = 256, message = "Can`t be longer than 256 characters")
    String title,
    @JsonProperty("description")
    @Size(max = 1000, message = "Can`t be longer than 1000 characters")
    String description,
    @JsonProperty("artist")
    ArtistJson artist,
    @JsonProperty("museum")
    MuseumJson museum,
    @FileSize
    @JsonProperty("content")
    String content) implements ResponseDto{

}
