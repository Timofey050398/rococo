package timofeyqa.rococo.data.repository;

import jakarta.persistence.EntityManager;
import org.springframework.util.CollectionUtils;

import java.util.*;

public interface HibernateRepository<T> {

  EntityManager em();

  Class<T> getEntityClass();

  default Optional<T> findById(UUID id) {
    return Optional.ofNullable(em().find(getEntityClass(), id));
  }

  default T create(T entity) {
    em().joinTransaction();
    em().persist(entity);
    return entity;
  }

  default List<T> createBatch(List<T> entities) {
    em().joinTransaction();
    List<T> result = new ArrayList<>(entities.size());

    for (T entity : entities) {
      result.add(em().merge(entity));
    }

    return result;
  }


  default T update(T entity) {
    em().joinTransaction();
    return em().merge(entity);
  }

  default Optional<T> findByParam(Object param, String paramName) {
    String className = getEntityClass().getSimpleName();
    String query = String.format("SELECT e FROM %s e WHERE e.%s = :%s", className, paramName, paramName);
    return em().createQuery(query, getEntityClass())
        .setParameter(paramName, param)
        .getResultStream()
        .findFirst();
  }

  default void remove(T entity) {
    em().joinTransaction();
    T managed = em().contains(entity) ? entity : em().merge(entity);
    em().remove(managed);
  }

  default List<T> findAllById(List<UUID> uuids) {
    if (CollectionUtils.isEmpty(uuids)) {
      return Collections.emptyList();
    }
    String className = getEntityClass().getSimpleName();
    String query = String.format("SELECT e FROM %s e WHERE e.id IN :uuids", className);
    return em().createQuery(query, getEntityClass())
        .setParameter("uuids", uuids)
        .getResultList();
  }

  default void removeByUuidList(List<UUID> uuids) {
    if (CollectionUtils.isEmpty(uuids)) {
      return;
    }
    em().joinTransaction();
    String className = getEntityClass().getSimpleName();
    String query = String.format("DELETE FROM %s e WHERE e.id IN :uuids", className);
    em().createQuery(query)
        .setParameter("uuids", uuids)
        .executeUpdate();
  }

}

