package timofeyqa.rococo.controller;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import timofeyqa.rococo.model.SessionJson;

import java.time.Instant;
import java.util.Date;


@RestController
public class SessionController {

  @GetMapping("/api/session/current")
  public SessionJson session(@AuthenticationPrincipal Jwt principal) {
    if (principal == null) return SessionJson.empty();

    final Instant issuedAt = principal.getIssuedAt();
    final Instant expiresAt = principal.getExpiresAt();

    if (issuedAt == null || expiresAt == null) {
      throw new AuthenticationServiceException("JWT missing issuedAt or expiresAt");
    }
    return new SessionJson(
            principal.getSubject(),
            Date.from(issuedAt),
            Date.from(expiresAt)
    );
  }

}
