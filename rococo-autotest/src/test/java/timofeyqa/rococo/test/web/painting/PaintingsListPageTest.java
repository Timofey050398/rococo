package timofeyqa.rococo.test.web.painting;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.*;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.rest.PaintingJson;
import timofeyqa.rococo.page.lists.PaintingsPage;
import timofeyqa.rococo.utils.RandomDataUtils;

import java.awt.image.BufferedImage;

@WebTest
@DisplayName("Тесты списочной страницы картин")
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
  @DisplayName("Карточки картин отображаются на списочной")
  void paintingShouldBeShown(ContentJson content, BufferedImage expected) {
    final var painting = content.paintings().iterator().next();
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .getCard(painting.title())
        .compareImage(expected);
  }

  @Test
  @Content(paintingCount = 21)
  @DisplayName("Поиск по списочной работает")
  void searchShouldWork(ContentJson content) {
    final var painting = content.paintings().iterator().next();
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .search(painting.title())
        .assertCardSize(1);
  }

  @Test
  @Content(paintingCount = 21)
  @DisplayName("Пагинация на списочной работает")
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
  @DisplayName("Нажатие по карточке открывает детальную страницу")
  void clickDetailShouldOpenDetailPage(ContentJson content) {
    final var painting = content.paintings().iterator().next();
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .getCard(painting.title())
        .openDetail()
        .checkThatPageLoaded();
  }
  
  @Test
  @DisplayName("Если карточка не найдена, отображается соответвующий текст")
  void paintingsNotFoundTest(){
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .search(RandomDataUtils.randomName())
        .compareCardNotFound();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Авторизованный пользователь имеет возможность открыть форму создания картины")
  void authorizedUserShouldCanOpenAddPaintingForm(){
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .addPainting()
        .checkThatPageLoaded();
  }
}
