package timofeyqa.rococo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timofeyqa.rococo.model.ArtistJson;
import timofeyqa.rococo.model.page.RestPage;
import timofeyqa.rococo.service.api.grpc.GrpcArtistClient;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/artist")
public class ArtistController {

    private final GrpcArtistClient artistClient;

    @Autowired
    public ArtistController(GrpcArtistClient artistClient){
        this.artistClient = artistClient;
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<ArtistJson>> getArtist(@PathVariable("id") String id) {
        if (id == null || id.isEmpty()) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }
        return artistClient.getArtistById(UUID.fromString(id))
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> ResponseEntity.status(500).build());
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<RestPage<ArtistJson>>> getAll(@PageableDefault Pageable pageable) {
        return artistClient.getArtistPage(pageable)
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> ResponseEntity.status(500).build());
    }

    //TODO доделать запросы ниже
    @PatchMapping
    public ArtistJson updateArtist(@RequestBody ArtistJson ArtistJson) {
        return ArtistJson;
    }
}
