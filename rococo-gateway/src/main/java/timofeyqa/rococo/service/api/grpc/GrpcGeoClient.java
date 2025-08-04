package timofeyqa.rococo.service.api.grpc;

import com.google.protobuf.Empty;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.model.ArtistJson;
import timofeyqa.rococo.model.CountryJson;
import timofeyqa.rococo.model.GeoJson;
import timofeyqa.rococo.model.MuseumJson;
import timofeyqa.rococo.service.utils.UuidUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static timofeyqa.rococo.service.utils.ToCompletableFuture.toCf;
import static timofeyqa.rococo.service.utils.UuidUtil.fromUuidList;

@Service
@RequiredArgsConstructor
public class GrpcGeoClient {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcGeoClient.class);

    @GrpcClient("grpcGeoClient")
    private final RococoGeoServiceGrpc.RococoGeoServiceBlockingStub geoBlockingStub;
    private final RococoGeoServiceGrpc.RococoGeoServiceFutureStub geoStub;


    @Nonnull
    @Cacheable("allCountries")
    public List<CountryJson> allCountries(){
        try {
            final GeoListResponse response = geoBlockingStub
                    .getAll(Empty.getDefaultInstance());
            return response.getGeoList()
                    .stream()
                    .map(CountryJson::fromGrpc)
                    .toList();
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    CountryJson getById(@Nonnull UUID id){
        return CountryJson.fromGrpc(geoBlockingStub.getGeo(UuidUtil.fromUuid(id)));
    }

    @Nonnull
    CompletableFuture<GeoJson> getMuseumGeo(MuseumJson museum) {
        if (museum.geo() == null || museum.geo().country() == null){
            return CompletableFuture.completedFuture(museum.geo());
        }
        final UUID countryId = museum.geo().country().id();
        if (countryId == null){
            return CompletableFuture.completedFuture(museum.geo());
        }
        return toCf(
                geoStub.getGeo(
                        Uuid.newBuilder()
                                .setUuid(countryId.toString())
                                .build()
                )
        ).thenApply(CountryJson::fromGrpc)
                .thenApply(country -> museum.geo().toBuilder()
                        .country(country)
                        .build());
    }

    @Nonnull
    CompletableFuture<List<CountryJson>> getCountriesByIds(@Nonnull List<UUID> ids){
        if(ids.isEmpty()){
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        return toCf(
                geoStub.getGeosByUuids(fromUuidList(ids))
        )
                .thenApply(geoList -> geoList
                        .getGeoList()
                        .stream()
                        .map(CountryJson::fromGrpc)
                        .toList()
                );
    }
}
