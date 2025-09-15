package timofeyqa.rococo.utils.waiter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WaiterConfig {
  public static Duration WAIT_TIME = Duration.ofSeconds(5);
  public static Duration INTERVAL = Duration.ofMillis(500);
  public static NotFoundStrategy NOT_FOUND_STRATEGY = NotFoundStrategy.ERROR;

  public enum NotFoundStrategy {
    NULLABLE, ERROR
  }
}
