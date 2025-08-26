package timofeyqa.rococo.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import timofeyqa.grpc.rococo.Museum;
import timofeyqa.rococo.data.entity.CountryEntity;
import timofeyqa.rococo.data.entity.MuseumEntity;
import timofeyqa.rococo.model.dto.MuseumDto;
import timofeyqa.rococo.model.rest.CountryJson;
import timofeyqa.rococo.model.rest.GeoJson;
import timofeyqa.rococo.model.rest.MuseumJson;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MuseumMapper extends CommonMapperUtils, PaintingSetMapper {

  MuseumMapper INSTANCE = Mappers.getMapper(MuseumMapper.class);


  @Mapping(source = "geo", target = "city", qualifiedByName = "geoToCity")
  @Mapping(source = "geo", target = "country", qualifiedByName = "geoToCountry")
  @Mapping(target = "paintings", ignore = true)
  MuseumEntity toEntity(MuseumDto museum);

  @Mapping(source = "photo", target = "photo", qualifiedByName = "byteToString")
  @Mapping(source = "paintings", target = "paintings", qualifiedByName = "toJsonSet")
  MuseumJson toJson(MuseumDto museum);

  @Mapping(source = ".", target = "geo", qualifiedByName = "toGeo")
  @Mapping(target = "paintings", source = ".", qualifiedByName = "emptySet")
  MuseumDto fromEntity(MuseumEntity museum);

  @Mapping(source = "photo", target = "photo", qualifiedByName = "stringToBytes")
  @Mapping(source = "paintings", target = "paintings", qualifiedByName = "fromJsonSet")
  MuseumDto fromJson(MuseumJson museumJson);

  @Mapping(source = "id",target = "id",qualifiedByName = "fromStringToUuid")
  @Mapping(source = "title",target = "title",qualifiedByName = "stringFromGrpc")
  @Mapping(source = "description",target = "description",qualifiedByName = "stringFromGrpc")
  @Mapping(source = "photo", target = "photo",qualifiedByName = "fromByteString")
  @Mapping(source = "city",target = "geo.city",qualifiedByName = "stringFromGrpc")
  @Mapping(source = "countryId",target = "geo.country.id",qualifiedByName = "fromStringToUuid")
  @Mapping(target = "geo.country.name",ignore = true)
  @Mapping(target = "paintings", source = ".", qualifiedByName = "emptySet")
  MuseumDto fromGrpc(Museum museum);

  @Named("geoToCity")
  static String geoToCity(GeoJson geo) {
    return geo == null || geo.city() == null
        ? null
        : geo.city();
  }

  @Named("geoToCountry")
  static CountryEntity geoToCountry(GeoJson geo) {
    return geo == null || geo.city() == null
        ? null
        : geo.country().toEntity();
  }

  @Named("toGeo")
  static GeoJson toGeo(MuseumEntity museum) {
    return new GeoJson(museum.getCity(), CountryJson.fromEntity(museum.getCountry()));
  }
}
