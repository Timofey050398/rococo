package timofeyqa.rococo.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.model.ArtistJson;

import java.util.Base64;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileSizeValidatorTest {

  private static Validator validator;

  @BeforeAll
  static void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void testValidSmallFile() {
    byte[] smallBytes = new byte[512 * 1024];
    String base64 = Base64.getEncoder().encodeToString(smallBytes);

    ArtistJson artist = new ArtistJson(UUID.randomUUID(), "Test", "random biography random", base64);
    Set<ConstraintViolation<ArtistJson>> violations = validator.validate(artist);

    assertTrue(violations.isEmpty(), "Файл до 1 МБ должен проходить проверку");
  }

  @Test
  void testInvalidLargeFile() {
    byte[] largeBytes = new byte[4 * 1024 * 1024];
    String base64 = Base64.getEncoder().encodeToString(largeBytes);

    base64 = "data:image/jpeg;base64," + base64;

    ArtistJson artist = new ArtistJson(UUID.randomUUID(), "Test", "random biography random", base64);
    Set<ConstraintViolation<ArtistJson>> violations = validator.validate(artist);

    assertFalse(violations.isEmpty(), "Файл больше 1 МБ должен не пройти проверку");

    violations.forEach(v -> System.out.println(v.getMessage()));
  }
}
