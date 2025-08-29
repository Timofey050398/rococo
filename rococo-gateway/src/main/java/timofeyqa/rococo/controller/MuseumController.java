package timofeyqa.rococo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    public CompletableFuture<MuseumJson> getMuseum(@PathVariable("id") String id) {
        return museumClient.getById(UUID.fromString(id));
    }


    @GetMapping
    public CompletableFuture<RestPage<MuseumJson>> getAll(
        @PageableDefault  @SizeLimited Pageable pageable,
        @RequestParam(required = false) String title) {
        return museumClient.getMuseumPage(pageable, title);
    }

    @PatchMapping
    public CompletableFuture<MuseumJson> updateMuseum(@RequestBody @Valid MuseumJson museumJson) {
        return museumClient.updateMuseum(museumJson);
   }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<MuseumJson> createMuseum(@RequestBody @Valid MuseumJson museumJson) {
        return museumClient.create(museumJson);
    }
}
