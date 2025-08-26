package timofeyqa.rococo.service.handler;

import io.grpc.Status;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionSystemException;

@GrpcAdvice
public class GrpcGlobalExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcGlobalExceptionHandler.class);

  @GrpcExceptionHandler(IllegalArgumentException.class)
  public Status handleInvalidArgument(IllegalArgumentException e) {
    LOG.warn("Invalid argument caught: {} ({})", e.getMessage(), e, e);

    if (e.getCause() != null) {
      LOG.warn("Cause: {}", e.getCause().toString(), e.getCause());
    }
    return Status.INVALID_ARGUMENT.withDescription(e.getMessage()).withCause(e);
  }

  @GrpcExceptionHandler(IllegalStateException.class)
  public Status handleNotFound(IllegalStateException e) {
    LOG.warn("Not found: {} ()", e.getMessage());
    return Status.NOT_FOUND.withDescription(e.getMessage()).withCause(e);
  }

  @GrpcExceptionHandler(Exception.class)
  public Status handleGenericException(Exception e) {
    LOG.error("Unexpected error", e);
    return Status.INTERNAL.withDescription("Internal error: " + e.getMessage()).withCause(e);
  }

  @GrpcExceptionHandler(ConstraintViolationException.class)
  public Status handleValidationException(ConstraintViolationException e) {
    LOG.warn("Validation failed: {}", e.getMessage());

    // Соберём все сообщения о нарушениях в одну строку
    StringBuilder description = new StringBuilder("Validation errors: ");
    for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
      description.append(violation.getPropertyPath())
          .append(" ")
          .append(violation.getMessage())
          .append("; ");
    }

    return Status.INVALID_ARGUMENT
        .withDescription(description.toString())
        .withCause(e);
  }


  @GrpcExceptionHandler(DataIntegrityViolationException.class)
  public Status handleKeyFound(DataIntegrityViolationException e) {
    LOG.warn("DataIntegrityViolation: {}", e.getMessage());

    Throwable cause = e.getCause();
    if (cause instanceof org.hibernate.exception.ConstraintViolationException cve) {
      Throwable sqlCause = cve.getCause();
      if (sqlCause instanceof java.sql.SQLIntegrityConstraintViolationException sqlEx) {
        String sqlExMessage = sqlEx.getMessage();
        if (sqlExMessage != null) {
          if (sqlExMessage.contains("fk_artist_id")) {
            return Status.NOT_FOUND
                .withDescription("Artist with provided Id not found")
                .withCause(cve);
          }
          if (sqlExMessage.contains("fk_museum_id")) {
            return Status.NOT_FOUND
                .withDescription("Museum with provided Id not found")
                .withCause(cve);
          }
        }
      }
    }

    return Status.INTERNAL
        .withDescription("Unexpected database error")
        .withCause(e);
  }


  @GrpcExceptionHandler(TransactionSystemException.class)
  public Status handleTransactionSystemException(TransactionSystemException e) {
    if (e.getRootCause() instanceof ConstraintViolationException violation) {
      return handleValidationException(violation);
    }
    return Status.INTERNAL.withDescription(e.getMessage()).withCause(e);
  }
}
