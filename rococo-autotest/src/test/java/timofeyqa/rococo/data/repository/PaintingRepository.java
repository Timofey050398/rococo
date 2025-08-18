package timofeyqa.rococo.data.repository;

import jakarta.persistence.EntityManager;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.data.entity.PaintingEntity;
import timofeyqa.rococo.data.jpa.EntityManagers;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

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
}
