package timofeyqa.rococo.api.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import okhttp3.ResponseBody;
import retrofit2.Response;
import timofeyqa.rococo.model.rest.ApiError;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;

@UtilityClass
class ErrorParser {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  static ApiError parseError(@Nullable Response<?> response) {
    Objects.requireNonNull(response, "Response не может быть null");

    ResponseBody errorBody = response.errorBody();
    if (errorBody == null) {
      throw new IllegalArgumentException("ErrorBody отсутствует в response");
    }

    String body;
    try {
      body = errorBody.string();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }


    try {
      return MAPPER.readValue(body, ApiError.class);
    } catch (IOException e) {
      throw new RuntimeException("Ошибка парсинга errorBody в ApiError: "+body, e);
    }
  }
}
