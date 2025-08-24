package timofeyqa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import timofeyqa.rococo.validation.FileSize;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder=true)
@Jacksonized
public record ArtistJson(
    @JsonProperty("id")
    UUID id,

    @JsonProperty("name")
    @Size(max = 255, message = "Name can`t be longer than 255 characters")
    String name,

    @JsonProperty("biography")
    @Size(max = 2000, message = "Biography name can`t be longer than 2000 characters")
    String biography,

    @JsonProperty("photo")
    @FileSize
    String photo)  implements ResponseDto {

  public ArtistJson(UUID id){
      this(id,null,null,null);
  }
}
