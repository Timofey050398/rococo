package timofeyqa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.qameta.allure.Param;
import io.qameta.allure.model.Parameter;
import lombok.Builder;
import lombok.ToString;

import java.util.*;

@Builder(toBuilder=true)
public record ArtistJson(
    @JsonProperty("id")
    UUID id,

    @JsonProperty("name")
    String name,

    @JsonProperty("biography")
    String biography,

    @Param(mode = Parameter.Mode.MASKED)
    @JsonProperty("photo")
    String photo,

    @ToString.Exclude
    @Param(mode = Parameter.Mode.HIDDEN)
    Set<PaintingJson> paintings)  implements ContentImpl {
}
