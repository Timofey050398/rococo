package timofeyqa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import timofeyqa.rococo.config.RococoGatewayServiceConfig;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
public record MuseumJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("title")
    @Size(max = 256, message = "Title name can`t be longer than 256 characters")
    String title,
    @JsonProperty("description")
    @Size(max = 1000, message = "Description name can`t be longer than 1000 characters")
    String description,
    @JsonProperty("photo")
    @Size(max = RococoGatewayServiceConfig.ONE_MB)
    String photo,
    @JsonProperty("geo")
    GeoJson geo) implements ResponseDto {

  public MuseumJson(UUID id){
      this(id, null,null,null,null);
  }
}
