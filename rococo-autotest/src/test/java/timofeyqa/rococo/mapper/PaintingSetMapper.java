package timofeyqa.rococo.mapper;

import org.mapstruct.Named;
import timofeyqa.rococo.model.dto.PaintingDto;
import timofeyqa.rococo.model.rest.PaintingJson;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public interface PaintingSetMapper {

  @Named("toJsonSet")
  static Set<PaintingJson> toJsonSet(Set<PaintingDto> dtoSet) {
    return dtoSet.stream()
        .map(PaintingMapper.INSTANCE::toJson)
        .collect(Collectors.toSet());
  }

  @Named("fromJsonSet")
  static Set<PaintingDto> fromJsonSet(Set<PaintingJson> jsonSet) {
    return jsonSet.stream()
        .map(PaintingMapper.INSTANCE::fromJson)
        .collect(Collectors.toSet());
  }

  @Named("emptySet")
  static Set<PaintingDto> emptySet(Object ignored) {
    return new HashSet<>();
  }
}
