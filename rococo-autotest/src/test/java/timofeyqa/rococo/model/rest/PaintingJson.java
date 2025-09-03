package timofeyqa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.qameta.allure.Param;
import io.qameta.allure.model.Parameter;
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
    @Param(mode = Parameter.Mode.MASKED)
    String content) implements ContentImpl {
}
