package timofeyqa.rococo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.jwt.Jwt;
import timofeyqa.rococo.model.SessionJson;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SessionControllerTest {

  private final SessionController sessionController = new SessionController();

  @Test
  void session_whenPrincipalIsNull_returnsEmptySessionJson() {
    SessionJson result = sessionController.session(null);

    assertNotNull(result);
    assertNull(result.username());
    assertNull(result.issuedAt());
    assertNull(result.expiresAt());
  }

  @Test
  void session_whenIssuedAtOrExpiresAtIsNull_throwsException() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getIssuedAt()).thenReturn(null);
    when(jwt.getExpiresAt()).thenReturn(Instant.now());

    AuthenticationServiceException thrown = assertThrows(AuthenticationServiceException.class,
        () -> sessionController.session(jwt));
    assertEquals("JWT missing issuedAt or expiresAt", thrown.getMessage());

    Jwt jwt2 = mock(Jwt.class);
    when(jwt2.getIssuedAt()).thenReturn(Instant.now());
    when(jwt2.getExpiresAt()).thenReturn(null);

    AuthenticationServiceException thrown2 = assertThrows(AuthenticationServiceException.class,
        () -> sessionController.session(jwt2));
    assertEquals("JWT missing issuedAt or expiresAt", thrown2.getMessage());
  }

  @Test
  void session_whenValidJwt_returnsSessionJson() {
    Instant issuedAt = Instant.now().minusSeconds(60);
    Instant expiresAt = Instant.now().plusSeconds(3600);
    String subject = "user123";

    Jwt jwt = mock(Jwt.class);
    when(jwt.getIssuedAt()).thenReturn(issuedAt);
    when(jwt.getExpiresAt()).thenReturn(expiresAt);
    when(jwt.getSubject()).thenReturn(subject);

    SessionJson sessionJson = sessionController.session(jwt);

    assertNotNull(sessionJson);
    assertEquals(subject, sessionJson.username());
    assertEquals(Date.from(issuedAt), sessionJson.issuedAt());
    assertEquals(Date.from(expiresAt), sessionJson.expiresAt());
  }
}
