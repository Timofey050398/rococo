package timofeyqa.rococo.test.web.painting;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.*;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.dto.MuseumDto;
import timofeyqa.rococo.page.detail.PaintingDetailPage;
import timofeyqa.rococo.page.lists.PaintingsPage;

import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.NoSuchElementException;

import static timofeyqa.rococo.utils.RandomDataUtils.randomDescription;
import static timofeyqa.rococo.utils.RandomDataUtils.randomFilePath;
import static timofeyqa.rococo.utils.RandomDataUtils.randomName;
import static timofeyqa.rococo.utils.RandomDataUtils.randomWord;

@WebTest
@DisplayName("Тесты компонента формы картины")
public class PaintingFormTest {

  private static final String FOLDER_NAME = "paintings";
  private static final String newArtist = "new artist";
  private static final String newMuseum = "new museum";

  @ScreenShotTest("paintings-list/burlaki-na-volge.png")
  @User
  @ApiLogin
  @Content(artistCount = 1, museumCount = 5)
  @DisplayName("Авторизованный пользователь может добавить картину")
  void authorizedUserShouldCanAddPainting(BufferedImage expected, ContentJson content) {
    final String title = randomName();
    final var lastMuseum = content.museums().stream()
        .max(Comparator.comparing(MuseumDto::title, String.CASE_INSENSITIVE_ORDER))
        .orElseThrow(() -> new NoSuchElementException("Set is empty"));

    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .addPainting()
        .checkThatPageLoaded()
        .fillForm(
            title,
            "img/content/paintings/burlaki-na-volge.png",
            content.artists().iterator().next().name(),
            randomDescription(),
            lastMuseum.title()
        )
        .checkToastMessage("Добавлена картины: "+title)
        .getCard(title)
        .compareImage(expected);
  }

  @ScreenShotTest("paintings-list/burlaki-na-volge.png")
  @User
  @ApiLogin
  @Content(artistCount = 1)
  @DisplayName("Музей - не обязательное поле")
  void museumShouldNotRequired(BufferedImage expected, ContentJson content) {
    final String title = randomName();

    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .addPainting()
        .checkThatPageLoaded()
        .fillForm(
            title,
            "img/content/paintings/burlaki-na-volge.png",
            content.artists().iterator().next().name(),
            randomDescription(),
            null
        )
        .checkToastMessage("Добавлена картины: "+title)
        .getCard(title)
        .compareImage(expected);
  }

  @ScreenShotTest("paintings-list/liquid-clocks.png")
  @Content(
      museums = {@Museum(title = newMuseum)},
      paintings = {@Painting(content = "img/content/paintings/ivan-the-terrible.png", museum = "old museum")}
  )
  @User
  @ApiLogin
  @DisplayName("Авторизованный пользователь может изменить картину")
  void authorizedUserShouldCanEditPainting(ContentJson content, BufferedImage expected) {
    final var painting = content.paintings().iterator().next();
    final String title = randomName();
    final String description = randomDescription();

    Selenide.open(PaintingDetailPage.url(painting.id()),PaintingDetailPage.class)
        .checkThatPageLoaded()
        .openEditForm()
        .fillTitle(title)
        .chooseMuseum(newMuseum)
        .fillDescription(description)
        .uploadImage("img/content/paintings/liquid-clocks.png")
        .clickSubmitButton()
        .checkToastMessage("Обновлена картина: "+title)
        .toPage(PaintingDetailPage.class)
        .compareTitle(title)
        .compareDescription(description)
        .checkToastHidden()
        .compareImage(expected);
  }

  @Test
  @Content(
      artists = {@Artist(name = newArtist)},
      paintings = {@Painting(content = "img/content/paintings/burlaki-na-volge.png", artist = "old artist")}
  )
  @User
  @ApiLogin
  @Disabled("Artist not edited: when user choose another artist " +
      "then send request with old artist id")
  @DisplayName("Авторизованный пользователь может изменить автора картины")
  void authorShouldEditCorrectly(ContentJson content) {
    final var painting = content.paintings().iterator().next();

    Selenide.open(PaintingDetailPage.url(painting.id()),PaintingDetailPage.class)
        .checkThatPageLoaded()
        .openEditForm()
        .chooseAuthor(newArtist)
        .clickSubmitButton()
        .checkToastMessage("Обновлена картина: "+painting.title())
        .toPage(PaintingDetailPage.class)
        .comapreArtist(newArtist);
  }

  @Test
  @User
  @ApiLogin
  @Content(artistCount = 1)
  @DisplayName("Имя картины - обязательное поле")
  void titleShouldBeRequired(ContentJson content) {
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .addPainting()
        .checkThatPageLoaded()
        .chooseAuthor(content.artists().iterator().next().name())
        .fillDescription(randomDescription())
        .uploadImage(randomFilePath(FOLDER_NAME))
        .clickSubmitButton()
        .assertTitleRequired();
  }

  @Test
  @User
  @ApiLogin
  @Content(artistCount = 1)
  @DisplayName("Описание картины - обязательное поле")
  void descriptionShouldBeRequired(ContentJson content) {
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .addPainting()
        .checkThatPageLoaded()
        .fillTitle(randomName())
        .chooseAuthor(content.artists().iterator().next().name())
        .uploadImage(randomFilePath(FOLDER_NAME))
        .clickSubmitButton()
        .assertDescriptionRequired();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Автор картины - обязательное поле")
  void authorShouldBeRequired() {
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .addPainting()
        .checkThatPageLoaded()
        .fillTitle(randomName())
        .fillDescription(randomDescription())
        .uploadImage(randomFilePath(FOLDER_NAME))
        .clickSubmitButton()
        .assertAuthorRequired();
  }

  @Test
  @User
  @ApiLogin
  @Content(artistCount = 1)
  @DisplayName("Изображение картины - обязательное поле")
  void imageShouldBeRequired(ContentJson content) {
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .addPainting()
        .checkThatPageLoaded()
        .fillTitle(randomName())
        .chooseAuthor(content.artists().iterator().next().name())
        .fillDescription(randomDescription())
        .clickSubmitButton()
        .assertImageRequired();
  }

  @Test
  @User
  @ApiLogin
  @Content(artistCount = 1)
  @DisplayName("При загрузке файла больше 1МБ отображается ошибка")
  void oversizeImageShouldBeValidated(ContentJson content) {
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .addPainting()
        .checkThatPageLoaded()
        .fillTitle(randomName())
        .chooseAuthor(content.artists().iterator().next().name())
        .uploadImage("img/content/oversize.png")
        .fillDescription(randomDescription())
        .clickSubmitButton()
        .checkToastFileSizeErrorMessage();
  }

  @Test
  @User
  @ApiLogin
  @Content(artistCount = 1)
  @DisplayName("При вводе наименования меньше 3 символов отображается ошибка")
  void tooShortNameShouldBeValidated(ContentJson content) {
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .addPainting()
        .checkThatPageLoaded()
        .fillTitle("ab")
        .chooseAuthor(content.artists().iterator().next().name())
        .uploadImage(randomFilePath(FOLDER_NAME))
        .fillDescription(randomDescription())
        .clickSubmitButton()
        .checkFormErrorMessage("Название не может быть короче 3 символов");
  }

  @Test
  @User
  @ApiLogin
  @Content(artistCount = 1)
  @DisplayName("При вводе описания меньше 10 символов отображается ошибка")
  void tooShortDescriptionShouldBeValidated(ContentJson content) {
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .addPainting()
        .checkThatPageLoaded()
        .fillTitle(randomName())
        .chooseAuthor(content.artists().iterator().next().name())
        .uploadImage(randomFilePath(FOLDER_NAME))
        .fillDescription(randomWord(9))
        .clickSubmitButton()
        .checkFormErrorMessage("Описание не может быть короче 10 символов");
  }

  @Test
  @User
  @ApiLogin
  @Content(artistCount = 1)
  @DisplayName("При вводе названия больше 255 символов отображается ошибка")
  void tooLongTitleShouldBeValidated(ContentJson content) {
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .addPainting()
        .checkThatPageLoaded()
        .chooseAuthor(content.artists().iterator().next().name())
        .fillTitle(randomWord(260))
        .uploadImage(randomFilePath(FOLDER_NAME))
        .fillDescription(randomDescription())
        .clickSubmitButton()
        .checkFormErrorMessage("Название не может быть длиннее 255 символов");
  }

  @Test
  @User
  @ApiLogin
  @Content(artistCount = 1)
  @DisplayName("При вводе описания больше 2000 символов отображается ошибка")
  void tooLongDescriptionShouldBeValidated(ContentJson content) {
    Selenide.open(PaintingsPage.URL,PaintingsPage.class)
        .checkThatPageLoaded()
        .addPainting()
        .checkThatPageLoaded()
        .fillTitle(randomName())
        .chooseAuthor(content.artists().iterator().next().name())
        .uploadImage(randomFilePath(FOLDER_NAME))
        .fillDescription(randomWord(2001))
        .clickSubmitButton()
        .checkFormErrorMessage("Описание не может быть длиннее 2000 символов");
  }
}
