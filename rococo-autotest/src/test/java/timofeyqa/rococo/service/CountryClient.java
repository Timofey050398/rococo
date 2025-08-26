package timofeyqa.rococo.service;

import timofeyqa.rococo.data.entity.Country;
import timofeyqa.rococo.model.rest.CountryJson;

import java.util.List;
import java.util.Optional;

public interface CountryClient {
  Optional<CountryJson> getByName(Country country);
  List<CountryJson> getAllCountries();
}
