package timofeyqa.rococo.mapper;

import org.mapstruct.Named;

import static timofeyqa.rococo.utils.PhotoConverter.convert;

public interface CommonMapperUtils extends GrpcMapperUtils {

  @Named("byteToString")
  static String byteToString(byte[] bytes) {
    return convert(bytes);
  }

  @Named("stringToBytes")
  static byte[] stringToBytes(String str) {
    return convert(str);
  }
}
