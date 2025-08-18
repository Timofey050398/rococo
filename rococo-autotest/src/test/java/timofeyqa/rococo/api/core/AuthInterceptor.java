package timofeyqa.rococo.api.core;

import org.jetbrains.annotations.NotNull;
import timofeyqa.rococo.jupiter.extension.ApiLoginExtension;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class AuthInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        String token = ApiLoginExtension.getToken();

        if (StringUtils.isNotBlank(token)) {

            Request authorisedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();

            return chain.proceed(authorisedRequest);
        }

        return chain.proceed(originalRequest);
    }
}
