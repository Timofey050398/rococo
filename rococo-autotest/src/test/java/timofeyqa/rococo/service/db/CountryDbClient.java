package timofeyqa.rococo.service.db;

import io.qameta.allure.Step;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.data.entity.Country;
import timofeyqa.rococo.data.repository.CountryRepository;
import timofeyqa.rococo.data.tpl.XaTransactionTemplate;
import timofeyqa.rococo.model.rest.CountryJson;
import timofeyqa.rococo.service.CountryClient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class CountryDbClient implements CountryClient {

  private final CountryRepository countryRepository = new CountryRepository();

  private static final Config CFG = Config.getInstance();
  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.jdbcUrl());

  @Override
  @Step("Get country {country} by name")
  public Optional<CountryJson> getByName(Country country) {
    return xaTransactionTemplate.execute(() -> countryRepository.findByName(country)
        .map(CountryJson::fromEntity)
    );
  }

  @Override
  public List<CountryJson> getAllCountries() {
    return xaTransactionTemplate.execute(() -> countryRepository.getAllCountries()
        .stream()
        .map(CountryJson::fromEntity)
        .toList()
    );
  }
}
