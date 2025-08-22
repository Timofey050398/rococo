package timofeyqa.rococo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import timofeyqa.rococo.ex.BadRequestException;
import timofeyqa.rococo.model.MuseumJson;
import timofeyqa.rococo.model.page.RestPage;
import timofeyqa.rococo.service.api.grpc.GrpcMuseumClient;
import timofeyqa.rococo.validation.SizeLimited;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/api/museum")
@Validated
public class MuseumController {

    private final GrpcMuseumClient museumClient;

    @Autowired
    public MuseumController(GrpcMuseumClient museumClient){
        this.museumClient = museumClient;
    }


    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<MuseumJson>> getMuseum(@PathVariable("id") String id) {
        if (id.isEmpty()) {
            throw new BadRequestException("Museum ID must not be empty");
        }
        return museumClient.getById(UUID.fromString(id))
                .thenApply(ResponseEntity::ok);
    }


    @GetMapping
    public CompletableFuture<ResponseEntity<RestPage<MuseumJson>>> getAll(
        @PageableDefault  @SizeLimited Pageable pageable,
        @RequestParam(required = false) String title) {
        return museumClient.getMuseumPage(pageable, title)
                .thenApply(ResponseEntity::ok);
    }

    @PatchMapping
    public CompletableFuture<ResponseEntity<MuseumJson>> updateMuseum(@RequestBody @Valid MuseumJson museumJson) {
        return museumClient.updateMuseum(museumJson)
            .thenApply(ResponseEntity::ok);
   }

    @PostMapping
    public CompletableFuture<ResponseEntity<MuseumJson>> createMuseum(@RequestBody @Valid MuseumJson museumJson) {
        return museumClient.create(museumJson)
            .thenApply(ResponseEntity::ok);
    }
}
