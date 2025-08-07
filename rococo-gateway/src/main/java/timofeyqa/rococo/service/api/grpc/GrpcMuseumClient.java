package timofeyqa.rococo.service.api.grpc;

import com.google.common.util.concurrent.ListenableFuture;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.model.CountryJson;
import timofeyqa.rococo.model.GeoJson;
import timofeyqa.rococo.model.MuseumJson;
import timofeyqa.rococo.mappers.MuseumMapper;
import timofeyqa.rococo.mappers.UuidMapper;
import timofeyqa.rococo.model.page.RestPage;
import timofeyqa.rococo.validation.IdRequired;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static timofeyqa.rococo.mappers.PageableMapper.toGrpcPageable;
import static timofeyqa.rococo.service.utils.ToCompletableFuture.toCf;
import static timofeyqa.rococo.mappers.UuidMapper.fromUuidList;

@Service
@RequiredArgsConstructor
public class GrpcMuseumClient implements GrpcClient<MuseumJson> {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcMuseumClient.class);

    private final RococoMuseumServiceGrpc.RococoMuseumServiceFutureStub museumStub;
    private final GrpcGeoClient grpcGeoClient;



    @Override
    public @Nonnull CompletableFuture<MuseumJson> getById(UUID id){
        if (id == null){
            return CompletableFuture.completedFuture(null);
        }
        return getByMuseum(museumStub.getMuseum(UuidMapper.fromUuid(id)));
    }

    private CompletableFuture<MuseumJson> getByMuseum(ListenableFuture<Museum> museum){
      return toCf(museum)
          .thenApply(MuseumMapper::fromGrpc)
          .thenCompose(museumJson->
              grpcGeoClient.getMuseumGeo(museumJson)
                  .thenApply(geo -> museumJson.toBuilder()
                      .geo(geo)
                      .build())
          );
    }

    public CompletableFuture<RestPage<MuseumJson>> getMuseumPage(Pageable pageable, @Nullable String title) {
        return toCf(museumStub.getMuseumPage(toGrpcPageable(pageable, title)))
                .thenApply(response -> MuseumMapper.fromGrpcPage(response, pageable))
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
                        .map(MuseumMapper::fromGrpc)
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

  public CompletableFuture<MuseumJson> updateMuseum(@Nonnull @IdRequired MuseumJson museumJson) {
    Optional
        .ofNullable(museumJson.geo())
        .map(GeoJson::country)
        .ifPresent(grpcGeoClient::validateCountry);

    return getByMuseum(museumStub.updateMuseum(MuseumMapper.toGrpc(museumJson)));
  }

  public CompletableFuture<MuseumJson> create(@Nonnull @IdRequired MuseumJson museumJson) {
    Optional
        .ofNullable(museumJson.geo())
        .map(GeoJson::country)
        .ifPresent(grpcGeoClient::validateCountry);

    return getByMuseum(museumStub.addMuseum(MuseumMapper.toPostGrpc(museumJson)));
  }

}
