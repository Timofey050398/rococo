package timofeyqa.rococo.service.db;

import org.springframework.util.CollectionUtils;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.data.entity.PaintingEntity;
import timofeyqa.rococo.data.repository.ArtistRepository;
import timofeyqa.rococo.data.repository.PaintingRepository;
import timofeyqa.rococo.data.tpl.XaTransactionTemplate;
import timofeyqa.rococo.mapper.ArtistMapper;
import timofeyqa.rococo.model.dto.ArtistDto;
import timofeyqa.rococo.service.ArtistClient;

import java.util.*;

public class ArtistDbClient implements ArtistClient, DeletableClient<ArtistDto> {
  private final ArtistRepository artistRepository = new ArtistRepository();
  private final PaintingRepository paintingRepository = new PaintingRepository();
  private static final Config CFG = Config.getInstance();
  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.jdbcUrl());

  @Override
  public ArtistDto create(ArtistDto artistDto) {
    return xaTransactionTemplate.execute(() -> ArtistMapper.INSTANCE.fromEntity(
        artistRepository.create(ArtistMapper.INSTANCE.toEntity(artistDto))
    ));
  }

  @Override
  public Optional<ArtistDto> findByName(String name) {
    return xaTransactionTemplate.execute(() -> artistRepository.findByName(name)
        .map(ArtistMapper.INSTANCE::fromEntity)
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
  public void remove(ArtistDto artist) {
    xaTransactionTemplate.execute(()-> {
      artistRepository.remove(ArtistMapper.INSTANCE.toEntity(artist));
      return null;
    });
  }

  @Override
  public List<ArtistDto> findAllById(List<UUID> uuids){
    return Objects.requireNonNull(xaTransactionTemplate.execute(() -> artistRepository.findAllById(uuids)))
        .stream()
        .map(ArtistMapper.INSTANCE::fromEntity)
        .toList();
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
