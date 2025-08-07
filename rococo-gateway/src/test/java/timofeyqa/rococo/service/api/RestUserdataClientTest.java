package timofeyqa.rococo.service.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.*;
import org.springframework.web.server.ResponseStatusException;
import timofeyqa.rococo.model.UserJson;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestUserdataClientTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private RestUserdataClient client;

  private static final String BASE_URI = "http://localhost:8089/internal/api";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    client = new RestUserdataClient(restTemplate, "http://localhost:8089");
  }

  @Test
  void shouldReturnUserOnGet() {
    UserJson expectedUser = new UserJson(UUID.randomUUID(), "Nickname","Tim", "Cook",null);
    URI expectedUri = URI.create(BASE_URI + "/user?username=tim");

    when(restTemplate.exchange(eq(expectedUri), eq(HttpMethod.GET), isNull(), eq(UserJson.class)))
        .thenReturn(new ResponseEntity<>(expectedUser, HttpStatus.OK));

    UserJson result = client.getUser("tim");

    assertThat(result).isEqualTo(expectedUser);
  }

  @Test
  void shouldUpdateUserSuccessfully() {
    UserJson user = new UserJson(UUID.randomUUID(), null, "Timothy", "Cook",null);
    URI expectedUri = URI.create(BASE_URI + "/user?username=Nickname");

    when(restTemplate.exchange(
        eq(expectedUri),
        eq(HttpMethod.PATCH),
        any(HttpEntity.class),
        eq(UserJson.class)
    )).thenReturn(new ResponseEntity<>(user, HttpStatus.OK));

    UserJson result = client.updateUser(user, "Nickname");

    assertThat(result).isEqualTo(user);
  }

  @Test
  void shouldThrowWhenUsernameChanged() {
    UserJson patchedUser = new UserJson(UUID.randomUUID(), "Nickname","Tim", "Cook",null);

    assertThatThrownBy(() -> client.updateUser(patchedUser, "tim"))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("User can't change username");
  }

  @Test
  void shouldThrowWhenClientError() {
    URI uri = URI.create(BASE_URI + "/user?username=tim");

    when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), isNull(), eq(UserJson.class)))
        .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad request", "Bad data".getBytes(), null));

    assertThatThrownBy(() -> client.getUser("tim"))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Remote error");
  }

  @Test
  void shouldThrowWhenServerError() {
    URI uri = URI.create(BASE_URI + "/user?username=tim");

    when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), isNull(), eq(UserJson.class)))
        .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

    assertThatThrownBy(() -> client.getUser("tim"))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Remote server error");
  }

  @Test
  void shouldThrowWhenRestClientFails() {
    URI uri = URI.create(BASE_URI + "/user?username=tim");

    when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), isNull(), eq(UserJson.class)))
        .thenThrow(new RestClientException("Timeout"));

    assertThatThrownBy(() -> client.getUser("tim"))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Unable to reach remote server");
  }

  @Test
  void shouldThrowWhenResponseBodyIsNull() {
    URI uri = URI.create(BASE_URI + "/user?username=tim");

    when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), isNull(), eq(UserJson.class)))
        .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

    assertThatThrownBy(() -> client.getUser("tim"))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("No response from GET /api/user");
  }
}