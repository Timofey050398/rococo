package timofeyqa.rococo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import timofeyqa.rococo.ex.BadRequestException;
import timofeyqa.rococo.model.PaintingJson;
import timofeyqa.rococo.model.page.RestPage;
import timofeyqa.rococo.service.api.grpc.GrpcPaintingClient;
import timofeyqa.rococo.validation.SizeLimited;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/painting")
@Validated
public class PaintingController {

    private final GrpcPaintingClient paintingClient;

    @Autowired
    public PaintingController(GrpcPaintingClient paintingClient){
        this.paintingClient = paintingClient;
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<PaintingJson>> getPainting(@PathVariable("id") String id) {
        if (id.isEmpty()) {
            throw new BadRequestException("Painting Id in path variable must not be empty");
        }
        return paintingClient.getById(UUID.fromString(id))
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/author/{artistId}")
    public CompletableFuture<ResponseEntity<RestPage<PaintingJson>>> getPaintingByArtist(@PageableDefault @SizeLimited Pageable pageable, @PathVariable("artistId") String artistId) {
        if (artistId.isEmpty()) {
            throw new BadRequestException("Artist Id in path variable must not be empty");
        }
        return paintingClient.getPaintingByArtist(pageable, UUID.fromString(artistId))
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<RestPage<PaintingJson>>> getAll(
        @PageableDefault  @SizeLimited Pageable pageable,
        @RequestParam(required = false) String title) {
        return paintingClient.getPaintingPage(pageable, title)
                .thenApply(ResponseEntity::ok);
    }

    @PatchMapping
    public CompletableFuture<ResponseEntity<PaintingJson>> updatePainting(@RequestBody @Valid PaintingJson paintingJson) {
        return paintingClient.updatePainting(paintingJson)
            .thenApply(ResponseEntity::ok);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<PaintingJson>> createPainting(@RequestBody @Valid PaintingJson paintingJson) {
        return paintingClient.create(paintingJson)
            .thenApply(ResponseEntity::ok);
    }
}
