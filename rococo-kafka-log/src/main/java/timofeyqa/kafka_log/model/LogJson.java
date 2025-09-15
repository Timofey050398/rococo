package timofeyqa.kafka_log.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;

@JsonSerialize
public record LogJson(
String service,
String level,
String message,
String thread,
String logger,
Instant timestamp
) {
}
