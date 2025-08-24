package timofeyqa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.ScreenShotTest;
import timofeyqa.rococo.jupiter.annotation.User;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.page.MainPage;
import timofeyqa.rococo.utils.RandomDataUtils;

import java.awt.image.BufferedImage;

@WebTest
@DisplayName("Тесты страницы логина")
class LoginPageTest {

  @ScreenShotTest("img/pages/login/hermitage.png")
  @DisplayName("На странице логина есть изображение эрмитажа")
  void loginPageShouldHasHermitageImage(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .checkThatPageLoaded()
        .checkHermitageImage(expected);
  }

  @Test
  @User
  @DisplayName("Если юзернейм пустой, то отображается ошибка")
  void whenUsernameIsEmptyThenErrorNotification(UserJson user){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .checkThatPageLoaded()
        .login(" ", user.password())
        .checkFormErrorMessage("Неверные учетные данные пользователя");
  }

  @Test
  @User
  @DisplayName("Если пароль пустой, то отображается ошибка")
  void whenPasswordIsEmptyThenErrorNotification(UserJson user){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .checkThatPageLoaded()
        .login(user.username(), " ")
        .checkFormErrorMessage("Неверные учетные данные пользователя");
  }

  @Test
  @User
  @DisplayName("Если юзернейм не верный, то отображается ошибка")
  void whenUsernameIsWrongThenErrorNotification(UserJson user){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .checkThatPageLoaded()
        .login(RandomDataUtils.randomUsername(), user.password())
        .checkFormErrorMessage("Неверные учетные данные пользователя");
  }

  @Test
  @User
  @DisplayName("Если пароль не верный, то отображается ошибка")
  void whenPasswordIsWrongThenErrorNotification(UserJson user){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .checkThatPageLoaded()
        .login(user.username(), user.password()+"b")
        .checkFormErrorMessage("Неверные учетные данные пользователя");
  }

  @Test
  @DisplayName("Нажатие кнопки 'Регистрация' переводит на страницу регистрации")
  void clickRegisterLinkShouldOpenRegisterPage(){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .checkThatPageLoaded()
        .clickRegister()
        .checkThatPageLoaded();
  }
}
