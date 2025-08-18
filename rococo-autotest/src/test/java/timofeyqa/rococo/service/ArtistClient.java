package timofeyqa.rococo.service;

import timofeyqa.rococo.model.rest.ArtistJson;

import java.util.Optional;

public interface ArtistClient {

  ArtistJson create(ArtistJson artistJson);

  Optional<ArtistJson> findByName(String name);
}
