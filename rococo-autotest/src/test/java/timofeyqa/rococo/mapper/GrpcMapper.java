package timofeyqa.rococo.mapper;

import com.google.protobuf.ByteString;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import timofeyqa.grpc.rococo.Uuid;
import timofeyqa.grpc.rococo.UuidList;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GrpcMapper {

  public static final GrpcMapper INSTANCE = new GrpcMapper();

  public Uuid toGrpcUuid(UUID uuid) {
    String str = uuid == null
        ? ""
        : uuid.toString();
    return Uuid.newBuilder().setUuid(str).build();
  }

  public UuidList toGrpcUuidList(List<UUID> uuids) {
    if (uuids == null) return UuidList.newBuilder().build();
    var uuidList = uuids.stream()
        .map(this::toGrpcUuid)
        .collect(Collectors.toList());

    return UuidList.newBuilder()
        .addAllUuid(uuidList)
        .build();
  }

  public UUID fromStringToUuid(String uuid) {
    return GrpcMapperUtils.fromStringToUuid(uuid);
  }

  public ByteString fromByte(byte[] bytes) {
    return ByteString.copyFrom(bytes);
  }
}
