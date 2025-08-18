package timofeyqa.rococo.service.api;

import timofeyqa.rococo.api.AuthApi;
import timofeyqa.rococo.api.UserdataApi;
import timofeyqa.rococo.api.core.RestClient;
import timofeyqa.rococo.api.core.ThreadSafeCookieStore;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.service.UserClient;
import timofeyqa.rococo.utils.SuccessRequestExecutor;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class UsersApiClient implements UserClient {

    private static final Config CFG = Config.getInstance();

    private final SuccessRequestExecutor sre = new SuccessRequestExecutor();

    private final AuthApi authApi = new RestClient.EmtyRestClient(CFG.authUrl()).create(AuthApi.class);
    private final UserdataApi userdataApi = new RestClient.EmtyRestClient(CFG.userdataUrl()).create(UserdataApi.class);


    @Override
    @Step("Create user with username {username} and password {password}")
    @Nonnull
    public UserJson createUser(String username, String password) {
        return Objects.requireNonNull(
                sre.<UserJson>executeRequest(
                    authApi.getRegisterPage(),
                    authApi.register(
                            username,
                            password,
                            password,
                            ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
                    ),
                    userdataApi.getUser(username)
                ))
                .withPassword(CFG.defaultPassword());
    }

    @Override
    @Step("Get user {username}")
    public UserJson getUser(String username){
        return sre.executeRequest(userdataApi.getUser(username));
    }

    @Override
    @Step("Update user {user}")
    public UserJson updateUser(UserJson user){
        return sre.executeRequest(userdataApi.updateUser(user.username(),user));
    }
}
