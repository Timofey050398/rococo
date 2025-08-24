package timofeyqa.rococo.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import timofeyqa.grpc.rococo.*;
import timofeyqa.rococo.data.CountryEntity;
import timofeyqa.rococo.data.repository.CountryRepository;

import java.util.List;
import java.util.UUID;

@GrpcService
public class GrpcGeoService extends RococoGeoServiceGrpc.RococoGeoServiceImplBase {
    private static final Logger LOG = LoggerFactory.getLogger(GrpcGeoService.class);

    private final CountryRepository countryRepository;

    @Autowired
    public GrpcGeoService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public void getGeo(Uuid request, StreamObserver<GeoResponse> responseObserver) {
        LOG.info("Fetching country by ID: {}", request.getUuid());
        final CountryEntity country = countryRepository.findById(UUID.fromString(request.getUuid()))
                .orElseThrow(()->new IllegalStateException("Country not found "+ request.getUuid()));

        responseObserver.onNext(fromEntity(country));
        responseObserver.onCompleted();
    }

    @Override
    public void getAll(Empty request, StreamObserver<GeoListResponse> responseObserver) {
        LOG.info("Get all countries");
        final List<CountryEntity> country = countryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        GeoListResponse response = GeoListResponse
                .newBuilder()
                .addAllGeo(
                country.stream()
                        .map(this::fromEntity)
                        .toList()
        ).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getGeosByUuids(UuidList request, StreamObserver<GeoListResponse> responseObserver) {
        List<UUID> uuids = request.getUuidList()
                .stream()
                .map(uuid -> UUID.fromString(uuid.getUuid()))
                .toList();

        LOG.info("Fetching country by ID's: {}", uuids);

        List<CountryEntity> country = countryRepository.findAllByIdIn(uuids);

        GeoListResponse response = GeoListResponse
                .newBuilder()
                .addAllGeo(
                        country.stream()
                                .map(this::fromEntity)
                                .toList()
                ).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private GeoResponse fromEntity(CountryEntity entity) {
        return GeoResponse.newBuilder()
                .setId(entity.getId().toString())
                .setName(entity.getName())
                .build();
    }
}
