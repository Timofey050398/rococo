package timofeyqa.rococo.test.kafka;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.jupiter.annotation.meta.KafkaTest;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.service.api.UserRestClient;
import timofeyqa.rococo.service.db.UsersDbClient;
import timofeyqa.rococo.service.kafka.KafkaService;
import timofeyqa.rococo.utils.RandomDataUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@KafkaTest
public class AuthKafkaTest {

  private final UserRestClient usersApiClient = new UserRestClient();
  private final UsersDbClient usersDbClient = new UsersDbClient();
  private static final String PASSWORD = Config.getInstance().defaultPassword();

  @Test
  void userShouldBeProducedToKafka() throws Exception {
    final String username = RandomDataUtils.randomUsername();

    usersApiClient.createUser(username, PASSWORD);

    UserJson userFromKafka = KafkaService.getUser(username);
    Assertions.assertEquals(
        username,
        userFromKafka.username()
    );
  }

  @Test
  void whenUserProducedToKafkaThenUserAddedToDb() throws Exception {
    final String username = RandomDataUtils.randomUsername();

    usersApiClient.createUser(username, PASSWORD);

    assertNotNull(KafkaService.getUser(username));

    UserJson userFromDb = usersDbClient.getUser(username);

    Assertions.assertNotNull(userFromDb,"user not found in db: "+username);
  }

  @Test
  void logsShouldBeProduced() throws Exception {
    final String username = RandomDataUtils.randomUsername();

    usersApiClient.createUser(username, PASSWORD);

    assertNotNull(KafkaService.takeLog(),"log after user created not found");
  }
}