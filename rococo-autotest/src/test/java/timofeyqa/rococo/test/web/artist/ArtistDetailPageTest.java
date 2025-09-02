package timofeyqa.rococo.test.web.artist;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.*;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.dto.ArtistDto;
import timofeyqa.rococo.page.detail.ArtistDetailPage;

import java.awt.image.BufferedImage;

import static timofeyqa.rococo.utils.RandomDataUtils.randomDescription;
import static timofeyqa.rococo.utils.RandomDataUtils.randomPaintingTitle;

@WebTest
@DisplayName("Тесты детальной страницы художника")
public class ArtistDetailPageTest {

  @ScreenShotTest("artists-list/picasso.png")
  @Content(
      artists = {
          @Artist(
              photo = "img/content/artists/picasso.png",
              name = "picasso",
              biography = "some interesting picasso biography"
          )
      }
  )
  @DisplayName("Детальная страница должна отображаться")
  void artistDetailShouldBeShown(ContentJson content, BufferedImage expected) {
    final ArtistDto artist = content.artists().iterator().next();
    Selenide.open(ArtistDetailPage.url(artist.id()),ArtistDetailPage.class)
        .checkThatPageLoaded()
        .compareImage(expected);
  }


  @Test
  @User
  @ApiLogin
  @Content(artistCount = 1)
  @DisplayName("Авторизованный пользователь может открыть форму изменения художника")
  void authorizedUserShouldCanOpenEditArtistPage(ContentJson content) {
    final var artist = content.artists().iterator().next();
    Selenide.open(ArtistDetailPage.url(artist.id()),ArtistDetailPage.class)
        .checkThatPageLoaded()
        .openEditForm()
        .checkThatPageLoaded();
  }

  @Test
  @Content(artistCount = 1)
  @DisplayName("Когда у ходжника нет картин, на детальной странице отображается информация об отсутствии картин")
  void whenArtistHasNotPaintingsThatShownEmptyPaintingsPage(ContentJson content) {
    final var artist = content.artists().iterator().next();
    Selenide.open(ArtistDetailPage.url(artist.id()),ArtistDetailPage.class)
        .checkThatPageLoaded()
        .comparePageIsEmpty();
  }

  @Test
  @Content(artistCount = 1,paintingCount = 10)
  @DisplayName("Пагинация списка картин на детальной странице художника работает")
  void artistDetailsPaginationShouldWork(ContentJson content) {
    final var artist = content.artists().iterator().next();
    Selenide.open(ArtistDetailPage.url(artist.id()),ArtistDetailPage.class)
        .checkThatPageLoaded()
        .nextPageAndCompare();
  }

  @ScreenShotTest("paintings-list/mona-liza.png")
  @User
  @ApiLogin
  @Content(artistCount = 1)
  @DisplayName("Если пользователь создает карточку картины в форме, открытой через детальную художника, то у художника отображается данная карточка в детальной")
  void whenUserAddPaintingFromArtistDetailThanPaintingShouldShownAtArtistDetail(ContentJson content, BufferedImage expected) {
    final var artist = content.artists().iterator().next();
    final String paintingTitle = randomPaintingTitle();
    Selenide.open(ArtistDetailPage.url(artist.id()),ArtistDetailPage.class)
        .checkThatPageLoaded()
        .openPaintingForm()
        .checkThatPageLoaded()
        .fillForm(
            paintingTitle,
            "img/content/paintings/mona-liza.png",
            randomDescription(),
            null
        )
        .checkToastMessage("Добавлена картина: " + paintingTitle)
        .getCard(paintingTitle)
        .compareImage(expected);
  }
}
