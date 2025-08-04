package timofeyqa.rococo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timofeyqa.rococo.model.PaintingJson;
import timofeyqa.rococo.model.page.RestPage;
import timofeyqa.rococo.service.api.grpc.GrpcPaintingClient;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/painting")
public class PaintingController {

    private final GrpcPaintingClient paintingClient;

    @Autowired
    public PaintingController(GrpcPaintingClient paintingClient){
        this.paintingClient = paintingClient;
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<PaintingJson>> getPainting(@PathVariable("id") String id) {
        if (id == null || id.isEmpty()) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }
        return paintingClient.getPaintingById(UUID.fromString(id))
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> ResponseEntity.status(500).build());
    }

    @GetMapping("/author/{artistId}")
    public CompletableFuture<ResponseEntity<RestPage<PaintingJson>>> getPaintingByArtist(@PageableDefault Pageable pageable,@PathVariable("artistId") String artistId) {
        if (artistId == null || artistId.isEmpty()) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }
        return paintingClient.getPaintingByArtist(pageable, UUID.fromString(artistId))
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> ResponseEntity.status(500).build());
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<RestPage<PaintingJson>>> getAll(@PageableDefault Pageable pageable) {
        return paintingClient.getPaintingPage(pageable)
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> ResponseEntity.status(500).build());
    }

    @PatchMapping
    public PaintingJson updatePainting(@RequestBody PaintingJson paintingJson) {
        return paintingJson;
    }
}
