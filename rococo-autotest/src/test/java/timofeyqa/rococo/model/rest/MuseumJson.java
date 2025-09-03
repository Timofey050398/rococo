package timofeyqa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.qameta.allure.Param;
import io.qameta.allure.model.Parameter;
import lombok.Builder;
import lombok.ToString;

import java.util.*;

@Builder(toBuilder = true)
public record MuseumJson(
    @JsonProperty("id")
    UUID id,

    @JsonProperty("title")
    String title,

    @JsonProperty("description")
    String description,

    @JsonProperty("photo")
    @Param(mode = Parameter.Mode.MASKED)
    String photo,

    @JsonProperty("geo")
    GeoJson geo,

    @ToString.Exclude
    @Param(mode = Parameter.Mode.HIDDEN)
    Set<PaintingJson> paintings) implements ContentImpl {
}
