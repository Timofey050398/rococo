package timofeyqa.rococo.service.api;

import com.fasterxml.jackson.databind.JsonNode;
import timofeyqa.rococo.api.AuthApi;
import timofeyqa.rococo.api.core.CodeInterceptor;
import timofeyqa.rococo.api.core.RestClient;
import timofeyqa.rococo.api.core.ThreadSafeCookieStore;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.jupiter.extension.ApiLoginExtension;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.utils.SuccessRequestExecutor;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

import static timofeyqa.rococo.utils.OauthUtils.generateCodeChallenge;
import static timofeyqa.rococo.utils.OauthUtils.generateCodeVerifier;

@ParametersAreNonnullByDefault
public class AuthApiClient extends RestClient {

    private static final Config CFG = Config.getInstance();
    private final SuccessRequestExecutor sre = new SuccessRequestExecutor();
    private final AuthApi authApi;
    private static final String authorizedUri = CFG.frontUrl() +"authorized";
    private static final String CLIENT_ID = "client";


    public AuthApiClient() {
        super(CFG.authUrl(), true, new CodeInterceptor());
        this.authApi = create(AuthApi.class);
    }

    public void preRequest(String codeVerifier){
        sre.executeRequest(authApi.authorize(
                "code",
                CLIENT_ID,
                "openid",
                authorizedUri,
                generateCodeChallenge(codeVerifier),
                "S256"
        ));
    }

    public void login(UserJson user){
        sre.executeRequest(
                authApi.login(
                        user.username(),
                        user.password(),
                        ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
                )
        );
    }

    @Nonnull
    public String token(String code, String codeVerifier){
        JsonNode node = sre.executeRequest(authApi.token(
                CLIENT_ID,
                authorizedUri,
                "authorization_code",
                code,
                codeVerifier
        ));

        return Objects.requireNonNull(node)
                .get("id_token")
                .asText();
    }

    @Nonnull
    public String token(UserJson userJson){
        String codeVerifier = generateCodeVerifier();
        preRequest(codeVerifier);
        login(userJson);
        return token(ApiLoginExtension.getCode(),codeVerifier);
    }
}
