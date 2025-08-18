package timofeyqa.rococo.service.api.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import timofeyqa.rococo.ex.BadRequestException;
import timofeyqa.rococo.model.ResponseDto;


import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface GrpcClient<T extends ResponseDto> {

  CompletableFuture<T> getById(UUID id);

  default void validateChildObject(T child) throws BadRequestException {
    Optional.ofNullable(child)
        .map(ResponseDto::id)
        .ifPresent(id -> {
          try {
            getById(child.id());
          } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
              throw new BadRequestException("The specified object does not exist: "+child);
            }
            throw e;
          }
        });
  }
}
