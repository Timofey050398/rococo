package timofeyqa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Size;
import timofeyqa.grpc.rococo.UserResponse;
import timofeyqa.rococo.config.RococoGatewayServiceConfig;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("username")
    String username,
    @JsonProperty("firstname")
    @Size(max = 30, message = "First name can`t be longer than 30 characters")
    String firstname,
    @JsonProperty("lastname")
    @Size(max = 50, message = "Surname can`t be longer than 50 characters")
    String lastname,
    @JsonProperty("avatar")
    @Size(max = RococoGatewayServiceConfig.ONE_MB)
    String avatar) {

  public static @Nonnull UserJson fromGrpc(UserResponse grpcUser) {
    final String avatar = grpcUser.getAvatar().isEmpty()
            ? null
            : new String(grpcUser.getAvatar().toByteArray(), StandardCharsets.UTF_8);
    return new UserJson(
        UUID.fromString(grpcUser.getUuid()),
        grpcUser.getUsername(),
        grpcUser.getFirstname(),
        grpcUser.getLastname(),
        avatar
    );
  }
}
