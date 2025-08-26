package timofeyqa.rococo.mappers;

import org.mapstruct.*;
import timofeyqa.grpc.rococo.Artist;
import timofeyqa.rococo.data.ArtistEntity;

@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ArtistPatcher {

  void patch(ArtistEntity source, @MappingTarget ArtistEntity entity);

  default void patch(Artist source, ArtistEntity entity, ArtistMapper artistMapper) {
    ArtistEntity sourceEntity = artistMapper.addEntityFromArtist(source);
    patch(sourceEntity,entity);
  }
}
