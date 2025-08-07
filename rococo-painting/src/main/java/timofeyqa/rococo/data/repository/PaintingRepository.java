package timofeyqa.rococo.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import timofeyqa.rococo.data.PaintingEntity;

import java.util.List;
import java.util.UUID;

public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID> {

  Page<PaintingEntity> findByArtistId(UUID artistId, PageRequest pageRequest);

  Page<PaintingEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);

}
