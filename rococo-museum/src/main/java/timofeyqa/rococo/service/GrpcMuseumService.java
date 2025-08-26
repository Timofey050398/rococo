package timofeyqa.rococo.service;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.data.MuseumEntity;
import timofeyqa.rococo.data.repository.MuseumRepository;
import timofeyqa.rococo.mappers.MuseumMapper;
import timofeyqa.rococo.mappers.MuseumPatcher;

import java.util.UUID;

@GrpcService
public class GrpcMuseumService extends RococoMuseumServiceGrpc.RococoMuseumServiceImplBase {
    private static final Logger LOG = LoggerFactory.getLogger(GrpcMuseumService.class);

    private final MuseumRepository museumRepository;
    private final MuseumMapper museumMapper;
    private final MuseumPatcher museumPatcher;

    @Autowired
    public GrpcMuseumService(MuseumRepository museumRepository, MuseumMapper museumMapper, MuseumPatcher museumPatcher) {
        this.museumRepository = museumRepository;
        this.museumMapper = museumMapper;
        this.museumPatcher = museumPatcher;
    }

    @Override
    public void getMuseum(Uuid request, StreamObserver<Museum> responseObserver) {
        LOG.info("Fetching Museum by ID: {}", request.getUuid());
        MuseumEntity entity = museumRepository.findById(UUID.fromString(request.getUuid()))
            .orElseThrow(() -> new IllegalStateException("Museum not found: " + request.getUuid()));

        responseObserver.onNext(fromEntity(entity));
        responseObserver.onCompleted();
    }

    @Override
    public void getMuseumPage(timofeyqa.grpc.rococo.Pageable request, StreamObserver<PageMuseum> responseObserver) {
        PageRequest pageRequest = PageRequest.of(
            request.getPage(),
            request.getSize(),
            Sort.by("title").ascending()
        );
        Page<MuseumEntity> enitityPage = request.getFilterField().isBlank()
            ? museumRepository.findAll(pageRequest)
            : museumRepository.findByTitleContainingIgnoreCase(request.getFilterField(), pageRequest);

        Page<Museum> museumPage = enitityPage
            .map(this::fromEntity);

        responseObserver.onNext(
            PageMuseum.newBuilder()
                .setTotalPages(museumPage.getTotalPages())
                .setTotalElements(museumPage.getTotalElements())
                .addAllMuseums(museumPage.getContent())
                .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void getMuseumsByUuids(UuidList request, StreamObserver<MuseumList> responseObserver) {
        responseObserver.onNext(
            MuseumList.newBuilder()
                .addAllMuseums(
                    museumRepository.findAllByIdIn(
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
    public void updateMuseum(Museum request, StreamObserver<Museum> responseObserver) {
        LOG.info("Updating Museum: {}", request.getId());
        MuseumEntity existing = museumRepository.findById(UUID.fromString(request.getId()))
            .orElseThrow(() -> new IllegalStateException("Museum not found: " + request.getId()));
        if (!request.getTitle().isBlank()) {
            museumRepository.findByTitle(request.getTitle())
                .ifPresent(museum -> {
                    if (!museum.getId().equals(existing.getId())) {
                        throw new IllegalStateException("Title already exists: " + request.getTitle());
                    }
                });
        }
        museumPatcher.patch(request,existing,museumMapper);

        MuseumEntity updated = museumRepository.save(existing);
        responseObserver.onNext(fromEntity(updated));
        responseObserver.onCompleted();
    }

    @Override
    public void addMuseum(AddMuseumRequest request, StreamObserver<Museum> responseObserver) {
        if (request.getTitle().isBlank()) {
            throw new IllegalStateException("Title required");
        } else {
            museumRepository.findByTitle(request.getTitle())
                .ifPresent(museum -> {
                    throw new IllegalStateException("Title already exists: " + request.getTitle());
                });
        }
        if (request.getCountryId().isBlank()) {
            throw new IllegalStateException("Country required");
        }

        MuseumEntity create = museumMapper.addEntityFromMuseum(museumMapper.toMuseum(request));

        responseObserver.onNext(fromEntity(museumRepository.save(create)));
        responseObserver.onCompleted();
    }

    private Museum fromEntity(MuseumEntity entity) {
        Museum.Builder builder = Museum.newBuilder()
            .setId(entity.getId().toString())
            .setTitle(entity.getTitle())
            .setPhoto(entity.getPhoto() != null
                ? ByteString.copyFrom(entity.getPhoto())
                : ByteString.EMPTY)
            .setCountryId(entity.getCountryId().toString());

        if(entity.getDescription() != null) {
            builder.setDescription(entity.getDescription());
        }
        if(entity.getCity() != null) {
            builder.setCity(entity.getCity());
        }
        return builder.build();
    }
}
