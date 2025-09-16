package timofeyqa.rococo.service.api;

import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;
import timofeyqa.rococo.api.AuthApi;
import timofeyqa.rococo.api.UserdataApi;
import timofeyqa.rococo.api.core.*;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.service.UserClient;
import io.qameta.allure.Step;
import timofeyqa.rococo.utils.waiter.Waiter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

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
        UserJson user = Waiter.getNonNull(() -> {
            try {
                Response<UserJson> response = userdataApi.getUser(username)
                    .execute();
                if (!requireNonNull(response).isSuccessful()) {
                    return null;
                }
                return response.body();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return Objects.requireNonNull(user)
                .withPassword(password);
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
