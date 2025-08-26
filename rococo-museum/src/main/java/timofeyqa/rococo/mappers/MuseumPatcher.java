package timofeyqa.rococo.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import timofeyqa.grpc.rococo.Museum;
import timofeyqa.rococo.data.MuseumEntity;

@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MuseumPatcher {

  void patch(MuseumEntity source, @MappingTarget MuseumEntity entity);

  default void patch(Museum source, MuseumEntity entity, MuseumMapper mapper) {
    MuseumEntity sourceEntity = mapper.addEntityFromMuseum(source);
    patch(sourceEntity,entity);
  }
}
