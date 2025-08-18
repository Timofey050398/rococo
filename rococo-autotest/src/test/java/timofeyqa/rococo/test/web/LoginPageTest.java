package timofeyqa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.ScreenShotTest;
import timofeyqa.rococo.jupiter.annotation.User;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.page.MainPage;
import timofeyqa.rococo.utils.RandomDataUtils;

import java.awt.image.BufferedImage;

@WebTest
class LoginPageTest {

  @ScreenShotTest("img/pages/login/hermitage.png")
  void loginPageShouldHasHermitageImage(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .checkThatPageLoaded()
        .checkHermitageImage(expected);
  }

  @Test
  @User
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
  void whenPasswordIsWrongThenErrorNotification(UserJson user){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .checkThatPageLoaded()
        .login(user.username(), user.password()+"b")
        .checkFormErrorMessage("Неверные учетные данные пользователя");
  }

  @Test
  void clickRegisterLinkShouldOpenRegisterPage(){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .checkThatPageLoaded()
        .clickRegister()
        .checkThatPageLoaded();
  }
}
