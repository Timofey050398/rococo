package timofeyqa.rococo.service.db;

import io.qameta.allure.Step;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.data.repository.PaintingRepository;
import timofeyqa.rococo.data.tpl.XaTransactionTemplate;
import timofeyqa.rococo.mapper.PaintingMapper;
import timofeyqa.rococo.model.dto.PaintingDto;
import timofeyqa.rococo.service.PaintingClient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@ParametersAreNonnullByDefault
public class PaintingDbClient implements PaintingClient, DeletableClient<PaintingDto> {

  private final PaintingRepository paintingRepository = new PaintingRepository();

  private static final Config CFG = Config.getInstance();
  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.jdbcUrl());

  @Override
  @Step("Create painting")
  public PaintingDto create(PaintingDto painting) {
    return xaTransactionTemplate.execute(() ->
        PaintingMapper.INSTANCE.fromEntity(paintingRepository.create(PaintingMapper.INSTANCE.toEntity(painting))
    ));
  }

  @Override
  @Step("Find painting by title")
  public Optional<PaintingDto> findByTitle(String title) {
    return xaTransactionTemplate.execute(() -> paintingRepository.findByTitle(title)
        .map(PaintingMapper.INSTANCE::fromEntity)
    );
  }


  @Override
  @Step("Find all painting's by uuid's")
  public List<PaintingDto> findAllById(List<UUID> uuids){
    return Objects.requireNonNull(xaTransactionTemplate.execute(() -> paintingRepository.findAllById(uuids)))
        .stream()
        .map(PaintingMapper.INSTANCE::fromEntity)
        .toList();
  }

  @Override
  @Step("Find all painting's by artist id")
  public List<PaintingDto> findAllByArtistId(UUID artistId) {
    return Objects.requireNonNull(xaTransactionTemplate.execute(() ->
            paintingRepository.findByArtistId(artistId)
        ))
        .stream()
        .map(PaintingMapper.INSTANCE::fromEntity)
        .toList();
  }

  @Override
  @Step("Delete painting's by uuid list")
  public void deleteList(List<UUID> uuidList){
    xaTransactionTemplate.execute(() -> {
      paintingRepository.removeByUuidList(uuidList);
      return null;
    });
  }

  @Override
  @Step("Delete painting")
  public void remove(PaintingDto painting) {
    xaTransactionTemplate.execute(()-> {
      paintingRepository.remove(PaintingMapper.INSTANCE.toEntity(painting));
      return null;
    });
  }
}
