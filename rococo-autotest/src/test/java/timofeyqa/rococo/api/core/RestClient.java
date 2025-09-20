package timofeyqa.rococo.api.core;

import org.slf4j.LoggerFactory;
import retrofit2.*;
import timofeyqa.rococo.config.Config;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import static okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS;
import static org.apache.commons.lang.ArrayUtils.isNotEmpty;
import static timofeyqa.rococo.utils.LogUtils.maskLongParams;

public abstract class RestClient implements RequestExecutor {

    protected static final Config CFG = Config.getInstance();

    private final OkHttpClient okHttpClient;
    private final Retrofit retrofit;

    public RestClient() {
        this(CFG.gatewayUrl());
    }

    public RestClient(String baseUrl) {
        this(baseUrl, false, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.BODY, false);
    }

    public RestClient(boolean isContentOversized){
        this(CFG.gatewayUrl(), isContentOversized);
    }

    public RestClient(String baseUrl, boolean isContentOversized) {
        this(baseUrl, false, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.BODY, isContentOversized);
    }

    public RestClient(String baseUrl, boolean followRedirect, @Nullable Interceptor... interceptors) {
        this(baseUrl, followRedirect, JacksonConverterFactory.create(), HEADERS, false, interceptors);
    }

    public RestClient(String baseUrl, boolean followRedirect, Converter.Factory factory, HttpLoggingInterceptor.Level level, boolean isContentOversized, @Nullable Interceptor... interceptors) {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .followRedirects(followRedirect);

        builder.addNetworkInterceptor(new HttpLoggingInterceptor(
                message -> LoggerFactory.getLogger(RestClient.class).debug(maskLongParams(message))
        ).setLevel(level));
        builder.addNetworkInterceptor(
                new AllureRestInterceptor()
                        .setRequestTemplate("http-request.ftl")
                        .setResponseTemplate("http-response.ftl")
        );
        if (isNotEmpty(interceptors)) {
            for (Interceptor interceptor : interceptors) {
                builder.addNetworkInterceptor(interceptor);
            }
        }

        builder.cookieJar(
                new JavaNetCookieJar(
                        new CookieManager(
                                ThreadSafeCookieStore.INSTANCE,
                                CookiePolicy.ACCEPT_ALL
                        )
                )
        );
        if (isContentOversized) {
            builder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);
        }
        this.okHttpClient = builder.build();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(factory)
                .build();
    }


    @Nonnull
    public <T> T create(final Class<T> service) {
        return this.retrofit.create(service);
    }

    public static final class EmptyRestClient extends RestClient {
        public EmptyRestClient(String baseUrl) {
            super(baseUrl);
        }

        public EmptyRestClient(String baseUrl, boolean followRedirect, @Nullable Interceptor... interceptors) {
            super(baseUrl, followRedirect, interceptors);
        }
    }
}