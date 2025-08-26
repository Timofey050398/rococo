package timofeyqa.rococo.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import timofeyqa.grpc.rococo.Artist;
import timofeyqa.rococo.data.entity.ArtistEntity;
import timofeyqa.rococo.model.dto.ArtistDto;
import timofeyqa.rococo.model.rest.ArtistJson;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ArtistMapper extends CommonMapperUtils, PaintingSetMapper {

  ArtistMapper INSTANCE = Mappers.getMapper(ArtistMapper.class);

  @Mapping(target = "paintings", ignore = true)
  ArtistEntity toEntity(ArtistDto artist);

  @Mapping(source = "photo", target = "photo", qualifiedByName = "byteToString")
  @Mapping(source = "paintings", target = "paintings", qualifiedByName = "toJsonSet")
  ArtistJson toJson(ArtistDto artist);

  @Mapping(target = "paintings", source = ".", qualifiedByName = "emptySet")
  ArtistDto fromEntity(ArtistEntity artist);

  @Mapping(source = "photo", target = "photo", qualifiedByName = "stringToBytes")
  @Mapping(source = "paintings", target = "paintings", qualifiedByName = "fromJsonSet")
  ArtistDto fromJson(ArtistJson artistJson);

  @Mapping(source = "id",target = "id",qualifiedByName = "fromStringToUuid")
  @Mapping(source = "name",target = "name",qualifiedByName = "stringFromGrpc")
  @Mapping(source = "biography",target = "biography",qualifiedByName = "stringFromGrpc")
  @Mapping(source = "photo", target = "photo",qualifiedByName = "fromByteString")
  @Mapping(target = "paintings", source = ".", qualifiedByName = "emptySet")
  ArtistDto fromGrpc(Artist artist);
}
