package timofeyqa.rococo.service.api.grpc;

import com.google.common.util.concurrent.ListenableFuture;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.model.ArtistJson;
import timofeyqa.rococo.model.MuseumJson;
import timofeyqa.rococo.model.PaintingJson;
import timofeyqa.rococo.model.page.RestPage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static timofeyqa.rococo.service.utils.Paginate.toGrpcPageable;
import static timofeyqa.rococo.service.utils.ToCompletableFuture.toCf;

@Service
@RequiredArgsConstructor
public class GrpcPaintingClient {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcPaintingClient.class);

    private final RococoPaintingServiceGrpc.RococoPaintingServiceFutureStub paintingStub;
    private final GrpcMuseumClient grpcMuseumClient;
    private final GrpcArtistClient grpcArtistClient;

    public CompletableFuture<PaintingJson> getPaintingById(UUID paintingId) {
        return toCf(paintingStub.getPainting(
                Uuid.newBuilder().setUuid(paintingId.toString()).build()
        )).thenCompose(paintingResp -> {
            PaintingJson json = PaintingJson.fromGrpc(paintingResp);

            CompletableFuture<ArtistJson> artistF = grpcArtistClient.getArtistById(json.artist().id());

            CompletableFuture<MuseumJson> museumF = grpcMuseumClient.getMuseumById(json.museum().id());

            return artistF.thenCombine(museumF, (artist, museum) ->
                    json.toBuilder()
                            .artist(artist)
                            .museum(museum)
                            .build()
            );
        });
    }

    public CompletableFuture<RestPage<PaintingJson>> getPaintingPage(Pageable pageable) {
        return pageEnrichTemplate(
                () -> paintingStub.getPaintingsPage(toGrpcPageable(pageable)),
                pageable
        );
    }

    public CompletableFuture<RestPage<PaintingJson>> getPaintingByArtist(Pageable pageable,@Nonnull UUID artistId) {
        var request = GetPaintingsByArtistRequest.newBuilder()
                .setUuid(
                        Uuid.newBuilder()
                                .setUuid(artistId.toString())
                                .build()
                )
                .setPageable(toGrpcPageable(pageable))
                .build();

        return pageEnrichTemplate(
                () -> paintingStub.getPaintingsByArtist(request),
                pageable
        );
    }


    private CompletableFuture<RestPage<PaintingJson>> pageEnrichTemplate(Supplier<ListenableFuture<PagePainting>> supplier, Pageable pageable) {
        return toCf(supplier.get())
                .thenCompose(pagePaintingResponse -> {
                    RestPage<PaintingJson> page = PaintingJson.fromGrpcPage(pagePaintingResponse, pageable);

                    List<UUID> artistIds = page.getContent().stream()
                            .map(p -> Optional.ofNullable(p.artist()).map(ArtistJson::id).orElse(null))
                            .filter(Objects::nonNull)
                            .distinct()
                            .toList();


                    List<UUID> museumIds = page.getContent().stream()
                            .map(p -> Optional.ofNullable(p.museum()).map(MuseumJson::id).orElse(null))
                            .filter(Objects::nonNull)
                            .distinct()
                            .toList();



                    CompletableFuture<List<ArtistJson>> artistFList = grpcArtistClient.getArtistsByIds(artistIds);

                    CompletableFuture<List<MuseumJson>> museumFList = grpcMuseumClient.getMuseumsByIds(museumIds);

                    return artistFList.thenCombine(museumFList, (artists, museums) -> {
                        Page<PaintingJson> enriched = page.map(painting -> {
                            ArtistJson artist = artists.stream()
                                    .filter(a -> a.id().equals(Optional.ofNullable(painting.artist())
                                            .map(ArtistJson::id)
                                            .orElse(null))
                                    )
                                    .findFirst()
                                    .orElse(null);

                            MuseumJson museum = museums.stream()
                                    .filter(m -> m.id().equals(Optional.ofNullable(painting.museum())
                                            .map(MuseumJson::id)
                                            .orElse(null))
                                    )
                                    .findFirst()
                                    .orElse(null);

                            return painting.toBuilder()
                                    .artist(artist)
                                    .museum(museum)
                                    .build();
                        });
                        return new RestPage<>(enriched.getContent(), pageable, page.getTotalElements());
                    });
                });
    }

}
