package timofeyqa.rococo.service;

import timofeyqa.rococo.model.rest.MuseumJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MuseumClient {

  MuseumJson create(MuseumJson museum);

  Optional<MuseumJson> findByTitle(String title);

  void deleteList(List<UUID> uuidList);
}
