package timofeyqa.rococo.data.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import timofeyqa.rococo.data.jdbc.DataSources;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
public class EntityManagers {
    private EntityManagers() {
    }

    private static final Map<String, EntityManagerFactory> emfs = new ConcurrentHashMap<>();

    @SuppressWarnings("resource")
    public static EntityManager em(String jdbcUrl) {
        return new ThreadSafeEntityManager(
                emfs.computeIfAbsent(
                        jdbcUrl,
                        key -> {
                            DataSources.dataSource(jdbcUrl);
                            return Persistence.createEntityManagerFactory(jdbcUrl);
                        }
                ).createEntityManager()
        );
    }
    public static void closeAllEmfs(){
        emfs.values().forEach(EntityManagerFactory::close);
    }
}