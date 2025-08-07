package timofeyqa.rococo.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import timofeyqa.rococo.data.MuseumEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MuseumRepository extends JpaRepository<MuseumEntity, UUID> {

  List<MuseumEntity> findAllByIdIn(List<UUID> ids);

  Optional<MuseumEntity> findByTitle(String title);

  Page<MuseumEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);

}
