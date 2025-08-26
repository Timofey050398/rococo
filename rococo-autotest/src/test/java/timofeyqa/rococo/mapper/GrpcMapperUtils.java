package timofeyqa.rococo.mapper;

import com.google.protobuf.ByteString;
import org.mapstruct.Named;
import timofeyqa.grpc.rococo.Uuid;

import java.util.UUID;

public interface GrpcMapperUtils {

  @Named("fromGrpcUuid")
  static UUID fromGrpcUuid(Uuid uuid) {
    if (uuid.getUuid().isEmpty()) return null;
    return UUID.fromString(uuid.getUuid());
  }

  @Named("fromByteString")
  static byte[] fromByteString(ByteString byteString) {
    return byteString.isEmpty()
        ? null
        : byteString.toByteArray();
  }

  @Named("stringFromGrpc")
  static String stringFromGrpc(String str) {
    return str.isEmpty()
        ? null
        : str;
  }

  @Named("fromStringToUuid")
  static UUID fromStringToUuid(String uuid) {
    return uuid.isEmpty() ? null : UUID.fromString(uuid);
  }
}
