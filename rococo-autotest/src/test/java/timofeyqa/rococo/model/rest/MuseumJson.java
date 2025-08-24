package timofeyqa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;
import timofeyqa.rococo.data.entity.MuseumEntity;

import java.util.*;
import java.util.stream.Collectors;

import static timofeyqa.rococo.utils.PhotoConverter.convert;


@Builder(toBuilder = true)
public record MuseumJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("title")
    String title,
    @JsonProperty("description")
    String description,
    @JsonProperty("photo")
    String photo,
    @JsonProperty("geo")
    GeoJson geo,
    @ToString.Exclude
    Set<PaintingJson> paintings) implements ContentImpl {

  public MuseumJson(UUID id){
      this(id, null,null,null,null,new HashSet<>());
  }

  public static MuseumJson fromEntity(MuseumEntity museumEntity){
    Set<PaintingJson> paintings = Optional.ofNullable(museumEntity.getPaintings())
        .map(ps -> ps.stream()
            .map(PaintingJson::fromEntity)
            .collect(Collectors.toSet()))
        .orElse(Collections.emptySet());

    return fromEntity(museumEntity,paintings);
  }

  public static MuseumJson fromEntitySafe(MuseumEntity museumEntity) {
    return fromEntity(museumEntity,Collections.emptySet());
  }

  public static MuseumJson fromEntity(MuseumEntity museumEntity , Set<PaintingJson> paintings) {
    return new MuseumJson(
        museumEntity.getId(),
        museumEntity.getTitle(),
        museumEntity.getDescription(),
        convert(museumEntity.getPhoto()),
        new GeoJson(museumEntity.getCity(), CountryJson.fromEntity(museumEntity.getCountry())),
        paintings
    );
  }


  public MuseumEntity toEntity(){
    MuseumEntity museumEntity = new MuseumEntity();
    museumEntity.setId(id);
    museumEntity.setTitle(title);
    museumEntity.setDescription(description);
    museumEntity.setPhoto(convert(photo));
    if (geo != null){
      museumEntity.setCity(geo.city());
      museumEntity.setCountry(geo.country().toEntity());
    }
    return museumEntity;
  }
}
