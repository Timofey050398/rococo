package timofeyqa.rococo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record LogJson(
    @JsonProperty String service,
    @JsonProperty String level,
    @JsonProperty String message,
    @JsonProperty String thread,
    @JsonProperty String logger,
    @JsonProperty Instant timestamp
) {
}
