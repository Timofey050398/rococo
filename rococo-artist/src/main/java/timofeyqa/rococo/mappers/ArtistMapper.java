package timofeyqa.rococo.mappers;

import com.google.protobuf.ByteString;
import org.mapstruct.*;
import timofeyqa.grpc.rococo.AddArtistRequest;
import timofeyqa.grpc.rococo.Artist;
import timofeyqa.rococo.data.ArtistEntity;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ArtistMapper {

  @Mapping(target = "id", source = "id", qualifiedByName = "stringToUUID")
  @Mapping(target = "name",source = "name",qualifiedByName = "blankToNull")
  @Mapping(target = "biography",source = "biography",qualifiedByName = "blankToNull")
  @Mapping(target = "photo", source = "photo", qualifiedByName = "byteStringToBytes")
  ArtistEntity addEntityFromArtist(Artist artist);

  @Named("stringToUUID")
  static UUID stringToUUID(String id) {
    return (id == null || id.trim().isBlank()) ? null : UUID.fromString(id);
  }

  @Named("byteStringToBytes")
  static byte[] byteStringToBytes(ByteString content) {
    return (content == null || content.isEmpty()) ? null : content.toByteArray();
  }

  @Named("blankToNull")
  static String blankToNull(String string) {
    return (string == null || string.isBlank()) ? null : string;
  }

  default Artist toArtist(AddArtistRequest request) {
    return Artist.newBuilder()
        .setName(request.getName())
        .setBiography(request.getBiography())
        .setPhoto(request.getPhoto())
        .build();
  }
}
