package timofeyqa.rococo.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import timofeyqa.rococo.model.rest.SessionJson;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtDecoder {

  private static final ObjectMapper mapper = new ObjectMapper();

  public static SessionJson decode(@Nonnull String jwt) {
    try {
      String[] parts = jwt.split("\\.");
      if (parts.length < 2) {
        throw new IllegalArgumentException("Invalid JWT token: " + jwt);
      }

      String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
      JsonNode payload = mapper.readTree(payloadJson);

      String username = payload.path("sub").asText(null);

      Date issuedAt = payload.has("iat")
          ? new Date(payload.get("iat").asLong() * 1000)
          : null;

      Date expiresAt = payload.has("exp")
          ? new Date(payload.get("exp").asLong() * 1000)
          : null;

      return new SessionJson(username, issuedAt, expiresAt);
    } catch (Exception e) {
      throw new RuntimeException("Failed to decode JWT", e);
    }
  }
}