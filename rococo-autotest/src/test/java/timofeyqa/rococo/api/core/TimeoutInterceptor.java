package timofeyqa.rococo.api.core;

import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TimeoutInterceptor implements Interceptor {

  @NotNull
  @Override
  public Response intercept(@NotNull Chain chain) throws IOException {
    return chain
        .withConnectTimeout(30, TimeUnit.SECONDS)
        .withReadTimeout(60, TimeUnit.SECONDS)
        .withWriteTimeout(60, TimeUnit.SECONDS)
        .proceed(chain.request());
  }
}
