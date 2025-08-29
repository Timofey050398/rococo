package timofeyqa.rococo.service;

import io.qameta.allure.Step;
import timofeyqa.grpc.rococo.Painting;
import timofeyqa.rococo.mapper.GrpcMapper;
import timofeyqa.rococo.model.dto.PaintingDto;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public interface PaintingClient extends GrpcComparator<PaintingDto, Painting> {

  PaintingDto create(PaintingDto painting);

  Optional<PaintingDto> findByTitle(String title);

  List<PaintingDto> findAllById(List<UUID> uuids);

  List<PaintingDto> findAllByArtistId(UUID artistId);

  @Override
  default String getId(Painting grpc){
    return grpc.getId();
  }

  @Override
  @Step("compare actual dto with expected")
  default void compareGrpc(PaintingDto expected, Painting actual){
    assertAll(
        ()-> assertEquals(expected.id(), GrpcMapper.INSTANCE.fromStringToUuid(actual.getId()),"id not equal"),
        ()-> assertEquals(expected.title(), actual.getTitle(),"title not equal"),
        ()-> assertEquals(expected.description(), actual.getDescription(),"description not equal"),
        ()-> assertEquals(expected.artist().id().toString(), actual.getArtistId(),"artist not equal"),
        ()-> {
          if (expected.museum() != null) {
            assertEquals(expected.museum().id().toString(), actual.getMuseumId(), "museum not equal");
          }
        },
        ()->   assertEquals(GrpcMapper.INSTANCE.fromByte(expected.content()),actual.getContent(),"photo not equal")
    );
  }
}

