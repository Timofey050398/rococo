package timofeyqa.rococo.mappers;

import com.google.protobuf.ByteString;
import org.mapstruct.*;
import timofeyqa.grpc.rococo.Artist;
import timofeyqa.rococo.data.ArtistEntity;

@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ArtistMapper {

  @Mapping(target = "name",source = "name",qualifiedByName = "blankToNull")
  @Mapping(target = "biography",source = "biography",qualifiedByName = "blankToNull")
  @Mapping(target = "photo", source = "photo", qualifiedByName = "byteStringToBytes")
  void updateEntityFromArtist(Artist artist, @MappingTarget ArtistEntity entity);

  @Named("byteStringToBytes")
  static byte[] byteStringToBytes(ByteString content) {
    return (content == null || content.isEmpty()) ? null : content.toByteArray();
  }

  @Named("blankToNull")
  static String blankToNull(String string) {
    return (string == null || string.isBlank()) ? null : string;
  }
}
