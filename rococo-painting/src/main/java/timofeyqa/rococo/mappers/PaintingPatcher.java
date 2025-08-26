package timofeyqa.rococo.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import timofeyqa.grpc.rococo.Painting;
import timofeyqa.rococo.data.PaintingEntity;

@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaintingPatcher {
  void patch(PaintingEntity source, @MappingTarget PaintingEntity target);

  default void patch(Painting source, PaintingEntity target, PaintingMapper mapper) {
    PaintingEntity srcEntity = mapper.addEntityFromPainting(source);
    patch(srcEntity, target);
  }
}
