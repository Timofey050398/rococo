package timofeyqa.rococo.test.rest.userdata;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.HttpException;
import timofeyqa.rococo.jupiter.annotation.User;
import timofeyqa.rococo.jupiter.annotation.meta.RestTest;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.service.api.UserRestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static timofeyqa.rococo.utils.PhotoConverter.convert;
import static timofeyqa.rococo.utils.RandomDataUtils.randomImage;
import static timofeyqa.rococo.utils.RandomDataUtils.randomName;

@DisplayName("Internal api сервиса user data")
@RestTest
class UserdataRestTest {

  private final UserRestClient userClient = new UserRestClient();
  private static final String DOMAIN = "/internal/api/user";


  @Test
  @DisplayName("Поиск не существующего юзера вызывает ошибку")
  void userNotFountTest(){
    String username = randomName();
    HttpException ex = assertThrows(HttpException.class, () -> userClient.getUser(username));

    userClient.assertError(
        404,
        ex,
        "404 NOT_FOUND",
        "Not found",
        DOMAIN,
        String.format("User with provided username: %s not found",username)
    );
  }

  @Test
  @DisplayName("Попытка апдейта не существующего пользователя вызывает ошибку")
  @User
  void cantUpdateNotExistedUserTest(UserJson user){
    String username = randomName();
    HttpException ex = assertThrows(HttpException.class, () -> userClient.updateUser(username, user));

    userClient.assertError(
        404,
        ex,
        "404 NOT_FOUND",
        "Not found",
        DOMAIN,
        String.format("User with provided username: %s not found",username)
    );
  }

  @Test
  @DisplayName("Апдейт username пользователя вызывает ошибку")
  @User
  void cantUpdateUsernameTest(UserJson user){
    UserJson patchedUser = user.toBuilder()
        .username(randomName())
        .build();

    HttpException ex = assertThrows(HttpException.class, () -> userClient.updateUser(user.username(), patchedUser));

    userClient.assertError(
        400,
        ex,
        "400",
        "Invalid argument or state",
        DOMAIN,
        "Username can't be updated"
    );
  }

  @Test
  @DisplayName("Апдейт uuid пользователя вызывает ошибку")
  @User
  void cantUpdateIdTest(UserJson user){
    UserJson patchedUser = user.toBuilder()
        .id(UUID.randomUUID())
        .build();

    HttpException ex = assertThrows(HttpException.class, () -> userClient.updateUser(user.username(), patchedUser));

    userClient.assertError(
        400,
        ex,
        "400",
        "Invalid argument or state",
        DOMAIN,
        "Id can't be updated"
    );
  }

  @Test
  @DisplayName("Получение существующего пользователя")
  @User
  void successGetUserTest(UserJson user){
    UserJson response = userClient.getUser(user.username());
    assertEquals(user, response);
  }

  @Test
  @DisplayName("Корректный апдейт пользователя")
  @User
  void successUpdateTest(UserJson user){
    final String newFirstname = randomName();
    final String newLastname = randomName();
    final String newAvatar = convert(randomImage("artists"));
    UserJson patchedUser = user.toBuilder()
        .firstname(newFirstname)
        .lastname(newLastname)
        .avatar(newAvatar)
        .build();

    UserJson response = userClient.updateUser(patchedUser);

    assertEquals(patchedUser, response);
  }

  @Test
  @DisplayName("При передаче пустых полей в апдейт, апдейт не перетирает поля")
  @User(
      firstname = "default firstname",
      lastname = "default lastname",
      avatar = "img/content/artists/dali.png"
  )
  void updateWithNullFieldsTest(UserJson user){
    assertAll(()->{
      assertNotNull(user.id());
      assertFalse(StringUtils.isEmpty(user.username()),"username is empty");
      assertFalse(StringUtils.isEmpty(user.firstname()),"firstname is empty");
      assertFalse(StringUtils.isEmpty(user.lastname()),"lastname is empty");
      assertFalse(StringUtils.isEmpty(user.avatar()),"avatar is empty");
    });

    UserJson patchedUser = user.toBuilder()
        .firstname(null)
        .lastname(null)
        .avatar(null)
        .build();

    UserJson response = userClient.updateUser(patchedUser);

    assertAll(()->{
      assertNotNull(response.id());
      assertFalse(StringUtils.isEmpty(response.username()));
      assertFalse(StringUtils.isEmpty(response.firstname()));
      assertFalse(StringUtils.isEmpty(response.lastname()));
      assertFalse(StringUtils.isEmpty(response.avatar()));
    });
  }
}
