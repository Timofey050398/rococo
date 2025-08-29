package timofeyqa.rococo.api.core;

import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

public interface RequestExecutor {

  @Nullable
  default  <T> T execute(@Nonnull Call<?>... calls) {
    try {
      Response<?> response = null;
      for(Call<?> call : calls) {
        response = call.execute();
      }
      if (!requireNonNull(response).isSuccessful()) {
        throw new HttpException(response);
      }
      @SuppressWarnings("unchecked")
      T result = (T) response.body();
      return result;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
