package timofeyqa.rococo.test.web.painting;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.*;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.rest.PaintingJson;
import timofeyqa.rococo.page.detail.PaintingDetailPage;

import java.awt.image.BufferedImage;

@WebTest
public class PaintingDetailPageTest {

  @ScreenShotTest("img/pages/paintings-list/the-kiss.png")
  @Content(
      paintings = {
          @Painting(
              content = "img/content/paintings/the-kiss.png",
              title = "the kiss",
              museum = "louvre"
          )
      }
  )
  void paintingDetailShouldBeShown(ContentJson content, BufferedImage expected) {
    final PaintingJson painting = content.paintings().iterator().next();
    Selenide.open(PaintingDetailPage.url(painting.id()),PaintingDetailPage.class)
        .checkThatPageLoaded()
        .compareImage(expected);
  }


  @Test
  @User
  @ApiLogin
  @Content(paintings = {@Painting})
  void authorizedUserShouldCanOpenEditDetailPage(ContentJson content) {
    final PaintingJson painting = content.paintings().iterator().next();
    Selenide.open(PaintingDetailPage.url(painting.id()),PaintingDetailPage.class)
        .checkThatPageLoaded()
        .openEditForm()
        .checkThatPageLoaded();
  }
}
