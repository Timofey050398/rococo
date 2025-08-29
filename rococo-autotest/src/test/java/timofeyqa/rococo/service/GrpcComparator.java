package timofeyqa.rococo.service;

import io.qameta.allure.Step;
import timofeyqa.rococo.mapper.GrpcMapper;
import timofeyqa.rococo.model.rest.ContentImpl;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public interface GrpcComparator<Dto extends ContentImpl,Grpc> {
  @Step("compare actual object with expected")
  void compareGrpc(Dto expected, Grpc actual);

  String getId(Grpc actual);

  @Step("compare actual set with expected set")
  default void compareGrpcSets(Set<Dto> expectedSet, Set<Grpc> actualSet) {

    assertEquals(expectedSet.size(), actualSet.size(), "Sets differ in size");

    Map<UUID, Grpc> actualMap = actualSet.stream()
        .collect(Collectors.toMap(
            a -> GrpcMapper.INSTANCE.fromStringToUuid(getId(a)),
            Function.identity()
        ));

    assertAll(expectedSet.stream()
        .map(expected -> () -> {
          Grpc actual = actualMap.get(expected.id());
          assertNotNull(actual, "No matching element found for id " + expected.id());
          compareGrpc(expected, actual);
        })
    );
  }
}
