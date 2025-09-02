package timofeyqa.rococo.data.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.data.jdbc.DataSources;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityManagers {

  private static final Logger LOG = LoggerFactory.getLogger(EntityManagers.class);

  private static final Map<String, EntityManagerFactory> emfs = new ConcurrentHashMap<>();
  private static final Config CFG = Config.getInstance();

  @SuppressWarnings("resource")
  public static EntityManager em(String jdbcUrl) {
    return new ThreadSafeEntityManager(
        emfs.computeIfAbsent(
            jdbcUrl,
            key -> {
              LOG.info("JDBC URL: {}", jdbcUrl);
              DataSources.dataSource(jdbcUrl);
              final String persistenceUnitName = StringUtils.substringAfter(jdbcUrl, CFG.dbPort()+"/");
              LOG.info("Creating EntityManager for {}", persistenceUnitName);
              return Persistence.createEntityManagerFactory(persistenceUnitName);
            }
        ).createEntityManager()
    );
  }
  public static void closeAllEmfs(){
    emfs.values().forEach(EntityManagerFactory::close);
  }
}