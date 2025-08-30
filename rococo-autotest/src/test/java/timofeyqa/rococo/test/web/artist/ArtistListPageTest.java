package timofeyqa.rococo.test.web.artist;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.*;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.page.lists.ArtistPage;
import timofeyqa.rococo.utils.RandomDataUtils;

import java.awt.image.BufferedImage;

@WebTest
@DisplayName("Тесты списочной страницы художников")
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
  @DisplayName("Карточки художников отображаются на списочной")
  void artistShouldBeShown(ContentJson content, BufferedImage expected) {
    final var artist = content.artists().iterator().next();
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .getCard(artist.name())
        .compareImage(expected);
  }

  @Test
  @Content(artistCount = 9)
  @DisplayName("Поиск по списочной работает")
  void searchShouldWork(ContentJson content) {
    final var artist = content.artists().iterator().next();
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .search(artist.name())
        .assertCardSize(1);
  }

  @Test
  @Content(artistCount = 19)
  @DisplayName("Пагинация на списочной работает")
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
  @DisplayName("Нажатие по карточке открывает детальную страницу")
  void clickDetailShouldOpenDetailPage(ContentJson content) {
    final var artist = content.artists().iterator().next();
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .getCard(artist.name())
        .openDetail()
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("Если карточка не найдена, отображается соответвующий текст")
  void artistsNotFoundTest(){
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .search(RandomDataUtils.randomName())
        .compareCardNotFound();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Авторизованный пользователь имеет возможность открыть форму создания художника")
  void authorizedUserShouldCanOpenAddArtistForm(){
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .addArtist()
        .checkThatPageLoaded();
  }
}
