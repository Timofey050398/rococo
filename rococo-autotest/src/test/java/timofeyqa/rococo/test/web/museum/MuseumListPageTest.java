package timofeyqa.rococo.test.web.museum;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.*;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.rest.MuseumJson;
import timofeyqa.rococo.page.lists.MuseumPage;
import timofeyqa.rococo.utils.RandomDataUtils;

import java.awt.image.BufferedImage;

@WebTest
@DisplayName("Тесты списочной страницы музея")
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
  @DisplayName("Карточки музеев отображаются на списочной")
  void museumShouldBeShown(ContentJson content, BufferedImage expected) {
    final var Museum = content.museums().iterator().next();
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .getCard(Museum.title())
        .compareImage(expected);
  }

  @Test
  @Content(museumCount = 9)
  @DisplayName("Поиск по списочной работает")
  void searchShouldWork(ContentJson content) {
    final var Museum = content.museums().iterator().next();
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .search(Museum.title())
        .assertCardSize(1);
  }

  @Test
  @Content(museumCount = 9)
  @DisplayName("Пагинация на списочной работает")
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
  @DisplayName("Нажатие по карточке открывает детальную страницу")
  void clickDetailShouldOpenDetailPage(ContentJson content) {
    final var Museum = content.museums().iterator().next();
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .getCard(Museum.title())
        .openDetail()
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("Если карточка не найдена, отображается соответвующий текст")
  void museumsNotFoundTest(){
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .search(RandomDataUtils.randomName())
        .compareCardNotFound();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Авторизованный пользователь имеет возможность открыть форму создания музея")
  void authorizedUserShouldCanOpenAddMuseumForm(){
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .addMuseum()
        .checkThatPageLoaded();
  }
}
