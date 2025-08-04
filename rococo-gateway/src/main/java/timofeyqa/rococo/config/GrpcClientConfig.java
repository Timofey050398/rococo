package timofeyqa.rococo.config;

import io.grpc.Channel;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import timofeyqa.grpc.rococo.RococoArtistServiceGrpc;
import timofeyqa.grpc.rococo.RococoGeoServiceGrpc;
import timofeyqa.grpc.rococo.RococoMuseumServiceGrpc;
import timofeyqa.grpc.rococo.RococoPaintingServiceGrpc;

@Configuration
public class GrpcClientConfig {

    @GrpcClient("painting-service")
    private Channel paintingChannel;

    @GrpcClient("artist-service")
    private Channel artistChannel;

    @GrpcClient("museum-service")
    private Channel museumChannel;

    @GrpcClient("geo-service")
    private Channel geoChannel;

    @Bean
    public RococoPaintingServiceGrpc.RococoPaintingServiceFutureStub paintingStub() {
        return RococoPaintingServiceGrpc.newFutureStub(paintingChannel);
    }

    @Bean
    public RococoArtistServiceGrpc.RococoArtistServiceFutureStub artistStub() {
        return RococoArtistServiceGrpc.newFutureStub(artistChannel);
    }

    @Bean
    public RococoArtistServiceGrpc.RococoArtistServiceBlockingStub artistBlockingStub() {
        return RococoArtistServiceGrpc.newBlockingStub(artistChannel);
    }

    @Bean
    public RococoMuseumServiceGrpc.RococoMuseumServiceFutureStub museumStub() {
        return RococoMuseumServiceGrpc.newFutureStub(museumChannel);
    }

    @Bean
    public RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub museumBlockingStub() {
        return RococoMuseumServiceGrpc.newBlockingStub(artistChannel);
    }

    @Bean
    public RococoGeoServiceGrpc.RococoGeoServiceFutureStub geoStub() {
        return RococoGeoServiceGrpc.newFutureStub(geoChannel);
    }

    @Bean
    public RococoGeoServiceGrpc.RococoGeoServiceBlockingStub geoBlockingStub() {
        return RococoGeoServiceGrpc.newBlockingStub(geoChannel);
    }
}
