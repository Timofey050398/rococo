package timofeyqa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import timofeyqa.rococo.config.Profile;
import timofeyqa.rococo.jupiter.annotation.OnProfile;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.page.lists.MuseumPage;
import timofeyqa.rococo.page.lists.PaintingsPage;
import timofeyqa.rococo.page.lists.ArtistPage;

@WebTest
@Isolated
@OnProfile({Profile.DOCKER,Profile.LOCAL})
public class EmptyListTest {

  @Test
  void emptyMuseumsListTest(){
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .comparePageIsEmpty();
  }

  @Test
  void emptyPaintingsListTest(){
    Selenide.open(PaintingsPage.URL, PaintingsPage.class)
        .checkThatPageLoaded()
        .comparePageIsEmpty();
  }

  @Test
  void emptyArtistsListTest(){
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .comparePageIsEmpty();
  }
}
