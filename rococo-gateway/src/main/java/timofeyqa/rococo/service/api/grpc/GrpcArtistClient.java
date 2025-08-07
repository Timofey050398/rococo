package timofeyqa.rococo.service.api.grpc;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.model.ArtistJson;
import timofeyqa.rococo.mappers.ArtistMapper;
import timofeyqa.rococo.model.page.RestPage;
import timofeyqa.rococo.validation.IdRequired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static timofeyqa.rococo.mappers.PageableMapper.toGrpcPageable;
import static timofeyqa.rococo.service.utils.ToCompletableFuture.toCf;
import static timofeyqa.rococo.mappers.UuidMapper.fromUuidList;

@Service
@RequiredArgsConstructor
public class GrpcArtistClient implements GrpcClient<ArtistJson> {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcArtistClient.class);

    private final RococoArtistServiceGrpc.RococoArtistServiceFutureStub artistStub;
    private final RococoArtistServiceGrpc.RococoArtistServiceBlockingStub artistBlockingStub;

    @Override
    public @Nonnull CompletableFuture<ArtistJson> getById(UUID id){
        if(id == null){
            return CompletableFuture.completedFuture(null);
        }
        return toCf(
                artistStub.getArtist(
                        Uuid.newBuilder()
                                .setUuid(id.toString())
                                .build()
                )
        ).thenApply(ArtistMapper::fromGrpc);
    }

    public CompletableFuture<RestPage<ArtistJson>> getArtistPage(Pageable pageable,@Nullable String name) {
        return toCf(artistStub.getArtistPage(toGrpcPageable(pageable, name)))
                .thenApply(response -> ArtistMapper.fromGrpcPage(response, pageable));
    }

    @Nonnull
    CompletableFuture<List<ArtistJson>> getArtistsByIds(@Nonnull List<UUID> ids){
        if(ids.isEmpty()){
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        return toCf(
                artistStub.getArtistsByUuids(fromUuidList(ids))
        )
                .thenApply(artistList -> artistList
                        .getArtistsList()
                        .stream()
                        .map(ArtistMapper::fromGrpc)
                        .toList()
                );
    }

    public ArtistJson updateArtist(@Nonnull @IdRequired ArtistJson artistJson) {
        return ArtistMapper.fromGrpc(artistBlockingStub.updateArtist(ArtistMapper.toGrpc(artistJson)));
    }

    public ArtistJson create(@Nonnull @IdRequired ArtistJson artistJson) {
        return ArtistMapper.fromGrpc(artistBlockingStub.addArtist(ArtistMapper.toPostGrpc(artistJson)));
    }
}
