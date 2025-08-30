package timofeyqa.rococo.test.web.painting;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.*;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.page.detail.PaintingDetailPage;

import java.awt.image.BufferedImage;

@WebTest
@DisplayName("Тесты детальной страницы картины")
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
  @DisplayName("Детальная страница должна отображаться")
  void paintingDetailShouldBeShown(ContentJson content, BufferedImage expected) {
    final var painting = content.paintings().iterator().next();
    Selenide.open(PaintingDetailPage.url(painting.id()),PaintingDetailPage.class)
        .checkThatPageLoaded()
        .compareImage(expected);
  }


  @Test
  @User
  @ApiLogin
  @Content(paintings = {@Painting})
  @DisplayName("Авторизованный пользователь может открыть форму изменения картины")
  void authorizedUserShouldCanOpenEditDetailPage(ContentJson content) {
    final var painting = content.paintings().iterator().next();
    Selenide.open(PaintingDetailPage.url(painting.id()),PaintingDetailPage.class)
        .checkThatPageLoaded()
        .openEditForm()
        .checkThatPageLoaded();
  }
}
