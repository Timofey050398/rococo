package timofeyqa.rococo.mapper;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import timofeyqa.rococo.data.UserEntity;
import timofeyqa.rococo.model.UserJson;

@Component
public class UserMapper implements ByteToStringMapper {

  public @Nonnull UserJson fromEntity(@Nonnull UserEntity entity) {
    return new UserJson(
        entity.getId(),
        entity.getUsername(),
        entity.getFirstname(),
        entity.getLastname(),
        fromByte(entity.getAvatar())
    );
  }
}
