package timofeyqa.rococo.service.db;

import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.data.repository.PaintingRepository;
import timofeyqa.rococo.data.tpl.XaTransactionTemplate;
import timofeyqa.rococo.model.rest.PaintingJson;
import timofeyqa.rococo.service.DeletableClient;
import timofeyqa.rococo.service.PaintingClient;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static timofeyqa.rococo.model.rest.PaintingJson.fromEntity;

public class PaintingDbClient implements PaintingClient, DeletableClient<PaintingJson> {

  private final PaintingRepository paintingRepository = new PaintingRepository();

  private static final Config CFG = Config.getInstance();
  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.jdbcUrl());

  @Override
  public PaintingJson create(PaintingJson painting) {
    return xaTransactionTemplate.execute(() -> fromEntity(paintingRepository.create(painting.toEntity())
    ));
  }

  @Override
  public Optional<PaintingJson> findByTitle(String title) {
    return xaTransactionTemplate.execute(() -> paintingRepository.findByTitle(title)
        .map(PaintingJson::fromEntity)
    );
  }

  @Override
  public void deleteList(List<UUID> uuidList){
    xaTransactionTemplate.execute(() -> {
      paintingRepository.removeByUuidList(uuidList);
      return null;
    });
  }

  @Override
  public void remove(PaintingJson painting) {
    xaTransactionTemplate.execute(()-> {
      paintingRepository.remove(painting.toEntity());
      return null;
    });
  }
}
