package timofeyqa.rococo.mappers;

import com.google.protobuf.ByteString;
import org.mapstruct.*;
import timofeyqa.grpc.rococo.AddPaintingRequest;
import timofeyqa.grpc.rococo.Painting;
import timofeyqa.rococo.data.PaintingEntity;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PaintingMapper {

  @Mapping(target = "id", source = "id", qualifiedByName = "stringToUUID")
  @Mapping(target = "title",source = "title",qualifiedByName = "blankToNull")
  @Mapping(target = "description",source = "description",qualifiedByName = "blankToNull")
  @Mapping(target = "artistId", source = "artistId", qualifiedByName = "stringToUUID")
  @Mapping(target = "museumId", source = "museumId", qualifiedByName = "stringToUUID")
  @Mapping(target = "content", source = "content", qualifiedByName = "byteStringToBytes")
  PaintingEntity addEntityFromPainting(Painting painting);

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
    return (string == null || string.trim().isBlank()) ? null : string;
  }

  default Painting toPainting(AddPaintingRequest request){
    return Painting.newBuilder()
        .setTitle(request.getTitle())
        .setDescription(request.getDescription())
        .setArtistId(request.getArtistId())
        .setMuseumId(request.getMuseumId())
        .setContent(request.getContent())
        .build();
  }

  default PaintingEntity addEntityFromPainting(AddPaintingRequest request){
    return addEntityFromPainting(toPainting(request));
  }
}
