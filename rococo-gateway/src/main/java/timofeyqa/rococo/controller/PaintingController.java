package timofeyqa.rococo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    public CompletableFuture<PaintingJson> getPainting(@PathVariable("id") String id) {
        return paintingClient.getById(UUID.fromString(id));
    }

    @GetMapping("/author/{artistId}")
    public CompletableFuture<RestPage<PaintingJson>> getPaintingByArtist(@PageableDefault @SizeLimited Pageable pageable, @PathVariable("artistId") String artistId) {
        return paintingClient.getPaintingByArtist(pageable, UUID.fromString(artistId));
    }

    @GetMapping
    public CompletableFuture<RestPage<PaintingJson>> getAll(
        @PageableDefault  @SizeLimited Pageable pageable,
        @RequestParam(required = false) String title) {
        return paintingClient.getPaintingPage(pageable, title);
    }

    @PatchMapping
    public CompletableFuture<PaintingJson> updatePainting(@RequestBody @Valid PaintingJson paintingJson) {
        return paintingClient.updatePainting(paintingJson);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<PaintingJson> createPainting(@RequestBody @Valid PaintingJson paintingJson) {
        return paintingClient.create(paintingJson);
    }
}
