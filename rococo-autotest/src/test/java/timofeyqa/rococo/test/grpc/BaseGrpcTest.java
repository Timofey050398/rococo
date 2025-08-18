package timofeyqa.rococo.test.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import timofeyqa.grpc.rococo.RococoArtistServiceGrpc;
import timofeyqa.grpc.rococo.RococoGeoServiceGrpc;
import timofeyqa.grpc.rococo.RococoMuseumServiceGrpc;
import timofeyqa.grpc.rococo.RococoPaintingServiceGrpc;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.jupiter.annotation.meta.GrpcTest;
import timofeyqa.rococo.utils.GrpcConsoleInterceptor;

@GrpcTest
public abstract class BaseGrpcTest {
    protected static final Config CFG = Config.getInstance();

    protected static final Channel museumChannel = channel(CFG.museumGrpcUrl(), CFG.museumGrpcPort());

    protected static final Channel artistChannel = channel(CFG.artistGrpcUrl(), CFG.artistGrpcPort());

    protected static final Channel geoChannel = channel(CFG.geoGrpcUrl(), CFG.geoGrpcPort());

    protected static final Channel paintingChannel = channel(CFG.paintingGrpcUrl(), CFG.paintingGrpcPort());


    protected static RococoArtistServiceGrpc.RococoArtistServiceBlockingStub artistStub
            = RococoArtistServiceGrpc.newBlockingStub(artistChannel);
    protected static RococoGeoServiceGrpc.RococoGeoServiceBlockingStub geoStub
        = RococoGeoServiceGrpc.newBlockingStub(geoChannel);
    protected static RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub museumStub
        = RococoMuseumServiceGrpc.newBlockingStub(museumChannel);
    protected static RococoPaintingServiceGrpc.RococoPaintingServiceBlockingStub paintingStub
        = RococoPaintingServiceGrpc.newBlockingStub(paintingChannel);

    private static Channel channel(String url,int port){
        return ManagedChannelBuilder
            .forAddress(url, port)
            .intercept(new GrpcConsoleInterceptor())
            .usePlaintext()
            .build();
    }
}
