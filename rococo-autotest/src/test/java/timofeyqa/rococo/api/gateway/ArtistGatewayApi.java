package timofeyqa.rococo.api.gateway;


import retrofit2.Call;
import retrofit2.http.*;
import timofeyqa.rococo.model.rest.ArtistJson;
import timofeyqa.rococo.model.rest.pageable.RestResponsePage;

import javax.annotation.Nullable;

public interface ArtistGatewayApi {

  @GET("/api/artist/{id}")
  Call<ArtistJson> getArtist(@Path("id") String id);

  @GET("/api/artist")
  Call<RestResponsePage<ArtistJson>> getPage(
      @Query("page") int page,
      @Query("size") int size,
      @Query("name") @Nullable String name);

  @PATCH("/api/artist")
  Call<ArtistJson> updateArtist(
      @Header("Authorization") String bearerToken,
      @Body ArtistJson artistJson);

  @POST("/api/artist")
  Call<ArtistJson> createArtist(
      @Header("Authorization") String bearerToken,
      @Body ArtistJson artistJson);
}
