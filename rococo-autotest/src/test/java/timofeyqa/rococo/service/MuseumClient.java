package timofeyqa.rococo.service;

import io.qameta.allure.Step;
import timofeyqa.grpc.rococo.Museum;
import timofeyqa.rococo.mapper.GrpcMapper;
import timofeyqa.rococo.model.dto.MuseumDto;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public interface MuseumClient {

  MuseumDto create(MuseumDto museum);

  Optional<MuseumDto> findByTitle(String title);

  List<MuseumDto> findAllById(List<UUID> uuids);

  @Step("compare actual dto with expected")
  default void compareGrpc(MuseumDto expected, Museum actual) {
    assertAll(()-> {
      assertEquals(expected.id(), GrpcMapper.INSTANCE.fromStringToUuid(actual.getId()),"id not equal");
      assertEquals(expected.title(), actual.getTitle(),"title not equal");
      assertEquals(expected.description(), actual.getDescription(),"description not equal");
      if(expected.geo()!=null){
        assertEquals(expected.geo().city(), actual.getCity(),"city not equal");
        if(expected.geo().country()!=null){
          var country = expected.geo().country();
          assertEquals(country.id().toString(), actual.getCountryId(),"country not equal");
        }
      }
      assertEquals(GrpcMapper.INSTANCE.fromByte(expected.photo()),actual.getPhoto(),"photo not equal");
    });
  }

  @Step("compare actual set with expected set")
  default void compareGrpcSets(Set<MuseumDto> expectedSet, Set<Museum> actualSet) {

    assertEquals(expectedSet.size(), actualSet.size(), "Sets differ in size");

    Map<UUID, Museum> actualMap = actualSet.stream()
        .collect(Collectors.toMap(
            a -> GrpcMapper.INSTANCE.fromStringToUuid(a.getId()),
            a -> a
        ));

    for (MuseumDto expected : expectedSet) {
      Museum actual = actualMap.get(expected.id());
      if (actual == null) {
        fail("No matching Artist found for id " + expected.id());
      }
      compareGrpc(expected, actual);
    }
  }
}
