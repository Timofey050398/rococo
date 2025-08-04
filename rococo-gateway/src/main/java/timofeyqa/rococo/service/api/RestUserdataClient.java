package timofeyqa.rococo.service.api;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import timofeyqa.rococo.ex.NoRestResponseException;
import timofeyqa.rococo.model.UserJson;

import java.net.URI;
import java.util.Optional;

@Component
@ConditionalOnProperty(prefix = "niffler-userdata", name = "client", havingValue = "rest")
public class RestUserdataClient {
    private final RestTemplate restTemplate;
    private final String userdataApiUri;

    @Autowired
    public RestUserdataClient(RestTemplate restTemplate, @Value("${rococo-userdata.base-uri}") String userdataApiUri) {
        this.restTemplate = restTemplate;
        this.userdataApiUri = userdataApiUri+"/internal";
    }

    @Nonnull
    public UserJson getUser(@Nonnull String username) {
        URI uri = UriComponentsBuilder.fromUriString(userdataApiUri + "/user")
                .queryParam("username", username)
                .build()
                .toUri();

        ResponseEntity<UserJson> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                UserJson.class
        );

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No response from [/user] for GET request"));
    }


    @Nonnull
    public UserJson updateUser(@Nonnull UserJson patchedUser){
        ResponseEntity<UserJson> response = restTemplate.exchange(
                userdataApiUri + "/user",
                HttpMethod.PATCH,
                new HttpEntity<>(patchedUser),
                UserJson.class
        );
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No response from [/user] for PATCH request"));
    }

}
