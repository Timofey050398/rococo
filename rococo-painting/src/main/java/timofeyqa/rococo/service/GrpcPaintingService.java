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
import timofeyqa.rococo.data.PaintingEntity;
import timofeyqa.rococo.data.repository.PaintingRepository;
import timofeyqa.rococo.mappers.PaintingMapper;
import timofeyqa.rococo.mappers.PaintingPatcher;

import java.util.UUID;

@GrpcService
public class GrpcPaintingService extends RococoPaintingServiceGrpc.RococoPaintingServiceImplBase {
    private static final Logger LOG = LoggerFactory.getLogger(GrpcPaintingService.class);

    private final PaintingRepository paintingRepository;
    private final PaintingMapper paintingMapper;
    private final PaintingPatcher patcher;

    @Autowired
    public GrpcPaintingService(PaintingRepository paintingRepository, PaintingMapper paintingMapper, PaintingPatcher patcher) {
        this.paintingRepository = paintingRepository;
        this.paintingMapper = paintingMapper;
        this.patcher = patcher;
    }

    @Override
    public void getPainting(Uuid request, StreamObserver<Painting> responseObserver) {
        LOG.info("Fetching Painting by ID: {}", request.getUuid());
        PaintingEntity entity = paintingRepository.findById(UUID.fromString(request.getUuid()))
            .orElseThrow(() -> new IllegalStateException("Painting not found: " + request.getUuid()));

        responseObserver.onNext(fromEntity(entity));
        responseObserver.onCompleted();
    }

    @Override
    public void getPaintingsPage(Pageable request, StreamObserver<PagePainting> responseObserver) {
        var entityPage = request.getFilterField().isBlank()
            ? paintingRepository.findAll(PageRequest.of(request.getPage(),request.getSize()))
            : paintingRepository.findByTitleContainingIgnoreCase(request.getFilterField(), PageRequest.of(request.getPage(),request.getSize()));

        Page<Painting> paintingPage =  entityPage.map(this::fromEntity);

        responseObserver.onNext(
            PagePainting.newBuilder()
                .setTotalPages(paintingPage.getTotalPages())
                .setTotalElements(paintingPage.getTotalElements())
                .addAllPaintings(paintingPage.getContent())
                .build()
        );
        responseObserver.onCompleted();
    }


    @Override
    public void getPaintingsByArtist(GetPaintingsByArtistRequest request, StreamObserver<PagePainting> responseObserver) {
        final var uuid = UUID.fromString(request.getUuid().getUuid());
        var page = request.getPageable();

        Page<Painting> paintingPage =  paintingRepository
            .findByArtistId(uuid,PageRequest.of(page.getPage(),page.getSize()))
            .map(this::fromEntity);

        responseObserver.onNext(
            PagePainting.newBuilder()
                .setTotalPages(paintingPage.getTotalPages())
                .setTotalElements(paintingPage.getTotalElements())
                .addAllPaintings(paintingPage.getContent())
                .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void updatePainting(Painting request, StreamObserver<Painting> responseObserver) {
        LOG.info("Updating Painting: {}", request.getId());
        PaintingEntity existing = paintingRepository.findById(UUID.fromString(request.getId()))
            .orElseThrow(() -> new IllegalStateException("painting not found: " + request.getId()));

        patcher.patch(request,existing,paintingMapper);

        PaintingEntity updated = paintingRepository.save(existing);
        responseObserver.onNext(fromEntity(updated));
        responseObserver.onCompleted();
    }

    @Override
    public void addPainting(AddPaintingRequest request, StreamObserver<Painting> responseObserver) {
        if(request.getTitle().isBlank()){
            throw new IllegalStateException("Title is required");
        }
        if (request.getArtistId().isBlank()) {
            throw new IllegalStateException("Artist is required");
        }

        PaintingEntity create = paintingMapper.addEntityFromPainting(request);

        PaintingEntity saved = paintingRepository.save(create);
        responseObserver.onNext(fromEntity(saved));
        responseObserver.onCompleted();
    }

    private Painting fromEntity(PaintingEntity entity) {
        Painting.Builder builder = Painting.newBuilder()
            .setId(entity.getId().toString())
            .setTitle(entity.getTitle())
            .setContent(entity.getContent() != null
                ? ByteString.copyFrom(entity.getContent())
                : ByteString.EMPTY)
            .setArtistId(entity.getArtistId().toString());

        if(entity.getDescription() != null) {
            builder.setDescription(entity.getDescription());
        }
        if(entity.getMuseumId() != null) {
            builder.setMuseumId(entity.getMuseumId().toString());
        }
        return builder.build();
    }
}
