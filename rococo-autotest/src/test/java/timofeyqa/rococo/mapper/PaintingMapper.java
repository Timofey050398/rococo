package timofeyqa.rococo.mapper;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import timofeyqa.grpc.rococo.Museum;
import timofeyqa.grpc.rococo.Painting;
import timofeyqa.rococo.data.entity.ArtistEntity;
import timofeyqa.rococo.data.entity.MuseumEntity;
import timofeyqa.rococo.data.entity.PaintingEntity;
import timofeyqa.rococo.model.dto.ArtistDto;
import timofeyqa.rococo.model.dto.MuseumDto;
import timofeyqa.rococo.model.dto.PaintingDto;
import timofeyqa.rococo.model.rest.*;

import java.util.HashSet;
import java.util.UUID;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaintingMapper extends CommonMapperUtils {

  PaintingMapper INSTANCE = Mappers.getMapper(PaintingMapper.class);

  @Mapping(source = "museum", target = "museum", qualifiedByName = "museumToEntity")
  @Mapping(source = "artist", target = "artist", qualifiedByName = "artistToEntity")
  PaintingEntity toEntity(PaintingDto painting);

  @Mapping(source = "museum", target = "museum", qualifiedByName = "museumFromEntity")
  @Mapping(source = "artist", target = "artist", qualifiedByName = "artistFromEntity")
  PaintingDto fromEntity(PaintingEntity painting);

  @Mapping(source = "museum", target = "museum", qualifiedByName = "museumToJson")
  @Mapping(source = "artist", target = "artist", qualifiedByName = "artistToJson")
  @Mapping(source = "content", target = "content", qualifiedByName = "byteToString")
  PaintingJson toJson(PaintingDto painting);

  @Mapping(source = "museum", target = "museum", qualifiedByName = "museumFromJson")
  @Mapping(source = "artist", target = "artist", qualifiedByName = "artistFromJson")
  @Mapping(source = "content", target = "content", qualifiedByName = "stringToBytes")
  PaintingDto fromJson(PaintingJson paintingJson);

  @Mapping(source = "id",target = "id",qualifiedByName = "fromStringToUuid")
  @Mapping(source = "title",target = "title",qualifiedByName = "stringFromGrpc")
  @Mapping(source = "description",target = "description",qualifiedByName = "stringFromGrpc")
  @Mapping(source = "content", target = "content",qualifiedByName = "fromByteString")
  @Mapping(source = "artistId",target = "artist",qualifiedByName = "artistFromString")
  @Mapping(source = "museumId",target = "museum",qualifiedByName = "museumFromString")
  PaintingDto fromGrpc(Painting painting);

  @Named("museumFromString")
  static MuseumDto museumFromString(String museum) {
    if (StringUtils.isEmpty(museum)) return null;
    return new MuseumDto(
        UUID.fromString(museum),
        null,
        null,
        null,
        new GeoJson(null, new CountryJson(null,null)),
        new HashSet<>()
    );
  }

  @Named("artistFromString")
  static ArtistDto artistFromString(String artist) {
    if (StringUtils.isEmpty(artist)) return null;
    return new ArtistDto(
        UUID.fromString(artist),
        null,
        null,
        null,
        new HashSet<>()
    );
  }

  @Named("museumToEntity")
  static MuseumEntity museumToEntity(MuseumDto museumDto) {
    return museumDto == null
      ? null
      : MuseumMapper.INSTANCE.toEntity(museumDto);
  }

  @Named("museumToJson")
  static MuseumJson museumToJson(MuseumDto museumDto) {
    return museumDto == null
        ?null
        :MuseumMapper.INSTANCE.toJson(museumDto);
  }

  @Named("museumFromEntity")
  static MuseumDto museumFromEntity(MuseumEntity museumEntity) {
    return MuseumMapper.INSTANCE.fromEntity(museumEntity);
  }

  @Named("museumFromJson")
  static MuseumDto museumFromJson(MuseumJson museumJson) {
    return MuseumMapper.INSTANCE.fromJson(museumJson);
  }

  @Named("artistToEntity")
  static ArtistEntity artistToEntity(ArtistDto artistDto) {
    return ArtistMapper.INSTANCE.toEntity(artistDto);
  }

  @Named("artistToJson")
  static ArtistJson artistToJson(ArtistDto artistDto) {
    return ArtistMapper.INSTANCE.toJson(artistDto);
  }

  @Named("artistFromEntity")
  static ArtistDto artistFromEntity(ArtistEntity artistEntity) {
    return ArtistMapper.INSTANCE.fromEntity(artistEntity);
  }

  @Named("artistFromJson")
  static ArtistDto artistFromJson(ArtistJson artistJson) {
    return ArtistMapper.INSTANCE.fromJson(artistJson);
  }
}
