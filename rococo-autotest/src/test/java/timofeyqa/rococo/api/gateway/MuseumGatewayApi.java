package timofeyqa.rococo.api.gateway;

import retrofit2.Call;
import retrofit2.http.*;
import timofeyqa.rococo.model.rest.MuseumJson;
import timofeyqa.rococo.model.rest.pageable.RestResponsePage;

import javax.annotation.Nullable;

public interface MuseumGatewayApi {
  
  @GET("/api/museum/{id}")
  Call<MuseumJson> getMuseum(@Path("id") String id);

  @GET("/api/museum")
  Call<RestResponsePage<MuseumJson>> getPage(
      @Query("page") int page,
      @Query("size") int size,
      @Query("title") @Nullable String title);

  @PATCH("/api/museum")
  Call<MuseumJson> updateMuseum(
      @Header("Authorization") String bearerToken,
      @Body MuseumJson MuseumJson);

  @POST("/api/museum")
  Call<MuseumJson> createMuseum(
      @Header("Authorization") String bearerToken,
      @Body MuseumJson MuseumJson);
}
