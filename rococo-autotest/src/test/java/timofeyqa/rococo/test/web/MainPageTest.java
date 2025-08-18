package timofeyqa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.ScreenShotTest;
import timofeyqa.rococo.jupiter.annotation.User;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.page.MainPage;

import java.awt.image.BufferedImage;

@WebTest
@DisplayName("Main page web tests")
class MainPageTest {

  @ScreenShotTest("img/pages/main/painting-main-page.png")
  void paintingButtonShouldHasImage(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .checkPaintingsImage(expected);
  }

  @ScreenShotTest("img/pages/main/museum-main-page.png")
  void museumButtonShouldHasImage(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .checkMuseumsImage(expected);
  }

  @ScreenShotTest("img/pages/main/artist-main-page.png")
  void artistButtonShouldHasImage(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .checkArtistsImage(expected);
  }

  @ScreenShotTest(value = "img/pages/main/dark-theme.png")
  void mainPageHasDarkThemeByDefault(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .checkTheme(expected);
  }

  @ScreenShotTest(value = "img/pages/main/light-theme.png",rewriteExpected = true)
  void themeShouldBeChangedAfterClickSwitcher(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .getHeader()
        .toggleLightSwitch()
        .toPage(MainPage.class)
        .checkTheme(expected);
  }

  @Test
  void clickPaintingsButtonShouldOpenPaintingsListPage(){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .clickPaintingsCard()
        .checkThatPageLoaded();
  }

  @Test
  void clickMuseumsButtonShouldOpenMuseumsListPage(){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .clickMuseumsCard()
        .checkThatPageLoaded();
  }

  @Test
  void clickArtistsButtonShouldOpenMuseumsListPage(){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .clickArtistsCard()
        .checkThatPageLoaded();
  }

  @Test
  @User
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