package timofeyqa.rococo.service.api.grpc;

import com.google.common.util.concurrent.ListenableFuture;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.model.*;
import timofeyqa.rococo.mappers.PaintingMapper;
import timofeyqa.rococo.mappers.UuidMapper;
import timofeyqa.rococo.model.page.RestPage;
import timofeyqa.rococo.validation.IdRequired;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static timofeyqa.rococo.mappers.PageableMapper.toGrpcPageable;
import static timofeyqa.rococo.service.utils.ToCompletableFuture.toCf;

@Service
@RequiredArgsConstructor
public class GrpcPaintingClient implements GrpcClient<PaintingJson> {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcPaintingClient.class);

    private final RococoPaintingServiceGrpc.RococoPaintingServiceFutureStub paintingStub;
    private final RococoPaintingServiceGrpc.RococoPaintingServiceBlockingStub paintingBlockingStub;
    private final GrpcMuseumClient grpcMuseumClient;
    private final GrpcArtistClient grpcArtistClient;

    @Override
    public CompletableFuture<PaintingJson> getById(UUID paintingId) {
      return getByPainting(paintingStub.getPainting(UuidMapper.fromUuid(paintingId)));
    }

    private CompletableFuture<PaintingJson> getByPainting(ListenableFuture<Painting> painting) {
      return toCf(painting).thenCompose(paintingResp -> {
        PaintingJson json = PaintingMapper.fromGrpc(paintingResp);

        CompletableFuture<ArtistJson> artistF = grpcArtistClient.getById(json.artist().id());

        CompletableFuture<MuseumJson> museumF = grpcMuseumClient.getById(json.museum().id());

        return artistF.thenCombine(museumF, (artist, museum) ->
            json.toBuilder()
                .artist(artist)
                .museum(museum)
                .build()
        );
      });
    }

    public CompletableFuture<RestPage<PaintingJson>> getPaintingPage(Pageable pageable,@Nullable String title) {
        return pageEnrichTemplate(
                () -> paintingStub.getPaintingsPage(toGrpcPageable(pageable,title)),
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
                .setPageable(toGrpcPageable(pageable,null))
                .build();

        return pageEnrichTemplate(
                () -> paintingStub.getPaintingsByArtist(request),
                pageable
        );
    }

    public CompletableFuture<PaintingJson> updatePainting(@Nonnull @IdRequired PaintingJson paintingJson) {
        grpcMuseumClient.validateChildObject(paintingJson.museum());
        grpcArtistClient.validateChildObject(paintingJson.artist());

        return getByPainting(paintingStub.updatePainting(PaintingMapper.toGrpc(paintingJson)));
    }

  public CompletableFuture<PaintingJson> create(@Nonnull @IdRequired PaintingJson paintingJson) {
    grpcMuseumClient.validateChildObject(paintingJson.museum());
    grpcArtistClient.validateChildObject(paintingJson.artist());

    return getByPainting(paintingStub.addPainting(PaintingMapper.toPostGrpc(paintingJson)));
  }

    private CompletableFuture<RestPage<PaintingJson>> pageEnrichTemplate(Supplier<ListenableFuture<PagePainting>> supplier, Pageable pageable) {
        return toCf(supplier.get())
                .thenCompose(pagePaintingResponse -> {
                    RestPage<PaintingJson> page = PaintingMapper.fromGrpcPage(pagePaintingResponse, pageable);

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
