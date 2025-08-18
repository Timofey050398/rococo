package timofeyqa.rococo.api;

import timofeyqa.rococo.model.rest.UserJson;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserdataApi {
    @GET("/internal/api/user")
    Call<UserJson> getUser(@Query("username") String username);

    @PATCH("/internal/api/user")
    Call<UserJson> updateUser(@Query("username") String username, @Body UserJson user);

}
