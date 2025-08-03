package timofeyqa.rococo.controller;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import timofeyqa.rococo.model.CountryJson;
import timofeyqa.rococo.service.CountryService;

@RestController
@RequestMapping("/internal/api/country")
public class CountryController {

    private final CountryService countryService;

    @Autowired
    public CountryController(CountryService countryService){
        this.countryService = countryService;
    }

    @GetMapping
    public @Nonnull Page<CountryJson> allCountries(Pageable pageable){
        return countryService.allCountries(pageable);
    }
}
