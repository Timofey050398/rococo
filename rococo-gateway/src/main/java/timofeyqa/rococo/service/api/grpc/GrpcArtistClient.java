package timofeyqa.rococo.service.api.grpc;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.model.ArtistJson;
import timofeyqa.rococo.model.page.RestPage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static timofeyqa.rococo.service.utils.Paginate.toGrpcPageable;
import static timofeyqa.rococo.service.utils.ToCompletableFuture.toCf;
import static timofeyqa.rococo.service.utils.UuidUtil.fromUuidList;

@Service
@RequiredArgsConstructor
public class GrpcArtistClient {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcArtistClient.class);

    private final RococoArtistServiceGrpc.RococoArtistServiceFutureStub artistStub;
    private final RococoArtistServiceGrpc.RococoArtistServiceBlockingStub artistBlockingStub;

    public @Nonnull CompletableFuture<ArtistJson> getArtistById(UUID id){
        if(id == null){
            return CompletableFuture.completedFuture(null);
        }
        return toCf(
                artistStub.getArtist(
                        Uuid.newBuilder()
                                .setUuid(id.toString())
                                .build()
                )
        ).thenApply(ArtistJson::fromGrpc);
    }

    public CompletableFuture<RestPage<ArtistJson>> getArtistPage(Pageable pageable) {
        return toCf(artistStub.getArtistPage(toGrpcPageable(pageable)))
                .thenApply(response -> ArtistJson.fromGrpcPage(response, pageable));
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
                        .map(ArtistJson::fromGrpc)
                        .toList()
                );
    }

    public ArtistJson updateArtist(ArtistJson artistJson) throws BadRequestException {
        if (artistJson.id() == null) {
            throw new BadRequestException("id required for this request");
        }
        return ArtistJson.fromGrpc(artistBlockingStub.updateArtist(artistJson.toGrpc()));
    }
}
