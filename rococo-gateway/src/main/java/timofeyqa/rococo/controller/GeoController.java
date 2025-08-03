package timofeyqa.rococo.controller;

import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import timofeyqa.rococo.model.CountryJson;
import timofeyqa.rococo.service.api.RestGeoClient;

@RestController
public class GeoController {

    private final RestGeoClient geoClient;

    @Autowired
    public GeoController(RestGeoClient geoClient){
        this.geoClient = geoClient;
    }

    @GetMapping("/api/country")
    public Page<CountryJson> allCountries(@PageableDefault Pageable pageable){
        return geoClient.allCountries(pageable);
    }
}
