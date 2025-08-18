package timofeyqa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.User;
import timofeyqa.rococo.jupiter.annotation.ScreenShotTest;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.page.MainPage;
import timofeyqa.rococo.page.RegisterPage;
import timofeyqa.rococo.utils.RandomDataUtils;

import java.awt.image.BufferedImage;

import static timofeyqa.rococo.utils.RandomDataUtils.randomUsername;
import static timofeyqa.rococo.utils.RandomDataUtils.randomWord;

@WebTest
public class RegisterPageTest {

  @ScreenShotTest("img/pages/register/renuar.png")
  void loginPageShouldHasHermitageImage(BufferedImage expected){
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .checkThatPageLoaded()
        .checkRenuarImage(expected);
  }

  @Test
  @User
  void registerWithExistedUserShouldProduceErrorNotification(UserJson user){
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .checkThatPageLoaded()
        .submitRegister(user.username(),user.password(),user.password())
        .checkFormErrorMessage(
            String.format("Username `%s` already exists",user.username())
        );
  }

  @Test
  void usernameShouldBeGraterThan2Characters(){
    final String password = randomWord(5);
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .checkThatPageLoaded()
        .submitRegister(randomWord(2),password,password)
        .checkFormErrorMessage("Allowed username length should be from 3 to 50 characters");
  }

  @Test
  void usernameShouldBeLessThan51Characters(){
    final String password = randomWord(5);
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .checkThatPageLoaded()
        .submitRegister(randomWord(51),password,password)
        .checkFormErrorMessage("Allowed username length should be from 3 to 50 characters");
  }

  @Test
  void passwordShouldBeGraterThan2Characters(){
    final String password = randomWord(2);
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .checkThatPageLoaded()
        .submitRegister(randomUsername(),password,password)
        .checkFormErrorMessage("Allowed password length should be from 3 to 12 characters");
  }

  @Test
  void passwordShouldBeLessThan13Characters(){
    final String password = randomWord(13);
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .checkThatPageLoaded()
        .submitRegister(randomUsername(),password,password)
        .checkFormErrorMessage("Allowed password length should be from 3 to 12 characters");
  }

  @Test
  void passwordSubmitShouldBeEqualToPassword(){
    final String password = randomWord(5);
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .checkThatPageLoaded()
        .submitRegister(randomUsername(),password,password+"b")
        .checkFormErrorMessage("Passwords should be equal");
  }

  @Test
  void clickToLoginLinkShouldRedirectToLoginPage(){
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .checkThatPageLoaded()
        .goToLoginPage()
        .checkThatPageLoaded();
  }

  @Test
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
