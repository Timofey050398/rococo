package timofeyqa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record CountryJson(
        @JsonProperty("id") UUID id,
        @JsonProperty("name") String name
        )  implements ResponseDto {
}
