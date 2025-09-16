package timofeyqa.kafka_log.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;

@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public record LogJson(
String service,
String level,
String message,
String thread,
String logger,
Instant timestamp
) {
}
