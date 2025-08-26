package timofeyqa.rococo.service;

import io.qameta.allure.Step;
import timofeyqa.grpc.rococo.Painting;
import timofeyqa.rococo.mapper.GrpcMapper;
import timofeyqa.rococo.model.dto.PaintingDto;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public interface PaintingClient {

  PaintingDto create(PaintingDto painting);

  Optional<PaintingDto> findByTitle(String title);

  List<PaintingDto> findAllById(List<UUID> uuids);

  List<PaintingDto> findAllByArtistId(UUID artistId);

  void deleteList(List<UUID> uuidList);

  @Step("compare actual dto with expected")
  default void compareGrpc(PaintingDto expected, Painting actual){
    assertAll(()-> {
      assertEquals(expected.id(), GrpcMapper.INSTANCE.fromStringToUuid(actual.getId()),"id not equal");
      assertEquals(expected.title(), actual.getTitle(),"title not equal");
      assertEquals(expected.description(), actual.getDescription(),"description not equal");
      assertEquals(expected.artist().id().toString(), actual.getArtistId(),"artist not equal");
      if (expected.museum() != null) {
        assertEquals(expected.museum().id().toString(), actual.getMuseumId(), "museum not equal");
      }

      assertEquals(GrpcMapper.INSTANCE.fromByte(expected.content()),actual.getContent(),"photo not equal");
    });
  }

  @Step("compare actual set with expected set")
  default void compareGrpcSets(Set<PaintingDto> expectedSet, Set<Painting> actualSet) {

    assertEquals(expectedSet.size(), actualSet.size(), "Sets differ in size");

    Map<UUID, Painting> actualMap = actualSet.stream()
        .collect(Collectors.toMap(
            a -> GrpcMapper.INSTANCE.fromStringToUuid(a.getId()),
            a -> a
        ));

    for (PaintingDto expected : expectedSet) {
      Painting actual = actualMap.get(expected.id());
      if (actual == null) {
        fail("No matching Painting found for id " + expected.id());
      }
      compareGrpc(expected, actual);
    }
  }
}

