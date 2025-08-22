package timofeyqa.rococo.test.web.artist;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.*;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.rest.ArtistJson;
import timofeyqa.rococo.page.lists.ArtistPage;
import timofeyqa.rococo.utils.RandomDataUtils;

import java.awt.image.BufferedImage;

@WebTest
public class ArtistListPageTest {

  @ScreenShotTest("img/pages/artists-list/dali.png")
  @Content(
      artists = {
          @Artist(
              photo = "img/content/artists/dali.png",
              name = "dali",
              biography = "random artist biography"
          )
      }
  )
  void artistShouldBeShown(ContentJson content, BufferedImage expected) {
    final ArtistJson artist = content.artists().iterator().next();
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .getCard(artist.name())
        .compareImage(expected);
  }

  @Test
  @Content(artistCount = 9)
  void searchShouldWork(ContentJson content) {
    final ArtistJson artist = content.artists().iterator().next();
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .search(artist.name())
        .assertCardSize(1);
  }

  @Test
  @Content(artistCount = 19)
  void paginateShouldWork() {
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .nextPageAndCompare();
  }

  @Test
  @Content(
      artists = {
          @Artist(
              photo = "img/content/artists/dali.png",
              name = "dali",
              biography = "random dali biography"
          )
      }
  )
  void clickDetailShouldOpenDetailPage(ContentJson content) {
    final ArtistJson artist = content.artists().iterator().next();
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .getCard(artist.name())
        .openDetail()
        .checkThatPageLoaded();
  }

  @Test
  void artistsNotFoundTest(){
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .search(RandomDataUtils.randomName())
        .compareCardNotFound();
  }

  @Test
  @User
  @ApiLogin
  void authorizedUserShouldCanOpenAddArtistForm(){
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .addArtist()
        .checkThatPageLoaded();
  }
}
