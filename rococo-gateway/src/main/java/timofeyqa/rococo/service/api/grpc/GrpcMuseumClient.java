package timofeyqa.rococo.service.api.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.model.CountryJson;
import timofeyqa.rococo.model.GeoJson;
import timofeyqa.rococo.model.MuseumJson;
import timofeyqa.rococo.model.page.RestPage;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static timofeyqa.rococo.service.utils.Paginate.toGrpcPageable;
import static timofeyqa.rococo.service.utils.ToCompletableFuture.toCf;
import static timofeyqa.rococo.service.utils.UuidUtil.fromUuidList;

@Service
@RequiredArgsConstructor
public class GrpcMuseumClient {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcMuseumClient.class);

    private final RococoMuseumServiceGrpc.RococoMuseumServiceFutureStub museumStub;
    private final RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub museumBlockingStub;
    private final GrpcGeoClient grpcGeoClient;



    public @Nonnull CompletableFuture<MuseumJson> getMuseumById(UUID id){
        if (id == null){
            return CompletableFuture.completedFuture(null);
        }
        return toCf(
                museumStub.getMuseum(
                        Uuid.newBuilder()
                                .setUuid(id.toString())
                                .build()
                )
        ).thenApply(MuseumJson::fromGrpc)
                .thenCompose(museum ->
                        grpcGeoClient.getMuseumGeo(museum)
                                .thenApply(geo -> museum.toBuilder()
                                        .geo(geo)
                                        .build())
                );
    }

    public CompletableFuture<RestPage<MuseumJson>> getMuseumPage(Pageable pageable) {
        return toCf(museumStub.getMuseumPage(toGrpcPageable(pageable)))
                .thenApply(response -> MuseumJson.fromGrpcPage(response, pageable))
                .thenCompose(museumPage ->
                        enrichMuseumsWithCountries(museumPage.getContent())
                                .thenApply(updated -> new RestPage<>(updated, pageable, museumPage.getTotalElements()))
                );
    }


    @Nonnull
    public CompletableFuture<List<MuseumJson>> getMuseumsByIds(@Nonnull List<UUID> ids) {
        if (ids.isEmpty()) {
            return CompletableFuture.completedFuture(List.of());
        }

        return toCf(museumStub.getMuseumsByUuids(fromUuidList(ids)))
                .thenApply(response -> response.getMuseumsList().stream()
                        .map(MuseumJson::fromGrpc)
                        .toList())
                .thenCompose(this::enrichMuseumsWithCountries);
    }

    private CompletableFuture<List<MuseumJson>> enrichMuseumsWithCountries(List<MuseumJson> museums) {
        List<UUID> countryIds = museums.stream()
                .map(p -> Optional.ofNullable(p.geo())
                        .map(GeoJson::country)
                        .map(CountryJson::id)
                        .orElse(null))
                .filter(Objects::nonNull)
                .distinct()
                .toList();


        if (countryIds.isEmpty()) {
            return CompletableFuture.completedFuture(museums);
        }

        return grpcGeoClient.getCountriesByIds(countryIds)
                .thenApply(countries -> {
                    Map<UUID, CountryJson> countryMap = countries.stream()
                            .collect(Collectors.toMap(CountryJson::id, Function.identity()));

                    return museums.stream()
                            .map(museum -> {
                                CountryJson country = countryMap.get(museum.geo().country().id());
                                if (country != null) {
                                    return museum.toBuilder()
                                            .geo(museum.geo().toBuilder()
                                                    .country(country)
                                                    .build())
                                            .build();
                                }
                                return museum;
                            })
                            .toList();
                });
    }

    public MuseumJson updateMuseum(@Nonnull MuseumJson museumJson) throws BadRequestException {
        if (museumJson.id() == null) {
            throw new BadRequestException("id required for this request");
        }
        UUID countryId = Optional.ofNullable(museumJson.geo())
                .map(GeoJson::country)
                .map(CountryJson::id)
                .orElse(null);

        if (countryId != null) {
            try {
                grpcGeoClient.getById(countryId);
            } catch (StatusRuntimeException e) {
                if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                    throw new BadRequestException("The specified country does not exist");
                }
                throw e;
            }
        }

        return MuseumJson.fromGrpc(museumBlockingStub.updateMuseum(museumJson.toGrpc()));
    }

}
