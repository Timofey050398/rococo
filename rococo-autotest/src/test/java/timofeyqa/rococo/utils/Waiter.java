package timofeyqa.rococo.utils;

import com.codeborne.selenide.Configuration;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

public class Waiter {

  public static <T> Optional<T> waitForOptional(Supplier<Optional<T>> supplier) {
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
