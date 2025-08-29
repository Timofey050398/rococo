package timofeyqa.rococo.api.gateway;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import timofeyqa.rococo.model.rest.UserJson;

public interface UserGatewayApi {

  @GET("/api/user")
  Call<UserJson> getUser(@Header("Authorization") String bearerToken);

  @PATCH("/api/user")
  Call<UserJson> updateUser(
      @Body UserJson user,
      @Header("Authorization") String bearerToken
  );

}
