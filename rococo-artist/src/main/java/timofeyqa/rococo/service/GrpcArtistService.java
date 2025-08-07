package timofeyqa.rococo.service;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.data.ArtistEntity;
import timofeyqa.rococo.data.repository.ArtistRepository;
import timofeyqa.rococo.mappers.ArtistMapper;
import timofeyqa.rococo.mappers.GrpcMapper;

import java.util.UUID;

@GrpcService
public class GrpcArtistService extends RococoArtistServiceGrpc.RococoArtistServiceImplBase {
    private static final Logger LOG = LoggerFactory.getLogger(GrpcArtistService.class);

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;
    private final GrpcMapper grpcMapper;

    @Autowired
    public GrpcArtistService(
        ArtistRepository artistRepository,
        ArtistMapper artistMapper,
        GrpcMapper grpcMapper) {
        this.artistRepository = artistRepository;
        this.artistMapper = artistMapper;
        this.grpcMapper = grpcMapper;
    }

    @Override
    public void getArtist(Uuid request, StreamObserver<Artist> responseObserver) {
        LOG.info("Fetching Artist by ID: {}", request.getUuid());
        ArtistEntity entity = artistRepository.findById(UUID.fromString(request.getUuid()))
            .orElseThrow(() -> new IllegalStateException("Artist not found: " + request.getUuid()));

        responseObserver.onNext(fromEntity(entity));
        responseObserver.onCompleted();
    }

    @Override
    public void getArtistPage(timofeyqa.grpc.rococo.Pageable request, StreamObserver<PageArtistResponse> responseObserver) {
        var entityPage = request.getFilterField().isBlank()
            ? artistRepository.findAll(PageRequest.of(request.getPage(),request.getSize()))
            : artistRepository.findByNameContainingIgnoreCase(request.getFilterField(), PageRequest.of(request.getPage(),request.getSize()));


        Page<Artist> artistPage =  entityPage.map(this::fromEntity);

        responseObserver.onNext(
            PageArtistResponse.newBuilder()
                .setTotalPages(artistPage.getTotalPages())
                .setTotalElements(artistPage.getTotalElements())
                .addAllArtists(artistPage.getContent())
                .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void getArtistsByUuids(UuidList request, StreamObserver<ArtistList> responseObserver) {
        responseObserver.onNext(
            ArtistList.newBuilder()
                .addAllArtists(
                    artistRepository.findAllByIdIn(
                        request.getUuidList()
                            .stream()
                            .map(Uuid::getUuid)
                            .map(UUID::fromString)
                            .toList()
                    )
                        .stream()
                        .map(this::fromEntity)
                        .toList()
                )
                .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void updateArtist(Artist request, StreamObserver<Artist> responseObserver) {
        LOG.info("Updating Artist: {}", request.getId());
        ArtistEntity existing = artistRepository.findById(UUID.fromString(request.getId()))
            .orElseThrow(() -> new IllegalStateException("Artist not found: " + request.getId()));
        if (!request.getName().isBlank()) {
            artistRepository.findByName(request.getName())
                .ifPresent(artist -> {
                    if (!existing.getId().equals(artist.getId())) {
                        throw new IllegalStateException("Name already exists: " + request.getName());
                    }
                });
        }

        artistMapper.updateEntityFromArtist(request,existing);

        ArtistEntity updated = artistRepository.save(existing);
        responseObserver.onNext(fromEntity(updated));
        responseObserver.onCompleted();
    }

    @Override
    public void addArtist(AddArtistRequest request, StreamObserver<Artist> responseObserver) {
        ArtistEntity create = new ArtistEntity();
        if (request.getName().isBlank()) {
            throw new IllegalStateException("Name required");
        } else {
            artistRepository.findByName(request.getName())
                .ifPresent(artist -> {
                    throw new IllegalStateException("Name already exists: " + request.getName());
                });
        }
        if (request.getBiography().isBlank()) {
            throw new IllegalStateException("Biography required");
        }
        artistMapper.updateEntityFromArtist(
            grpcMapper.toArtist(request),
            create
        );

        responseObserver.onNext(fromEntity(artistRepository.save(create)));
        responseObserver.onCompleted();
    }

    private Artist fromEntity(ArtistEntity entity) {
        return Artist.newBuilder()
            .setId(entity.getId().toString())
            .setName(entity.getName())
            .setBiography(entity.getBiography())
            .setPhoto(entity.getPhoto() != null
                ? ByteString.copyFrom(entity.getPhoto())
                : ByteString.EMPTY)
            .build();
    }
}
