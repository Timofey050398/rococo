package timofeyqa.rococo.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LogJson(
    @JsonProperty String service,
    @JsonProperty String level,
    @JsonProperty String message,
    @JsonProperty("thread_name") String thread,
    @JsonProperty("logger_name") String logger,
    @JsonProperty("@timestamp") Instant timestamp
) {

  public static LogJson parse(String json) throws JsonProcessingException{
    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .readValue(json, LogJson.class);
  }
}
