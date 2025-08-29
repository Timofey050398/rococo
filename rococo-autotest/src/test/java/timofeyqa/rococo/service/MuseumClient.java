package timofeyqa.rococo.service;

import io.qameta.allure.Step;
import timofeyqa.grpc.rococo.Museum;
import timofeyqa.rococo.mapper.GrpcMapper;
import timofeyqa.rococo.model.dto.MuseumDto;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public interface MuseumClient extends GrpcComparator<MuseumDto, Museum> {

  MuseumDto create(MuseumDto museum);

  Optional<MuseumDto> findByTitle(String title);

  List<MuseumDto> findAllById(List<UUID> uuids);

  @Override
  default String getId(Museum grpc){
    return grpc.getId();
  }

  @Override
  @Step("compare actual dto with expected")
  default void compareGrpc(MuseumDto expected, Museum actual) {
    assertAll(
        ()-> assertEquals(expected.id(), GrpcMapper.INSTANCE.fromStringToUuid(actual.getId()),"id not equal"),
        ()-> assertEquals(expected.title(), actual.getTitle(),"title not equal"),
        ()-> assertEquals(expected.description(), actual.getDescription(),"description not equal"),
        ()-> {
          if (expected.geo() != null) {
            assertEquals(expected.geo().city(), actual.getCity(), "city not equal");
          }
        },
        () -> {
          if (expected.geo() != null && expected.geo().country() != null) {
            var country = expected.geo().country();
            assertEquals(country.id().toString(), actual.getCountryId(), "country not equal");
          }
        },
        ()->  assertEquals(GrpcMapper.INSTANCE.fromByte(expected.photo()),actual.getPhoto(),"photo not equal")
    );
  }
}
