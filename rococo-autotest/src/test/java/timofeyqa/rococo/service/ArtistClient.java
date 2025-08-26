package timofeyqa.rococo.service;

import io.qameta.allure.Step;
import timofeyqa.grpc.rococo.Artist;
import timofeyqa.rococo.mapper.GrpcMapper;
import timofeyqa.rococo.model.dto.ArtistDto;
import timofeyqa.rococo.model.rest.ArtistJson;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public interface ArtistClient {

  ArtistDto create(ArtistDto artistJson);

  Optional<ArtistDto> findByName(String name);

  List<ArtistDto> findAllById(List<UUID> uuids);


  @Step("compare actual dto with expected")
  default void compareGrpc(ArtistDto expected, Artist actual) {
    assertAll(()-> {
      assertEquals(expected.id(), GrpcMapper.INSTANCE.fromStringToUuid(actual.getId()),"id not equal");
      assertEquals(expected.name(), actual.getName(),"name not equal");
      assertEquals(expected.biography(), actual.getBiography(),"biography not equal");
      assertEquals(GrpcMapper.INSTANCE.fromByte(expected.photo()),actual.getPhoto(),"photo not equal");
    });
  }

  @Step("compare actual set with expected set")
  default void compareGrpcSets(Set<ArtistDto> expectedSet, Set<Artist> actualSet) {

    assertEquals(expectedSet.size(), actualSet.size(), "Sets differ in size");

    Map<UUID, Artist> actualMap = actualSet.stream()
        .collect(Collectors.toMap(
            a -> GrpcMapper.INSTANCE.fromStringToUuid(a.getId()),
            a -> a
        ));

    for (ArtistDto expected : expectedSet) {
      Artist actual = actualMap.get(expected.id());
      if (actual == null) {
        fail("No matching Artist found for id " + expected.id());
      }
      compareGrpc(expected, actual);
    }
  }

}
