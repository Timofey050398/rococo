package timofeyqa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;
import timofeyqa.rococo.data.entity.ArtistEntity;
import java.util.*;
import java.util.stream.Collectors;

import static timofeyqa.rococo.utils.PhotoConverter.convert;

@Builder(toBuilder=true)
public record ArtistJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("name")
    String name,
    @JsonProperty("biography")
    String biography,
    @JsonProperty("photo")
    String photo,
    @ToString.Exclude
    Set<PaintingJson> paintings)  implements ContentImpl {

  public static ArtistJson fromEntity(ArtistEntity artistEntity){
    return new ArtistJson(
        artistEntity.getId(),
        artistEntity.getName(),
        artistEntity.getBiography(),
        convert(artistEntity.getPhoto()),
        artistEntity.getPaintings()
            .stream()
            .map(PaintingJson::fromEntity)
            .collect(Collectors.toSet())
    );
  }

  public ArtistEntity toEntity(){
    ArtistEntity artistEntity = new ArtistEntity();
    artistEntity.setId(id);
    artistEntity.setName(name);
    artistEntity.setBiography(biography);
    artistEntity.setPhoto(convert(photo));
    return artistEntity;
  }
}
