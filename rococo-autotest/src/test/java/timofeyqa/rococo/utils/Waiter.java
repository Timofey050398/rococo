package timofeyqa.rococo.utils;

import com.codeborne.selenide.Configuration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Waiter {

  @SuppressWarnings("BusyWait")
  public static <T> Optional<T> waitForOptional(@Nonnull Supplier<Optional<T>> supplier) {
    long deadline = System.currentTimeMillis() + Configuration.timeout;

    Optional<T> result = Optional.empty();
    while (System.currentTimeMillis() < deadline && result.isEmpty()) {
      result = supplier.get();
      if (result.isEmpty()) {
        try {
          Thread.sleep(Configuration.pollingInterval);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw new RuntimeException("Interrupted while waiting", e);
        }
      }
    }
    return result;
  }
}
