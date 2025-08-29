package timofeyqa.rococo.test.rest.gateway.session;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.HttpException;
import timofeyqa.rococo.jupiter.annotation.ApiLogin;
import timofeyqa.rococo.jupiter.annotation.Token;
import timofeyqa.rococo.jupiter.annotation.User;
import timofeyqa.rococo.jupiter.annotation.meta.RestTest;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.jupiter.extension.ApiLoginExtension;
import timofeyqa.rococo.model.rest.SessionJson;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.service.api.SessionRestClient;
import timofeyqa.rococo.utils.JwtDecoder;

import static org.junit.jupiter.api.Assertions.*;
import static timofeyqa.rococo.jupiter.extension.ApiLoginExtension.rest;

@RestTest
@DisplayName("Session api test")
public class SessionTest {
  private final SessionRestClient client = new SessionRestClient();

  @RegisterExtension
  private static final ApiLoginExtension extension = rest();

  @Test
  @DisplayName("GET session не существующим токеном возвращает null во всех полях")
  void sessionNotFountTest(){
    SessionJson session = client.session("abc");

    assertAll(
        () -> assertNull(session.username()),
        () -> assertNull(session.expiresAt()),
        () -> assertNull(session.issuedAt())
    );
  }

  @Test
  @DisplayName("GET session успешный ответ")
  @User
  @ApiLogin
  void sessionSuccessTest(@Token String token){
    SessionJson session = client.session("Bearer "+token);
    assertEquals(JwtDecoder.decode(token),session);
  }
}
