package timofeyqa.rococo.utils;

import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@UtilityClass
@ParametersAreNonnullByDefault
public class PhotoConverter {

  public static String loadImageAsString(String resourcePath) {
    try (InputStream is = PhotoConverter.class.getClassLoader().getResourceAsStream(resourcePath)) {
      if (is == null) throw new RuntimeException("Resource not found: " + resourcePath);
      byte[] bytes = is.readAllBytes();
      return convert(bytes);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static byte[] loadImageAsBytes(String resourcePath) {
    try (InputStream is = PhotoConverter.class.getClassLoader().getResourceAsStream(resourcePath)) {
      if (is == null) throw new RuntimeException("Resource not found: " + resourcePath);
      return is.readAllBytes();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Nullable
  public static byte[] convert(@Nullable String str) {
    if (StringUtils.isEmpty(str)) return null;
    if (str.startsWith("data:")) {
      str = str.replaceFirst("^data:[^;]+;base64,", "");
      if (StringUtils.isEmpty(str.trim())) return null;
      return Base64.getDecoder().decode(str);
    }
    return str.getBytes(StandardCharsets.UTF_8);
  }

  @Nullable
  public static String convert(@Nullable byte[] bytes){
    if (bytes == null || bytes.length == 0) return null;
    String base64 = Base64.getEncoder().encodeToString(bytes);
    return "data:image/png;base64," + base64;
  }
}