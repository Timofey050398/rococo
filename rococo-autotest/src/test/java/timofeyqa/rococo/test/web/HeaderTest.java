package timofeyqa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.ScreenShotTest;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.page.MainPage;
import timofeyqa.rococo.page.lists.ArtistPage;

import java.awt.image.BufferedImage;

@WebTest
public class HeaderTest {

  @ScreenShotTest("img/pages/main/light-theme.png")
  void themeShouldBeChangedAfterClickSwitcher(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .getHeader()
        .toggleLightSwitch()
        .toPage(MainPage.class)
        .checkTheme(expected);
  }

  @Test
  void clickPaintingsLinkShouldOpenPaintingsListPage(){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .getHeader()
        .clickPaintingsLink()
        .checkThatPageLoaded();
  }

  @Test
  void clickMuseumsLinkShouldOpenMuseumsListPage(){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .getHeader()
        .clickMuseumsLink()
        .checkThatPageLoaded();
  }

  @Test
  void clickArtistsLinkShouldOpenMuseumsListPage(){
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .getHeader()
        .clickArtistsLink()
        .checkThatPageLoaded();
  }

  @Test
  void clickLogoShouldOpenMainPage(){
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .getHeader()
        .clickLogo()
        .checkThatPageLoaded();
  }
}
