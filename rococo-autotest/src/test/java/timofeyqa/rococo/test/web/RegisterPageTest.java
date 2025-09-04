package timofeyqa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.User;
import timofeyqa.rococo.jupiter.annotation.ScreenShotTest;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.page.MainPage;
import timofeyqa.rococo.page.RegisterPage;

import java.awt.image.BufferedImage;

import static timofeyqa.rococo.utils.RandomDataUtils.randomUsername;
import static timofeyqa.rococo.utils.RandomDataUtils.randomWord;

@WebTest
@DisplayName("Тесты страницы регистрации")
public class RegisterPageTest {

  @ScreenShotTest("register/renuar.png")
  @DisplayName("На странице логина должно быть изображение эрмитажа")
  void loginPageShouldHasHermitageImage(BufferedImage expected){
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .checkThatPageLoaded()
        .checkRenuarImage(expected);
  }

  @Test
  @User
  @DisplayName("При регистрации с занятым пользователем должна отображаться ошибка")
  void registerWithExistedUserShouldProduceErrorNotification(UserJson user){
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .checkThatPageLoaded()
        .submitRegister(user.username(),user.password(),user.password())
        .checkFormErrorMessage(
            String.format("Username `%s` already exists",user.username())
        );
  }

  @Test
  @DisplayName("Логин должен быть больше 2 символов")
  void usernameShouldBeGraterThan2Characters(){
    final String password = randomWord(5);
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .checkThatPageLoaded()
        .submitRegister(randomWord(2),password,password)
        .checkFormErrorMessage("Allowed username length should be from 3 to 50 characters");
  }

  @Test
  @DisplayName("Логин должен быть меньше 51 символа")
  void usernameShouldBeLessThan51Characters() {
    final String password = randomWord(5);
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .checkThatPageLoaded()
        .submitRegister(randomWord(51),password,password)
        .checkFormErrorMessage(
            "Allowed username length should be from 3 to 50 characters"
        );
  }

  @Test
  @DisplayName("Пароль должен быть больше 2 символов")
  void passwordShouldBeGraterThan2Characters() {
    String passErrorText = "Allowed password length should be from 3 to 12 characters";
    final String password = randomWord(2);
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .checkThatPageLoaded()
        .submitRegister(randomUsername(),password,password)
        .checkFormErrorMessage(passErrorText,passErrorText);
  }

  @Test
  @DisplayName("Пароль должен быть меньше 13 символов")
  void passwordShouldBeLessThan13Characters(){
    String passErrorText = "Allowed password length should be from 3 to 12 characters";
    final String password = randomWord(13);
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .checkThatPageLoaded()
        .submitRegister(randomUsername(),password,password)
        .checkFormErrorMessage(passErrorText,passErrorText );
  }

  @Test
  @DisplayName("Значение полей 'пароль' и 'повторите пароль' должно быть одинаковым")
  void passwordSubmitShouldBeEqualToPassword(){
    final String password = randomWord(5);
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .checkThatPageLoaded()
        .submitRegister(randomUsername(),password,password+"b")
        .checkFormErrorMessage("Passwords should be equal");
  }

  @Test
  @DisplayName("Активация кнопки 'логин' должна производить редирект на страницу логина")
  void clickToLoginLinkShouldRedirectToLoginPage(){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .getHeader()
        .clickLoginButton()
        .clickRegister()
        .goToLoginPage()
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("Пользователь должен иметь возможность зарегистрироваться")
  void userShouldCanRegister(){
    final String password = randomWord(5);
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .clickRegister()
        .checkThatPageLoaded()
        .submitRegister(randomUsername(),password,password)
        .assertSuccessRegisterScreen()
        .submit()
        .checkThatPageLoaded();
  }
}
