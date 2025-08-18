package timofeyqa.rococo.data.repository;

import jakarta.persistence.EntityManager;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.data.entity.ArtistEntity;
import timofeyqa.rococo.data.jpa.EntityManagers;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class ArtistRepository implements HibernateRepository<ArtistEntity> {

  private static final Config CFG = Config.getInstance();

  private final EntityManager em = EntityManagers.em(CFG.jdbcUrl());

  @Override
  public EntityManager em(){
    return em;
  }

  @Override
  public Class<ArtistEntity> getEntityClass() {
    return ArtistEntity.class;
  }

  public Optional<ArtistEntity> findByName(String name) {
    return findByParam(name,"name");
  }

}
