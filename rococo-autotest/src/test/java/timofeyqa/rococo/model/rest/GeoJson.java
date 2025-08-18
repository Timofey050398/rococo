package timofeyqa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record GeoJson(
        @JsonProperty("city")
        String city,
        @JsonProperty("country")
        CountryJson country
) {
  public GeoJson(String city, UUID countryId) {
    this(city, new CountryJson(countryId, null));
  }

  public String toListWebView(){
    return city + ", " + country.name();
  }

  public String toDetailWebView(){
    return country.name() + ", " + city;
  }
}
