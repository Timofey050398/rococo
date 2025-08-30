package timofeyqa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.ScreenShotTest;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.page.MainPage;

import java.awt.image.BufferedImage;

@WebTest
@DisplayName("Тесты главной страницы")
class MainPageTest {

  @ScreenShotTest("img/pages/main/painting-main-page.png")
  @DisplayName("На странице есть изображение для компонента 'Картины'")
  void paintingButtonShouldHasImage(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .checkPaintingsImage(expected);
  }

  @ScreenShotTest("img/pages/main/museum-main-page.png")
  @DisplayName("На странице есть изображение для компонента 'Музеи'")
  void museumButtonShouldHasImage(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .checkMuseumsImage(expected);
  }

  @ScreenShotTest("img/pages/main/artist-main-page.png")
  @DisplayName("На странице есть изображение для компонента 'Художники'")
  void artistButtonShouldHasImage(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .checkArtistsImage(expected);
  }

  @ScreenShotTest("img/pages/main/dark-theme.png")
  @DisplayName("На главной странице можно выбрать темную тему")
  void mainPageHasDarkThemeByDefault(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .checkTheme(expected);
  }

  @Test
  @DisplayName("Клик по кнопке 'картины' переводит на списочную страницу картин")
  void clickPaintingsButtonShouldOpenPaintingsListPage(){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .clickPaintingsCard()
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("Клик по кнопке 'музеи' переводит на списочную страницу муззев")
  void clickMuseumsButtonShouldOpenMuseumsListPage(){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .clickMuseumsCard()
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("Клик по кнопке 'художники' переводит на списочную страницу художников")
  void clickArtistsButtonShouldOpenMuseumsListPage(){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .clickArtistsCard()
        .checkThatPageLoaded();
  }
}