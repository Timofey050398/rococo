package timofeyqa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder(toBuilder = true)
public record GeoJson(
        @JsonProperty("city")
        String city,
        @JsonProperty("country")
        CountryJson country
) {
}
