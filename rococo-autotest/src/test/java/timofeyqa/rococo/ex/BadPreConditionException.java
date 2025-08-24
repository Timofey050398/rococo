package timofeyqa.rococo.ex;

public class BadPreConditionException extends RuntimeException {
  public BadPreConditionException(String message) {
    super(message);
  }

  public BadPreConditionException() {
    super();
  }
}
