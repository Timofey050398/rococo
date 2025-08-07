package timofeyqa.rococo.mappers;

import com.google.protobuf.ByteString;
import org.mapstruct.*;
import timofeyqa.grpc.rococo.Museum;
import timofeyqa.rococo.data.MuseumEntity;

import java.util.UUID;

@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MuseumMapper {

  @Mapping(target = "title",source = "title",qualifiedByName = "blankToNull")
  @Mapping(target = "description",source = "description",qualifiedByName = "blankToNull")
  @Mapping(target = "city", source = "city", qualifiedByName = "blankToNull")
  @Mapping(target = "photo", source = "photo", qualifiedByName = "byteStringToBytes")
  @Mapping(target = "countryId", source = "countryId", qualifiedByName = "stringToUUID")
  void updateEntityFromMuseum(Museum museum, @MappingTarget MuseumEntity entity);

  @Named("stringToUUID")
  static UUID stringToUUID(String id) {
    return (id == null || id.isBlank()) ? null : UUID.fromString(id);
  }

  @Named("byteStringToBytes")
  static byte[] byteStringToBytes(ByteString content) {
    return (content == null || content.isEmpty()) ? null : content.toByteArray();
  }

  @Named("blankToNull")
  static String blankToNull(String string) {
    return (string == null || string.isBlank()) ? null : string;
  }
}
