package timofeyqa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import timofeyqa.rococo.data.entity.CountryEntity;

import java.util.UUID;

@Builder(toBuilder = true)
public record CountryJson(
    @JsonProperty("id") UUID id,
    @JsonProperty("name") String name
) {
   public static CountryJson fromEntity(CountryEntity countryEntity){
       return new CountryJson(
           countryEntity.getId(),
           countryEntity.getName()
       );
   }

  public CountryEntity toEntity() {
     CountryEntity countryEntity = new CountryEntity();
     countryEntity.setId(id);
     countryEntity.setName(name);
     return countryEntity;
  }
}
