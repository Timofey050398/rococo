package timofeyqa.rococo.data.repository;

import jakarta.persistence.EntityManager;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.data.entity.Country;
import timofeyqa.rococo.data.entity.CountryEntity;
import timofeyqa.rococo.data.jpa.EntityManagers;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.List;

@ParametersAreNonnullByDefault
public class CountryRepository implements  HibernateRepository<CountryEntity> {

  private static final Config CFG = Config.getInstance();

  private final EntityManager em = EntityManagers.em(CFG.jdbcUrl());

  @Override
  public EntityManager em(){
    return em;
  }

  @Override
  public Class<CountryEntity> getEntityClass() {
    return CountryEntity.class;
  }

  public Optional<CountryEntity> findByName(Country country) {
    return findByParam(country.toString(),"name");
  }

  public List<CountryEntity> getAllCountries() {
    return em().createQuery(
        "SELECT c FROM CountryEntity c", CountryEntity.class
    ).getResultList();
  }
}
