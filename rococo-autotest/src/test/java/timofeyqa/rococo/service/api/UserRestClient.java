package timofeyqa.rococo.service.api;

import timofeyqa.rococo.api.AuthApi;
import timofeyqa.rococo.api.UserdataApi;
import timofeyqa.rococo.api.core.*;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.service.UserClient;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class UserRestClient implements UserClient, ErrorAsserter, RequestExecutor {

    private static final Config CFG = Config.getInstance();

    private final AuthApi authApi = new RestClient.EmptyRestClient(CFG.authUrl()).create(AuthApi.class);
    private final UserdataApi userdataApi = new RestClient.EmptyRestClient(CFG.userdataUrl()).create(UserdataApi.class);


    @Override
    @Step("Create user with username {username} and password {password}")
    @Nonnull
    public UserJson createUser(String username, String password) {
        execute(authApi.getRegisterPage());
        execute(authApi.register(
            username,
            password,
            password,
            ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
        ));
        return Objects.requireNonNull(
                this.<UserJson>execute(userdataApi.getUser(username)))
                .withPassword(CFG.defaultPassword());
    }

    @Override
    @Step("Get user {username}")
    public UserJson getUser(String username){
        return execute(userdataApi.getUser(username));
    }

    @Step("Update user {user}")
    public UserJson updateUser(String username, UserJson user){
        return execute(userdataApi.updateUser(username,user));
    }

    @Override
    @Step("Update user {user}")
    public UserJson updateUser(UserJson user){
        return updateUser(user.username(), user);
    }
}
