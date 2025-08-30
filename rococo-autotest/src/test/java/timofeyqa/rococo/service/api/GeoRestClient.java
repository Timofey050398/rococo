package timofeyqa.rococo.service.api;

import io.qameta.allure.Step;
import timofeyqa.rococo.api.core.RestClient;
import timofeyqa.rococo.api.gateway.GeoGatewayApi;
import timofeyqa.rococo.data.entity.Country;
import timofeyqa.rococo.model.rest.CountryJson;
import timofeyqa.rococo.model.rest.pageable.RestResponsePage;
import timofeyqa.rococo.service.CountryClient;
import timofeyqa.rococo.api.core.ErrorAsserter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class GeoRestClient extends RestClient implements ErrorAsserter, CountryClient {

  private final GeoGatewayApi api = create(GeoGatewayApi.class);

  @Step("execute GET /api/country?page={page}&size={size}")
  public RestResponsePage<CountryJson> getCountryPage(int page, int size){
    return execute(api.getCountries(page, size));
  }

  @Override
  @Step("get country by name {country.name}")
  public Optional<CountryJson> getByName(Country country) {
    return getAllCountries()
        .stream()
        .filter(countryJson -> countryJson.name().equals(country.getName()))
        .findAny();
  }

  @Override
  @Step("get all countries")
  public List<CountryJson> getAllCountries() {
    List<CountryJson> countries = new ArrayList<>();
    final int size = 20;
    int pageNumber = 0;
    RestResponsePage<CountryJson> page;
    do {
      page = getCountryPage(pageNumber, size);
      countries.addAll(page.getContent());
      pageNumber++;
    } while (!page.isLast());
    return countries;
  }
}
