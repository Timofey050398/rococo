package timofeyqa.rococo.data.repository;


import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import timofeyqa.rococo.data.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

  @Nonnull
  Optional<UserEntity> findByUsername(@Nonnull String username);
}
