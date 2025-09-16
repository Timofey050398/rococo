package timofeyqa.rococo.test.kafka;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.jupiter.annotation.meta.KafkaTest;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.service.api.UserRestClient;
import timofeyqa.rococo.service.db.UsersDbClient;
import timofeyqa.rococo.service.kafka.KafkaService;
import timofeyqa.rococo.utils.RandomDataUtils;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@KafkaTest
@DisplayName("Тесты kafka")
public class AuthKafkaTest {

  private final UserRestClient usersApiClient = new UserRestClient();
  private final UsersDbClient usersDbClient = new UsersDbClient();
  private static final String PASSWORD = Config.getInstance().defaultPassword();

  @Test
  @DisplayName("При регистрации пользователь отправляется в kafka")
  void userShouldBeProducedToKafka() throws Exception {
    final String username = RandomDataUtils.randomUsername();

    usersApiClient.createUser(username, PASSWORD);

    UserJson userFromKafka = Objects.requireNonNull(KafkaService.getUser(username));
    Assertions.assertEquals(
        username,
        userFromKafka.username()
    );
  }

  @Test
  @DisplayName("После отправки в kafka пользователь сохраняется в бд userdata")
  void whenUserProducedToKafkaThenUserAddedToDb() throws Exception {
    final String username = RandomDataUtils.randomUsername();

    usersApiClient.createUser(username, PASSWORD);

    assertNotNull(KafkaService.getUser(username));

    UserJson userFromDb = usersDbClient.getUser(username);

    Assertions.assertNotNull(userFromDb,"user not found in db: "+username);
  }

  @Test
  @DisplayName("Логи отправляются в kafka")
  void logsShouldBeProduced() throws Exception {
    final String username = RandomDataUtils.randomUsername();

    usersApiClient.createUser(username, PASSWORD);

    assertNotNull(KafkaService.takeLog(),"log after user created not found");
  }
}