package timofeyqa.kafka_log.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;

@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public record LogJson(
    @JsonProperty String service,
    @JsonProperty String level,
    @JsonProperty String message,
    @JsonProperty("thread_name") String thread,
    @JsonProperty("logger_name") String logger,
    @JsonProperty("@timestamp") Instant timestamp
) {
}
