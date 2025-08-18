package timofeyqa.rococo.service;

import timofeyqa.rococo.model.rest.PaintingJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaintingClient {

  PaintingJson create(PaintingJson painting);

  Optional<PaintingJson> findByTitle(String title);

  void deleteList(List<UUID> uuidList);
}
