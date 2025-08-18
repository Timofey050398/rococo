package timofeyqa.rococo.service.utils;

import lombok.experimental.UtilityClass;
import timofeyqa.rococo.model.ResponseDto;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@UtilityClass
public class UuidListExtractor {

  public static <T> List<UUID> extractUuids(
      List<T> list,
      Function<T, ? extends ResponseDto> extractor
  ) {
    return list.stream()
        .map(item -> Optional.ofNullable(extractor.apply(item))
            .map(ResponseDto::id)
            .orElse(null))
        .filter(Objects::nonNull)
        .distinct()
        .toList();
  }
}
