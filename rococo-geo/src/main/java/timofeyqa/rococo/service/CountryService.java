package timofeyqa.rococo.service;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import timofeyqa.rococo.data.repository.CountryRepository;
import timofeyqa.rococo.model.CountryJson;

@Component
public class CountryService {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryService(CountryRepository countryRepository){
        this.countryRepository = countryRepository;
    }

    @Transactional(readOnly = true)
    public @Nonnull Page<CountryJson> allCountries(Pageable pageable) {
        return countryRepository.findAll(pageable)
                .map(entity -> new CountryJson(
                        entity.getId(),
                        entity.getName()
                ));
    }

}
