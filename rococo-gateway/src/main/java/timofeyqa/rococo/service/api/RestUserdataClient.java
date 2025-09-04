package timofeyqa.rococo.service.api;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import timofeyqa.rococo.model.UserJson;

import java.net.URI;
import java.util.Optional;

@Component
public class RestUserdataClient {
    private final RestTemplate restTemplate;
    private final String userdataApiUri;

    @Autowired
    public RestUserdataClient(RestTemplate restTemplate, @Value("${rococo-userdata.base-uri}") String userdataApiUri) {
        this.restTemplate = restTemplate;
        this.userdataApiUri = userdataApiUri+"/internal/api";
    }

    @Nonnull
    public UserJson getUser(@Nonnull String username) {
        return process(HttpMethod.GET,null,username);
    }


    @Nonnull
    public UserJson updateUser(@Nonnull UserJson patchedUser, @Nonnull String username) {
        if (!StringUtils.isEmpty(patchedUser.username()) && !patchedUser.username().equals(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User can't change username");
        }
        return process(HttpMethod.PATCH, new HttpEntity<>(patchedUser),username);
    }

    private UserJson process(@Nonnull HttpMethod method, @Nullable HttpEntity<?> requestEntity, @Nonnull String username) {
        URI uri = UriComponentsBuilder.fromUriString(userdataApiUri + "/user")
            .queryParam("username", username)
            .build()
            .toUri();

        try {
            ResponseEntity<UserJson> response = restTemplate.exchange(
                uri,
                method,
                requestEntity,
                UserJson.class
            );

            return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "No response from "+method.name()+" /api/user"));

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode(), "Remote error: " + e.getResponseBodyAsString(), e);

        } catch (HttpServerErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Remote server error", e);

        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to reach remote server", e);
        }
    }

}
