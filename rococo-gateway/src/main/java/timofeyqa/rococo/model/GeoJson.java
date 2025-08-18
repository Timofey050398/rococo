package timofeyqa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Builder(toBuilder = true)
@Jacksonized
public record GeoJson(
        @JsonProperty("city")
        String city,
        @JsonProperty("country")
        CountryJson country
) {

    public GeoJson(String city, UUID countryId) {
        this(city, new CountryJson(countryId, null));
    }
}
