package timofeyqa.rococo.service.api.grpc;

import com.google.common.util.concurrent.ListenableFuture;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.model.*;
import timofeyqa.rococo.mappers.PaintingMapper;
import timofeyqa.rococo.mappers.UuidMapper;
import timofeyqa.rococo.model.page.RestPage;
import timofeyqa.rococo.validation.IdRequired;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static timofeyqa.rococo.mappers.PageableMapper.toGrpcPageable;
import static timofeyqa.rococo.service.utils.ToCompletableFuture.toCf;
import static timofeyqa.rococo.service.utils.UuidListExtractor.extractUuids;

@Service
@Validated
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class GrpcPaintingClient implements GrpcClient<PaintingJson> {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcPaintingClient.class);

    private final RococoPaintingServiceGrpc.RococoPaintingServiceFutureStub paintingStub;
    private final GrpcMuseumClient grpcMuseumClient;
    private final GrpcArtistClient grpcArtistClient;

    @Override
    public CompletableFuture<PaintingJson> getById(UUID paintingId) {
      return getByPainting(paintingStub.getPainting(UuidMapper.fromUuid(paintingId)));
    }

    private CompletableFuture<PaintingJson> getByPainting(ListenableFuture<Painting> painting) {
      return toCf(painting).thenCompose(paintingResp -> {
        PaintingJson json = PaintingMapper.fromGrpc(paintingResp);

        CompletableFuture<ArtistJson> artistF = Optional.ofNullable(json.artist())
            .map(ArtistJson::id)
            .map(grpcArtistClient::getById)
            .orElse(CompletableFuture.completedFuture(null));

        CompletableFuture<MuseumJson> museumF = Optional.ofNullable(json.museum())
            .map(MuseumJson::id)
            .map(grpcMuseumClient::getById)
            .orElse(CompletableFuture.completedFuture(null));

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

    public CompletableFuture<RestPage<PaintingJson>> getPaintingByArtist(Pageable pageable, UUID artistId) {
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

    public CompletableFuture<PaintingJson> updatePainting(@IdRequired PaintingJson paintingJson) {
        grpcMuseumClient.validateChildObject(paintingJson.museum());
        grpcArtistClient.validateChildObject(paintingJson.artist());

        return getByPainting(paintingStub.updatePainting(PaintingMapper.toGrpc(paintingJson)));
    }

  public CompletableFuture<PaintingJson> create(PaintingJson paintingJson) {
    grpcMuseumClient.validateChildObject(paintingJson.museum());
    grpcArtistClient.validateChildObject(paintingJson.artist());

    return getByPainting(paintingStub.addPainting(PaintingMapper.toPostGrpc(paintingJson)));
  }

    private CompletableFuture<RestPage<PaintingJson>> pageEnrichTemplate(Supplier<ListenableFuture<PagePainting>> supplier, Pageable pageable) {
        return toCf(supplier.get())
                .thenCompose(pagePaintingResponse -> {
                    RestPage<PaintingJson> page = PaintingMapper.fromGrpcPage(pagePaintingResponse, pageable);

                    List<UUID> artistIds = extractUuids(
                        page.getContent(),
                        PaintingJson::artist
                    );

                    List<UUID> museumIds = extractUuids(
                        page.getContent(),
                        PaintingJson::museum
                    );

                    CompletableFuture<List<ArtistJson>> artistFList = grpcArtistClient.getArtistsByIds(artistIds);

                    CompletableFuture<List<MuseumJson>> museumFList = grpcMuseumClient.getMuseumsByIds(museumIds);

                    return artistFList.thenCombine(museumFList, (artists, museums) -> {
                        Page<PaintingJson> enriched = page.map(painting -> {
                            ArtistJson artist = findCorrect(artists,painting.artist());
                            MuseumJson museum = findCorrect(museums,painting.museum());

                            return painting.toBuilder()
                                    .artist(artist)
                                    .museum(museum)
                                    .build();
                        });
                        return new RestPage<>(enriched.getContent(), pageable, page.getTotalElements());
                    });
                });
    }

    private <T extends ResponseDto> T findCorrect(List<T> list, @Nullable T expected){
      UUID expectedId = Optional.ofNullable(expected)
          .map(ResponseDto::id)
          .orElse(null);

      if (expectedId == null) {
        return null;
      }

      return list.stream()
          .filter(resp -> Objects.equals(resp.id(), expectedId))
          .findFirst()
          .orElse(null);
    }

}
