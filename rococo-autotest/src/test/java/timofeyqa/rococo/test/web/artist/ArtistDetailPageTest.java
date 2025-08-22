package timofeyqa.rococo.test.web.artist;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.*;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.rest.ArtistJson;
import timofeyqa.rococo.page.detail.ArtistDetailPage;

import java.awt.image.BufferedImage;

@WebTest
public class ArtistDetailPageTest {

  @ScreenShotTest("img/pages/artists-list/picasso.png")
  @Content(
      artists = {
          @Artist(
              photo = "img/content/artists/picasso.png",
              name = "picasso",
              biography = "some interesting picasso biography"
          )
      }
  )
  void artistDetailShouldBeShown(ContentJson content, BufferedImage expected) {
    final ArtistJson artist = content.artists().iterator().next();
    Selenide.open(ArtistDetailPage.url(artist.id()),ArtistDetailPage.class)
        .checkThatPageLoaded()
        .compareImage(expected);
  }


  @Test
  @User
  @ApiLogin
  @Content(artistCount = 1)
  void authorizedUserShouldCanOpenEditArtistPage(ContentJson content) {
    final ArtistJson artist = content.artists().iterator().next();
    Selenide.open(ArtistDetailPage.url(artist.id()),ArtistDetailPage.class)
        .checkThatPageLoaded()
        .openEditForm()
        .checkThatPageLoaded();
  }

  @Test
  @User
  @ApiLogin
  @Content(artistCount = 1)
  void authorizedUserShouldCanOpenEditArtistPage2(ContentJson content) {
    final ArtistJson artist = content.artists().iterator().next();
    Selenide.open(ArtistDetailPage.url(artist.id()),ArtistDetailPage.class)
        .checkThatPageLoaded()
        .openEditForm()
        .checkThatPageLoaded();
  }
}
