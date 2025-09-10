package timofeyqa.kafka_log.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import timofeyqa.kafka_log.data.LogEntity;
import timofeyqa.kafka_log.model.LogJson;

@Mapper(componentModel = "spring")
public interface LogMapper {

  @Mapping(target = "id", ignore = true)
  LogEntity toEntity(LogJson logJson);
}
