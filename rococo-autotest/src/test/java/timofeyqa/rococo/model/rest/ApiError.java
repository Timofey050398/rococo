package timofeyqa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.List;

public record ApiError(
    @JsonProperty
    String apiVersion,

    @JsonProperty
    String code,

    @JsonProperty
    String message,

    @JsonProperty
    String domain,

    @JsonProperty
    List<String> errors
) implements Serializable {

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize ApiError", e);
        }
    }

    public String remoteError() {
        return "Remote error: "+this.toJson();
    }
}
