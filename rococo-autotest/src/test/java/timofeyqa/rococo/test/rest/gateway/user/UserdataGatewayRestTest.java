package timofeyqa.rococo.test.rest.gateway.user;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.HttpException;
import timofeyqa.rococo.jupiter.annotation.ApiLogin;
import timofeyqa.rococo.jupiter.annotation.Token;
import timofeyqa.rococo.jupiter.annotation.User;
import timofeyqa.rococo.jupiter.annotation.meta.RestTest;
import timofeyqa.rococo.jupiter.extension.ApiLoginExtension;
import timofeyqa.rococo.model.rest.ApiError;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.service.api.UserGatewayRestClient;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static timofeyqa.rococo.jupiter.extension.ApiLoginExtension.rest;
import static timofeyqa.rococo.utils.PhotoConverter.convert;
import static timofeyqa.rococo.utils.RandomDataUtils.randomImage;
import static timofeyqa.rococo.utils.RandomDataUtils.randomName;

@DisplayName("Internal api сервиса user data")
@RestTest
class UserdataGatewayRestTest {

  private final UserGatewayRestClient userClient = new UserGatewayRestClient();
  private static final String DOMAIN = "/api/user";

  @RegisterExtension
  private static final ApiLoginExtension apiLoginExtension = rest();

  @Test
  @DisplayName("Поиск юзера с не существующим токеном возвращает 401")
  void userNotFountTest(){
    HttpException ex = assertThrows(HttpException.class, () -> userClient.getUser("abc"));

    userClient.assertError(
        401,
        ex,
        "401",
        "Unauthorized",
        DOMAIN,
        "Full authentication is required to access this resource"
    );
  }

  @Test
  @DisplayName("Попытка апдейта с не существующим токеном возвращает 401")
  @User
  void cantUpdateNotExistedUserTest(UserJson user){
    HttpException ex = assertThrows(HttpException.class, () -> userClient.updateUser(user, "abc"));

    userClient.assertError(
        401,
        ex,
        "401",
        "Unauthorized",
        DOMAIN,
        "Full authentication is required to access this resource"
    );
  }

  @Test
  @DisplayName("Апдейт username пользователя вызывает ошибку")
  @User
  @ApiLogin
  void cantUpdateUsernameTest(UserJson user, @Token String token){
    UserJson patchedUser = user.toBuilder()
        .username(randomName())
        .build();

    HttpException ex = assertThrows(HttpException.class, () -> userClient.updateUser(patchedUser, "Bearer "+token));

    userClient.assertError(
        400,
        ex,
        "400 BAD_REQUEST",
        "400 BAD_REQUEST \"User can't change username\"",
        DOMAIN,
        "User can't change username"
    );
  }

  @Test
  @DisplayName("Апдейт uuid пользователя вызывает ошибку")
  @User
  @ApiLogin
  void cantUpdateIdTest(UserJson user, @Token String token){
    UserJson patchedUser = user.toBuilder()
        .id(UUID.randomUUID())
        .build();

    HttpException ex = assertThrows(HttpException.class, () -> userClient.updateUser(patchedUser, "Bearer "+token));

    String error = new ApiError("v1.0","400","Invalid argument or state","/internal/api/user", List.of("Id can't be updated"))
        .remoteError();
    String code = "400 BAD_REQUEST";
    String message = String.format("%s \"%s\"",code,error);

    userClient.assertError(
        400,
        ex,
        code,
        message,
        DOMAIN,
        error
    );
  }

  @Test
  @DisplayName("Получение существующего пользователя")
  @User
  @ApiLogin
  void successGetUserTest(UserJson user, @Token String token){
    UserJson response = userClient.getUser("Bearer "+ token);
    assertEquals(user, response);
  }

  @Test
  @DisplayName("Корректный апдейт пользователя")
  @User
  @ApiLogin
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
  @ApiLogin
  void updateWithNullFieldsTest(UserJson user) {
    assertAll(
        () -> assertNotNull(user.id()),
        () -> assertFalse(StringUtils.isEmpty(user.username()), "username is empty"),
        () -> assertFalse(StringUtils.isEmpty(user.firstname()), "firstname is empty"),
        () -> assertFalse(StringUtils.isEmpty(user.lastname()), "lastname is empty"),
        () -> assertFalse(StringUtils.isEmpty(user.avatar()), "avatar is empty")
    );

    UserJson patchedUser = user.toBuilder()
        .firstname(null)
        .lastname(null)
        .avatar(null)
        .build();

    UserJson response = userClient.updateUser(patchedUser);

    assertAll(
        () -> assertNotNull(response.id()),
        () -> assertFalse(StringUtils.isEmpty(response.username())),
        () -> assertFalse(StringUtils.isEmpty(response.firstname())),
        () -> assertFalse(StringUtils.isEmpty(response.lastname())),
        () -> assertFalse(StringUtils.isEmpty(response.avatar()))
    );
  }
}
