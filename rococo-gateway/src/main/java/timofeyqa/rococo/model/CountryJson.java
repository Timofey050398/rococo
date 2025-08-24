package timofeyqa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Builder(toBuilder = true)
@Jacksonized
public record CountryJson(
        @JsonProperty("id")
        UUID id,

        @JsonProperty("name")
        @Size(max = 255, message = "Country can`t be longer than 255 characters")
        String name
) implements ResponseDto {
}
