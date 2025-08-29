package timofeyqa.rococo.service.api;

import com.fasterxml.jackson.databind.JsonNode;
import timofeyqa.rococo.api.AuthApi;
import timofeyqa.rococo.api.core.CodeInterceptor;
import timofeyqa.rococo.api.core.RestClient;
import timofeyqa.rococo.api.core.ThreadSafeCookieStore;
import timofeyqa.rococo.jupiter.extension.ApiLoginExtension;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.api.core.ErrorAsserter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

import static timofeyqa.rococo.utils.OauthUtils.generateCodeChallenge;
import static timofeyqa.rococo.utils.OauthUtils.generateCodeVerifier;

@ParametersAreNonnullByDefault
public class AuthRestClient extends RestClient implements ErrorAsserter {

    private final AuthApi authApi;
    private static final String authorizedUri = CFG.frontUrl() +"authorized";
    private static final String CLIENT_ID = "client";


    public AuthRestClient() {
        super(CFG.authUrl(), true, new CodeInterceptor());
        this.authApi = create(AuthApi.class);
    }

    public void preRequest(String codeVerifier){
        execute(authApi.authorize(
                "code",
                CLIENT_ID,
                "openid",
                authorizedUri,
                generateCodeChallenge(codeVerifier),
                "S256"
        ));
    }

    public void login(UserJson user){
        execute(
                authApi.login(
                        user.username(),
                        user.password(),
                        ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
                )
        );
    }

    @Nonnull
    public String token(String code, String codeVerifier){
        JsonNode node = execute(authApi.token(
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
