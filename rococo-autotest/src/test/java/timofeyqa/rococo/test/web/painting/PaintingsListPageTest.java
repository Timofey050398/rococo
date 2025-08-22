package timofeyqa.rococo.test.web.painting;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.*;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.rest.PaintingJson;
import timofeyqa.rococo.page.lists.PaintingsPage;
import timofeyqa.rococo.utils.RandomDataUtils;

import java.awt.image.BufferedImage;

@WebTest
public class PaintingsListPageTest {

  @ScreenShotTest("img/pages/paintings-list/cossacs.png")
  @Content(
      paintings = {
          @Painting(
              content = "img/content/paintings/cossacs.png",
              title = "cossacs",
              museum = "random museum"
          )
      }
  )
  void paintingShouldBeShown(ContentJson content, BufferedImage expected) {
    final PaintingJson painting = content.paintings().iterator().next();
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .getCard(painting.title())
        .compareImage(expected);
  }

  @Test
  @Content(paintingCount = 21)
  void searchShouldWork(ContentJson content) {
    final PaintingJson painting = content.paintings().iterator().next();
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .search(painting.title())
        .assertCardSize(1);
  }

  @Test
  @Content(paintingCount = 21)
  void paginateShouldWork() {
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .nextPageAndCompare();
  }

  @Test
  @Content(
      paintings = {
          @Painting(
              content = "img/content/paintings/cossacs.png",
              title = "cossacs",
              museum = "random museum"
          )
      }
  )
  void clickDetailShouldOpenDetailPage(ContentJson content) {
    final PaintingJson painting = content.paintings().iterator().next();
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .getCard(painting.title())
        .openDetail()
        .checkThatPageLoaded();
  }
  
  @Test
  void paintingsNotFoundTest(){
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .search(RandomDataUtils.randomName())
        .compareCardNotFound();
  }

  @Test
  @User
  @ApiLogin
  void authorizedUserShouldCanOpenAddPaintingForm(){
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .addPainting()
        .checkThatPageLoaded();
  }
}
