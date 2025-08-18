package timofeyqa.rococo.service.api.grpc;

import com.google.protobuf.Empty;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.ex.BadRequestException;
import timofeyqa.rococo.model.CountryJson;
import timofeyqa.rococo.model.GeoJson;
import timofeyqa.rococo.model.MuseumJson;
import timofeyqa.rococo.mappers.CountryMapper;
import timofeyqa.rococo.mappers.UuidMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static timofeyqa.rococo.service.utils.ToCompletableFuture.toCf;
import static timofeyqa.rococo.mappers.UuidMapper.fromUuidList;

@Service
@RequiredArgsConstructor
public class GrpcGeoClient implements GrpcClient<CountryJson> {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcGeoClient.class);

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
                    .map(CountryMapper::fromGrpc)
                    .toList();
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    @Override
    public @Nonnull CompletableFuture<CountryJson> getById(UUID id){
        return toCf(geoStub.getGeo(UuidMapper.fromUuid(id)))
            .thenApply(CountryMapper::fromGrpc);
    }

    @Nonnull
    CompletableFuture<GeoJson> getMuseumGeo(@Nonnull MuseumJson museum) {
        var geo = museum.geo();
        return Optional.ofNullable(geo)
            .map(GeoJson::country)
            .map(CountryJson::id)
            .map(UuidMapper::fromUuid)
            .map(uuid -> toCf(geoStub.getGeo(uuid))
                .thenApply(CountryMapper::fromGrpc)
                .thenApply(country -> geo.toBuilder()
                    .country(country)
                    .build()))
            .orElseGet(() -> CompletableFuture.completedFuture(geo));
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
                        .map(CountryMapper::fromGrpc)
                        .toList()
                );
    }

    void validateCountry(CountryJson country) {
        validateChildObject(country);

        if (country == null || country.id() == null) {
            return;
        }

        String actualName = geoBlockingStub
            .getGeo(UuidMapper.fromUuid(country.id()))
            .getName();

        if (!StringUtils.isEmpty(country.name()) && !actualName.equals(country.name())) {
            throw new BadRequestException("Country with provided combination of id and name does not exist");
        }
    }

}
