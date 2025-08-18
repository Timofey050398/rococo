package timofeyqa.rococo.data.repository;

import jakarta.persistence.EntityManager;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.data.entity.MuseumEntity;
import timofeyqa.rococo.data.jpa.EntityManagers;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class MuseumRepository implements  HibernateRepository<MuseumEntity> {

  private static final Config CFG = Config.getInstance();

  private final EntityManager em = EntityManagers.em(CFG.jdbcUrl());

  @Override
  public EntityManager em(){
    return em;
  }

  @Override
  public Class<MuseumEntity> getEntityClass() {
    return MuseumEntity.class;
  }
  public Optional<MuseumEntity> findByTitle(String title) {
    return findByParam(title,"title");
  }
}
