package timofeyqa.rococo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timofeyqa.rococo.model.MuseumJson;
import timofeyqa.rococo.model.page.RestPage;
import timofeyqa.rococo.service.api.grpc.GrpcMuseumClient;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/api/museum")
public class MuseumController {

    private final GrpcMuseumClient museumClient;

    @Autowired
    public MuseumController(GrpcMuseumClient museumClient){
        this.museumClient = museumClient;
    }


    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<MuseumJson>> getMuseum(@PathVariable("id") String id) {
        if (id == null || id.isEmpty()) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }
        return museumClient.getMuseumById(UUID.fromString(id))
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> ResponseEntity.status(500).build());
    }


    @GetMapping
    public CompletableFuture<ResponseEntity<RestPage<MuseumJson>>> getAll(@PageableDefault Pageable pageable) {
        return museumClient.getMuseumPage(pageable)
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> ResponseEntity.status(500).build());
    }

    //TODO доделать запросы ниже
    @PatchMapping
    public MuseumJson updateMuseum(@RequestBody MuseumJson museumJson) {
        return museumJson;
    }
}
