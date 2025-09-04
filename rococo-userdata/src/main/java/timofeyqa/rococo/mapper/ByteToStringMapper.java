package timofeyqa.rococo.mapper;

import io.micrometer.common.util.StringUtils;

import java.util.Base64;

interface ByteToStringMapper {

  default String fromByte(byte[] avatar) {
    return avatar == null || avatar.length == 0
        ? null
        : "data:image/png;base64," + Base64.getEncoder().encodeToString(avatar);
  }

  default byte[] toByte(String str) {
    if (StringUtils.isEmpty(str)) return new byte[0];
    if (str.startsWith("data:")) {
      str = str.replaceFirst("^data:[^;]+;base64,", "");
    }
    if (StringUtils.isEmpty(str.trim())) return new byte[0];

    return Base64.getDecoder().decode(str);
  }
}
