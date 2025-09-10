package timofeyqa.kafka_log.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import timofeyqa.kafka_log.data.Service;

import java.time.Instant;

@JsonSerialize
public record LogJson(
Service service,
String level,
String message,
String thread,
String logger,
Instant timestamp
) {
}
