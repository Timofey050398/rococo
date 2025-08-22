package timofeyqa.rococo.test.web.museum;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.*;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.rest.MuseumJson;
import timofeyqa.rococo.page.lists.MuseumPage;
import timofeyqa.rococo.utils.RandomDataUtils;

import java.awt.image.BufferedImage;

@WebTest
public class MuseumListPageTest {

  @ScreenShotTest("img/pages/museums-list/louvre.png")
  @Content(
      museums = {
          @Museum(
              photo = "img/content/museums/louvre.png",
              title = "louvre",
              description = "random museum description"
          )
      }
  )
  void museumShouldBeShown(ContentJson content, BufferedImage expected) {
    final MuseumJson Museum = content.museums().iterator().next();
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .getCard(Museum.title())
        .compareImage(expected);
  }

  @Test
  @Content(museumCount = 9)
  void searchShouldWork(ContentJson content) {
    final MuseumJson Museum = content.museums().iterator().next();
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .search(Museum.title())
        .assertCardSize(1);
  }

  @Test
  @Content(museumCount = 9)
  void paginateShouldWork() {
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .nextPageAndCompare();
  }

  @Test
  @Content(
      museums = {
          @Museum(
              photo = "img/content/museums/louvre.png",
              title = "louvre",
              description = "random museum description"
          )
      }
  )
  void clickDetailShouldOpenDetailPage(ContentJson content) {
    final MuseumJson Museum = content.museums().iterator().next();
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .getCard(Museum.title())
        .openDetail()
        .checkThatPageLoaded();
  }

  @Test
  void museumsNotFoundTest(){
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .search(RandomDataUtils.randomName())
        .compareCardNotFound();
  }

  @Test
  @User
  @ApiLogin
  void authorizedUserShouldCanOpenAddMuseumForm(){
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .addMuseum()
        .checkThatPageLoaded();
  }
}
