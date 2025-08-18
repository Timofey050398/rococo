package timofeyqa.rococo.data.repository;

import jakarta.persistence.EntityManager;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.data.entity.auth.AuthUserEntity;
import timofeyqa.rococo.data.jpa.EntityManagers;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class AuthUserRepository implements HibernateRepository<AuthUserEntity> {

  private static final Config CFG = Config.getInstance();

  private final EntityManager em = EntityManagers.em(CFG.authJdbcUrl());

  @Override
  public EntityManager em(){
    return em;
  }

  @Override
  public Class<AuthUserEntity> getEntityClass() {
    return AuthUserEntity.class;
  }

  public Optional<AuthUserEntity> findByUsername(String username) {
    return findByParam(username,"username");
  }
}
