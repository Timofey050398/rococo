package timofeyqa.rococo.service;

import timofeyqa.rococo.model.rest.MuseumJson;

import java.util.Optional;

public interface MuseumClient {

  MuseumJson create(MuseumJson museum);

  Optional<MuseumJson> findByTitle(String title);
}
