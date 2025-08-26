package timofeyqa.rococo.mappers;

import com.google.protobuf.ByteString;
import org.mapstruct.*;
import timofeyqa.grpc.rococo.AddMuseumRequest;
import timofeyqa.grpc.rococo.Museum;
import timofeyqa.rococo.data.MuseumEntity;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MuseumMapper {
  @Mapping(target = "id", source = "id", qualifiedByName = "stringToUUID")
  @Mapping(target = "title",source = "title",qualifiedByName = "blankToNull")
  @Mapping(target = "description",source = "description",qualifiedByName = "blankToNull")
  @Mapping(target = "city", source = "city", qualifiedByName = "blankToNull")
  @Mapping(target = "photo", source = "photo", qualifiedByName = "byteStringToBytes")
  @Mapping(target = "countryId", source = "countryId", qualifiedByName = "stringToUUID")
  MuseumEntity addEntityFromMuseum(Museum museum);

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

  default Museum toMuseum(AddMuseumRequest request){
    return Museum.newBuilder()
        .setTitle(request.getTitle())
        .setDescription(request.getDescription())
        .setCity(request.getCity())
        .setPhoto(request.getPhoto())
        .setCountryId(request.getCountryId())
        .build();
  }
}
