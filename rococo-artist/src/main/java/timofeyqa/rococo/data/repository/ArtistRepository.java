package timofeyqa.rococo.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import timofeyqa.rococo.data.ArtistEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtistRepository extends JpaRepository<ArtistEntity, UUID> {

  List<ArtistEntity> findAllByIdIn(List<UUID> ids);

  Optional<ArtistEntity> findByName(String name);

  Page<ArtistEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
