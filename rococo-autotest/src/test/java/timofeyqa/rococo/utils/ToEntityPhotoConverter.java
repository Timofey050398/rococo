package timofeyqa.rococo.utils;

import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class ToEntityPhotoConverter {


  //TODO доделать обработку из сурсов
  @Nullable
  public static byte[] convert(@Nullable String str){
    if (StringUtils.isEmpty(str)) {
      return null;
    }
    return str.getBytes();
  }
}
