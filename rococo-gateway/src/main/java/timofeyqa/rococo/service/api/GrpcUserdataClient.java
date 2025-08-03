package timofeyqa.rococo.service.api;

import com.google.protobuf.Empty;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import timofeyqa.grpc.rococo.RococoUserdataServiceGrpc;
import timofeyqa.grpc.rococo.UserResponse;
import timofeyqa.grpc.rococo.Username;
import timofeyqa.rococo.model.UserJson;

@Component
public class GrpcUserdataClient {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcUserdataClient.class);
    private static final Empty EMPTY = Empty.getDefaultInstance();

    @GrpcClient("grpcUserdataClient")
    private RococoUserdataServiceGrpc.RococoUserdataServiceBlockingStub rococoCurrencyServiceStub;

    public @Nonnull UserJson getUser(final String username){
        try {
            final UserResponse response = rococoCurrencyServiceStub.getUser(
                    Username.newBuilder()
                            .setUsername(username)
                            .build()
            );
            return UserJson.fromGrpc(response);
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }
}
