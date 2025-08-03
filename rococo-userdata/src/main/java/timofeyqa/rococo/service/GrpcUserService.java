package timofeyqa.rococo.service;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import timofeyqa.grpc.rococo.RococoUserdataServiceGrpc;
import timofeyqa.grpc.rococo.UserResponse;
import timofeyqa.grpc.rococo.Username;
import timofeyqa.rococo.data.UserEntity;
import timofeyqa.rococo.data.repository.UserRepository;

import java.util.Optional;

@GrpcService
public class GrpcUserService extends RococoUserdataServiceGrpc.RococoUserdataServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcUserService.class);

    private final UserRepository userRepository;

    @Autowired
    public GrpcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void getUser(Username request, StreamObserver<UserResponse> responseObserver) {
        final UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        final ByteString avatar = user.getAvatar() == null
                ? ByteString.empty()
                : ByteString.copyFrom(user.getAvatar());

        final UserResponse response = UserResponse.newBuilder()
                .setUuid(user.getId().toString())
                .setUsername(user.getUsername())
                .setFirstname(user.getFirstname())
                .setLastname(user.getLastname())
                .setAvatar(avatar)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
