package timofeyqa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import timofeyqa.rococo.data.UserEntity;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("username")
    String username,
    @JsonProperty("firstname")
    String firstname,
    @JsonProperty("lastname")
    String lastname,
    @JsonProperty("avatar")
    String avatar) {

  public static @Nonnull UserJson fromEntity(@Nonnull UserEntity entity) {
    return new UserJson(
        entity.getId(),
        entity.getUsername(),
        entity.getFirstname(),
        entity.getLastname(),
        entity.getAvatar() != null && entity.getAvatar().length > 0 ? new String(entity.getAvatar(), StandardCharsets.UTF_8) : null
    );
  }
}
