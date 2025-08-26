package timofeyqa.rococo.data.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.data.entity.PaintingEntity;
import timofeyqa.rococo.data.jpa.EntityManagers;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@ParametersAreNonnullByDefault
public class PaintingRepository implements  HibernateRepository<PaintingEntity> {

  private static final Config CFG = Config.getInstance();

  private final EntityManager em = EntityManagers.em(CFG.jdbcUrl());

  @Override
  public EntityManager em(){
    return em;
  }
  @Override
  public Class<PaintingEntity> getEntityClass() {
    return PaintingEntity.class;
  }
  public Optional<PaintingEntity> findByTitle(String title) {
    return findByParam(title,"title");
  }

  public List<PaintingEntity> findByArtistId(UUID artistId) {
    return em.createQuery(
        "SELECT p FROM PaintingEntity p WHERE p.artist.id = :artistId",
        PaintingEntity.class
        )
        .setParameter("artistId", artistId)
        .getResultList();
  }
}
