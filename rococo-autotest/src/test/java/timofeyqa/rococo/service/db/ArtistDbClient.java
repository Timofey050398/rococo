package timofeyqa.rococo.service.db;

import org.springframework.util.CollectionUtils;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.data.entity.PaintingEntity;
import timofeyqa.rococo.data.repository.ArtistRepository;
import timofeyqa.rococo.data.repository.PaintingRepository;
import timofeyqa.rococo.data.tpl.XaTransactionTemplate;
import timofeyqa.rococo.model.rest.ArtistJson;
import timofeyqa.rococo.service.ArtistClient;
import timofeyqa.rococo.service.DeletableClient;

import java.util.*;

import static timofeyqa.rococo.model.rest.ArtistJson.fromEntity;

public class ArtistDbClient implements ArtistClient, DeletableClient<ArtistJson> {
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

  @Override
  public void deleteList(List<UUID> uuidList) {
    var paintingUuids = getPaintingUuids(uuidList);
    xaTransactionTemplate.execute(() -> {
      if (!CollectionUtils.isEmpty(paintingUuids)) {
        paintingRepository.removeByUuidList(paintingUuids);
      }
      artistRepository.removeByUuidList(uuidList);
      return null;
    });
  }

  @Override
  public void remove(ArtistJson artist) {
    xaTransactionTemplate.execute(()-> {
      artistRepository.remove(artist.toEntity());
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
