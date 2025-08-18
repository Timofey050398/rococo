package timofeyqa.rococo.service.db;

import org.springframework.util.CollectionUtils;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.data.entity.PaintingEntity;
import timofeyqa.rococo.data.repository.MuseumRepository;
import timofeyqa.rococo.data.repository.PaintingRepository;
import timofeyqa.rococo.data.tpl.XaTransactionTemplate;
import timofeyqa.rococo.model.rest.MuseumJson;
import timofeyqa.rococo.service.MuseumClient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

import static timofeyqa.rococo.model.rest.MuseumJson.fromEntity;

@ParametersAreNonnullByDefault
public class MuseumDbClient implements MuseumClient {

  private final MuseumRepository museumRepository = new MuseumRepository();
  private final PaintingRepository paintingRepository = new PaintingRepository();

  private static final Config CFG = Config.getInstance();
  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.jdbcUrl());

  @Override
  public MuseumJson create(MuseumJson museum) {
    return xaTransactionTemplate.execute(() -> fromEntity(
        museumRepository.create(museum.toEntity())
    ));
  }

  @Override
  public Optional<MuseumJson> findByTitle(String title) {
    return xaTransactionTemplate.execute(() -> museumRepository.findByTitle(title)
        .map(MuseumJson::fromEntity)
    );
  }

  public void deleteList(List<UUID> list) {
    var paintingUuids = getPaintingUuids(list);
    xaTransactionTemplate.execute(() -> {
      if (!CollectionUtils.isEmpty(paintingUuids)) {
        paintingRepository.removeByUuidList(paintingUuids);
      }
      museumRepository.removeByUuidList(list);
      return null;
    });
  }

  private List<UUID> getPaintingUuids(List<UUID> list) {
    var museumList = xaTransactionTemplate.execute(() -> museumRepository.findAllById(list));
    return Optional.ofNullable(museumList)
        .stream()
        .flatMap(Collection::stream)
        .map(museum -> new HashSet<>(museum.getPaintings()))
        .flatMap(Set::stream)
        .map(PaintingEntity::getId)
        .toList();
  }
}
