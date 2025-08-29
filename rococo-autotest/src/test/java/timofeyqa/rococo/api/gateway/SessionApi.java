package timofeyqa.rococo.api.gateway;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import timofeyqa.rococo.model.rest.SessionJson;

public interface SessionApi {

  @GET("/api/session")
  Call<SessionJson> session(@Header("Authorization") String bearerToken);
}
