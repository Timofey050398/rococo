package timofeyqa.rococo.api.gateway;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import timofeyqa.rococo.model.rest.CountryJson;
import timofeyqa.rococo.model.rest.pageable.RestResponsePage;

public interface GeoGatewayApi {

  @GET("/api/country")
  Call<RestResponsePage<CountryJson>> getCountries(@Query("page") int page, @Query("size") int size);
}
