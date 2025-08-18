package timofeyqa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import timofeyqa.rococo.data.entity.UserEntity;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static timofeyqa.rococo.utils.PhotoConverter.convert;

@Builder(toBuilder = true)
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
    String avatar,
    @JsonIgnore
    String password) {

  public static @Nonnull UserJson fromEntity(@Nonnull UserEntity entity) {
    return new UserJson(
        entity.getId(),
        entity.getUsername(),
        entity.getFirstname(),
        entity.getLastname(),
        convert(entity.getAvatar()),
        null
    );
  }
  public @Nonnull UserJson withPassword(@Nonnull String password) {
    return this.toBuilder()
        .password(password)
        .build();
  }
}
