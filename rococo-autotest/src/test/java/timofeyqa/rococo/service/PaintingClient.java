package timofeyqa.rococo.service;

import timofeyqa.rococo.model.rest.PaintingJson;

import java.util.Optional;

public interface PaintingClient {

  PaintingJson create(PaintingJson painting);

  Optional<PaintingJson> findByTitle(String title);
}
