package timofeyqa.rococo.service;

import java.util.List;
import java.util.UUID;

public interface DeletableClient<T> {

  void deleteList(List<UUID> uuidList);

  void remove(T object);
}
