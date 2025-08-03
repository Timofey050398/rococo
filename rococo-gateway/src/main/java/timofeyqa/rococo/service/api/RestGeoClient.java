package timofeyqa.rococo.service.api;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import timofeyqa.rococo.ex.NoRestResponseException;
import timofeyqa.rococo.model.CountryJson;
import timofeyqa.rococo.model.page.RestPage;
import org.springframework.data.domain.Pageable;
import timofeyqa.rococo.service.utils.HttpQueryPaginationAndSort;

import java.util.Optional;

@Component
@ConditionalOnProperty(prefix = "niffler-userdata", name = "client", havingValue = "rest")
public class RestGeoClient {
    private final RestTemplate restTemplate;
    private final String geoClientApiUri;

    @Autowired
    public RestGeoClient(RestTemplate restTemplate, @Value("${rococo-geo.base-uri}") String geoClientApiUri) {
        this.restTemplate = restTemplate;
        this.geoClientApiUri = geoClientApiUri+"/internal";
    }

    @Nonnull
    public Page<CountryJson> allCountries(@Nonnull Pageable pageable){
        ResponseEntity<RestPage<CountryJson>> response = restTemplate.exchange(
                geoClientApiUri +"/country"
                + new HttpQueryPaginationAndSort(pageable),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>(){}
                );
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST Page<UserJson> response is given [/v2/friends/all/ Route]"));

    }
}
