package timofeyqa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CountryJson(
        @JsonProperty UUID id,
        @JsonProperty String name
        ) {
}
