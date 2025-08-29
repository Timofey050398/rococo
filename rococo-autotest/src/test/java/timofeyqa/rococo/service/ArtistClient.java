package timofeyqa.rococo.service;

import io.qameta.allure.Step;
import timofeyqa.grpc.rococo.Artist;
import timofeyqa.rococo.mapper.GrpcMapper;
import timofeyqa.rococo.model.dto.ArtistDto;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public interface ArtistClient extends GrpcComparator<ArtistDto, Artist> {

  ArtistDto create(ArtistDto artistJson);

  Optional<ArtistDto> findByName(String name);

  List<ArtistDto> findAllById(List<UUID> uuids);

  @Override
  default String getId(Artist grpc){
    return grpc.getId();
  }

  @Override
  @Step("compare actual dto with expected")
  default void compareGrpc(ArtistDto expected, Artist actual) {
    assertAll(
      ()-> assertEquals(expected.id(), GrpcMapper.INSTANCE.fromStringToUuid(actual.getId()),"id not equal"),
      ()-> assertEquals(expected.name(), actual.getName(),"name not equal"),
      ()-> assertEquals(expected.biography(), actual.getBiography(),"biography not equal"),
      ()-> assertEquals(GrpcMapper.INSTANCE.fromByte(expected.photo()),actual.getPhoto(),"photo not equal")
    );
  }
}
