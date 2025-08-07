package timofeyqa.rococo.service.api.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import timofeyqa.rococo.ex.BadRequestException;
import timofeyqa.rococo.model.ResponseDto;


import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface GrpcClient<T extends ResponseDto> {
  @Nonnull CompletableFuture<T> getById(UUID id);


  default void validateChildObject(ResponseDto child) throws BadRequestException {
    if (child != null && child.id() != null) {
      try {
        getById(child.id());
      } catch (StatusRuntimeException e) {
        if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
          throw new BadRequestException("The specified object does not exist: "+child);
        }
        throw e;
      }
    }
  }
}
