package timofeyqa.rococo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timofeyqa.rococo.ex.BadRequestException;
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
        if (id.isEmpty()) {
            throw new BadRequestException("Artist Id at request path must not be empty");
        }
        return artistClient.getById(UUID.fromString(id))
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<RestPage<ArtistJson>>> getAll(
        @PageableDefault Pageable pageable,
        @RequestParam(required = false) String name) {
        return artistClient.getArtistPage(pageable, name)
                .thenApply(ResponseEntity::ok);
    }

    @PatchMapping
    public ArtistJson updateArtist(@RequestBody @Valid ArtistJson artistJson) {
        return artistClient.updateArtist(artistJson);
    }

    @PostMapping
    public ArtistJson createArtist(@RequestBody @Valid ArtistJson artistJson) {
        return artistClient.create(artistJson);
    }
}
