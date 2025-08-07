package timofeyqa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import timofeyqa.rococo.config.RococoGatewayServiceConfig;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder=true)
public record ArtistJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("name")
    String name,
    @JsonProperty("biography")
    @Size(max = 2000, message = "Biography name can`t be longer than 2000 characters")
    String biography,
    @JsonProperty("photo")
    @Size(max = RococoGatewayServiceConfig.ONE_MB)
    String photo)  implements ResponseDto {

  public ArtistJson(UUID id){
      this(id,null,null,null);
  }
}
