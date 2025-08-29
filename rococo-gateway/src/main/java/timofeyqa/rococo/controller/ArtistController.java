package timofeyqa.rococo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import timofeyqa.rococo.model.ArtistJson;
import timofeyqa.rococo.model.page.RestPage;
import timofeyqa.rococo.service.api.grpc.GrpcArtistClient;
import timofeyqa.rococo.validation.SizeLimited;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/artist")
@Validated
public class ArtistController {

    private final GrpcArtistClient artistClient;

    @Autowired
    public ArtistController(GrpcArtistClient artistClient){
        this.artistClient = artistClient;
    }

    @GetMapping("/{id}")
    public CompletableFuture<ArtistJson> getArtist(@PathVariable("id") String id) {
        return artistClient.getById(UUID.fromString(id));
    }

    @GetMapping
    public CompletableFuture<RestPage<ArtistJson>> getAll(
        @PageableDefault  @SizeLimited(max = 18) Pageable pageable,
        @RequestParam(required = false) String name) {
        return artistClient.getArtistPage(pageable, name);
    }

    @PatchMapping
    public ArtistJson updateArtist(@RequestBody @Valid ArtistJson artistJson) {
        return artistClient.updateArtist(artistJson);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArtistJson createArtist(@RequestBody @Valid ArtistJson artistJson) {
        return artistClient.create(artistJson);
    }
}
