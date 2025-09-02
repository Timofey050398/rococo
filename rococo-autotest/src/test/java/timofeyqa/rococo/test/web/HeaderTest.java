package timofeyqa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.ApiLogin;
import timofeyqa.rococo.jupiter.annotation.ScreenShotTest;
import timofeyqa.rococo.jupiter.annotation.User;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.page.MainPage;
import timofeyqa.rococo.page.lists.ArtistPage;

import java.awt.image.BufferedImage;

@WebTest
@DisplayName("Тесты компонента хедера")
public class HeaderTest {

  @ScreenShotTest("main/light-theme.png")
  @DisplayName("Нажатие свитчера смены темы меняет тему")
  void themeShouldBeChangedAfterClickSwitcher(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .getHeader()
        .toggleLightSwitch()
        .toPage(MainPage.class)
        .checkTheme(expected);
  }

  @Test
  @DisplayName("Нажатие кнопки 'Картины' переводит на списочную картин")
  void clickPaintingsLinkShouldOpenPaintingsListPage(){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .getHeader()
        .clickPaintingsLink()
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("Нажатие кнопки 'Музеи' переводит на списочную музеев")
  void clickMuseumsLinkShouldOpenMuseumsListPage(){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .getHeader()
        .clickMuseumsLink()
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("Нажатие кнопки 'Художники' переводит на списочную художников")
  void clickArtistsLinkShouldOpenMuseumsListPage(){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .getHeader()
        .clickArtistsLink()
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("Клик по логотипу переводит на главную страницу")
  void clickLogoShouldOpenMainPage(){
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .getHeader()
        .clickLogo()
        .checkThatPageLoaded();
  }

  @ScreenShotTest("profile/avatar-small.png")
  @User(avatar = "img/content/avatar.png")
  @ApiLogin
  @DisplayName("У пользователя с аватаром аватар должен отображаться в шапке")
  void avatarShouldBeShown(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .checkAvatarImage(expected);
  }



  @ScreenShotTest("profile/avatar-template-small.png")
  @User
  @ApiLogin
  @DisplayName("У пользователя без аватара должна отображатсья заглушка в шапке")
  void avatarTemplateShouldBeShown(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .checkAvatarImage(expected);
  }

  @Test
  @User
  @DisplayName("Пользователь может сделать логин")
  void userShouldCanLogin(UserJson user){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .checkThatPageLoaded()
        .login(user.username(), user.password())
        .checkThatPageLoaded()
        .getHeader()
        .assertAuthorized();
  }
}
