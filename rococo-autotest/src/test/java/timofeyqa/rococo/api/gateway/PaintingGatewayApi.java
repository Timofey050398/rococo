package timofeyqa.rococo.api.gateway;

import retrofit2.Call;
import retrofit2.http.*;
import timofeyqa.rococo.model.rest.PaintingJson;
import timofeyqa.rococo.model.rest.pageable.RestResponsePage;

import javax.annotation.Nullable;

public interface PaintingGatewayApi {

  @GET("/api/painting/{id}")
  Call<PaintingJson> getPainting(@Path("id") String id);

  @GET("/api/painting")
  Call<RestResponsePage<PaintingJson>> getPage(
      @Query("page") int page,
      @Query("size") int size,
      @Query("title") @Nullable String title);

  @GET("/api/painting/author/{artistId}")
  Call<RestResponsePage<PaintingJson>> getPageByArtist(
      @Path("artistId") String artistId,
      @Query("page") int page,
      @Query("size") int size);

  @PATCH("/api/painting")
  Call<PaintingJson> updatePainting(
      @Header("Authorization") String bearerToken,
      @Body PaintingJson PaintingJson);

  @POST("/api/painting")
  Call<PaintingJson> createPainting(
      @Header("Authorization") String bearerToken,
      @Body PaintingJson PaintingJson);
}
