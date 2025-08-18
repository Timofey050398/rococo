package timofeyqa.rococo.service.db;

import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.data.entity.PaintingEntity;
import timofeyqa.rococo.data.repository.ArtistRepository;
import timofeyqa.rococo.data.repository.PaintingRepository;
import timofeyqa.rococo.data.tpl.XaTransactionTemplate;
import timofeyqa.rococo.model.rest.ArtistJson;
import timofeyqa.rococo.service.ArtistClient;

import java.util.*;

import static timofeyqa.rococo.model.rest.ArtistJson.fromEntity;

public class ArtistDbClient implements ArtistClient {
  private final ArtistRepository artistRepository = new ArtistRepository();
  private final PaintingRepository paintingRepository = new PaintingRepository();
  private static final Config CFG = Config.getInstance();
  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.jdbcUrl());

  @Override
  public ArtistJson create(ArtistJson artistJson) {
    return xaTransactionTemplate.execute(() -> fromEntity(
        artistRepository.create(artistJson.toEntity())
    ));
  }

  @Override
  public Optional<ArtistJson> findByName(String name) {
    return xaTransactionTemplate.execute(() -> artistRepository.findByName(name)
        .map(ArtistJson::fromEntity)
    );
  }

  public void deleteList(List<UUID> list) {
    var paintingUuids = getPaintingUuids(list);
    xaTransactionTemplate.execute(() -> {
      if (!CollectionUtils.isEmpty(paintingUuids)) {
        paintingRepository.removeByUuidList(paintingUuids);
      }
      artistRepository.removeByUuidList(list);
      return null;
    });
  }

  private List<UUID> getPaintingUuids(List<UUID> list) {
    var artistList = xaTransactionTemplate.execute(() -> artistRepository.findAllById(list));
    return Optional.ofNullable(artistList)
        .stream()
        .flatMap(Collection::stream)
        .map(artist -> new HashSet<>(artist.getPaintings()))
        .flatMap(Set::stream)
        .map(PaintingEntity::getId)
        .toList();
  }
}
