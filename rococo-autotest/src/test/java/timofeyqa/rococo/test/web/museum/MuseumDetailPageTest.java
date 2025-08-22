package timofeyqa.rococo.test.web.museum;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.*;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.rest.MuseumJson;
import timofeyqa.rococo.page.detail.MuseumDetailPage;

import java.awt.image.BufferedImage;

@WebTest
public class MuseumDetailPageTest {

  @ScreenShotTest("img/pages/museums-list/hermitage.png")
  @Content(
      museums = {
          @Museum(
              photo = "img/content/museums/hermitage.png",
              title = "hermitage",
              description = "some interesting museum description",
              city = "Moscow"
          )
      }
  )
  void museumDetailShouldBeShown(ContentJson content, BufferedImage expected) {
    final MuseumJson museum = content.museums().iterator().next();
    Selenide.open(MuseumDetailPage.url(museum.id()),MuseumDetailPage.class)
        .checkThatPageLoaded()
        .compareImage(expected);
  }


  @Test
  @User
  @ApiLogin
  @Content(museumCount = 1)
  void authorizedUserShouldCanOpenEditMuseumPage(ContentJson content) {
    final MuseumJson museum = content.museums().iterator().next();
    Selenide.open(MuseumDetailPage.url(museum.id()),MuseumDetailPage.class)
        .checkThatPageLoaded()
        .openEditForm()
        .checkThatPageLoaded();
  }
}
