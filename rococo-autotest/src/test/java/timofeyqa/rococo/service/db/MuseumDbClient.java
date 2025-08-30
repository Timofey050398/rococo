package timofeyqa.rococo.service.db;

import io.qameta.allure.Step;
import org.springframework.util.CollectionUtils;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.data.entity.PaintingEntity;
import timofeyqa.rococo.data.repository.MuseumRepository;
import timofeyqa.rococo.data.repository.PaintingRepository;
import timofeyqa.rococo.data.tpl.XaTransactionTemplate;
import timofeyqa.rococo.mapper.MuseumMapper;
import timofeyqa.rococo.model.dto.MuseumDto;
import timofeyqa.rococo.service.MuseumClient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;


@ParametersAreNonnullByDefault
public class MuseumDbClient implements MuseumClient, DeletableClient<MuseumDto> {

  private final MuseumRepository museumRepository = new MuseumRepository();
  private final PaintingRepository paintingRepository = new PaintingRepository();

  private static final Config CFG = Config.getInstance();
  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.jdbcUrl());

  @Override
  @Step("Create museum")
  public MuseumDto create(MuseumDto museum) {
    return xaTransactionTemplate.execute(() -> MuseumMapper.INSTANCE.fromEntity(
        museumRepository.create(MuseumMapper.INSTANCE.toEntity(museum))
    ));
  }

  @Override
  @Step("Find museum by title")
  public Optional<MuseumDto> findByTitle(String title) {
    return xaTransactionTemplate.execute(() -> museumRepository.findByTitle(title)
        .map(MuseumMapper.INSTANCE::fromEntity)
    );
  }

  @Override
  @Step("Delete museum's by uuid list")
  public void deleteList(List<UUID> uuidList) {
    var paintingUuids = getPaintingUuids(uuidList);
    xaTransactionTemplate.execute(() -> {
      if (!CollectionUtils.isEmpty(paintingUuids)) {
        paintingRepository.removeByUuidList(paintingUuids);
      }
      museumRepository.removeByUuidList(uuidList);
      return null;
    });
  }

  @Override
  @Step("Find museum's by uuid list")
  public List<MuseumDto> findAllById(List<UUID> uuids){
    return Objects.requireNonNull(xaTransactionTemplate.execute(() -> museumRepository.findAllById(uuids)))
        .stream()
        .map(MuseumMapper.INSTANCE::fromEntity)
        .toList();
  }


  @Step("Get painting uuid's by museum uuid's")
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

  @Override
  @Step("Delete museum")
  public void remove(MuseumDto museum) {
    xaTransactionTemplate.execute(()-> {
      museumRepository.remove(MuseumMapper.INSTANCE.toEntity(museum));
      return null;
    });
  }
}
