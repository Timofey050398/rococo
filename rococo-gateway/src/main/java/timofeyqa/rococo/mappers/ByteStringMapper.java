package timofeyqa.rococo.mappers;

import com.google.protobuf.ByteString;
import org.apache.commons.lang3.StringUtils;

import java.util.Base64;

public interface ByteStringMapper {

  static String fromByteString(ByteString content) {
    return content.isEmpty()
        ? null
        : "data:image/png;base64," + Base64.getEncoder().encodeToString(content.toByteArray());
  }

  static ByteString toByteString(String str) {
    if (StringUtils.isEmpty(str)) return ByteString.EMPTY;
    if (str.startsWith("data:")) {
      str = str.replaceFirst("^data:[^;]+;base64,", "");
    }
    if (StringUtils.isEmpty(str.trim())) return ByteString.EMPTY;

    byte[] bytes = Base64.getDecoder().decode(str);
    return ByteString.copyFrom(bytes);
  }
}
