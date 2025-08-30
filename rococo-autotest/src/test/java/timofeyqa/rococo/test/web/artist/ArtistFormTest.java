package timofeyqa.rococo.test.web.artist;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.*;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.page.detail.ArtistDetailPage;
import timofeyqa.rococo.page.lists.ArtistPage;

import java.awt.image.BufferedImage;

import static timofeyqa.rococo.utils.RandomDataUtils.*;


@WebTest
@DisplayName("Тесты компонента формы художника")
public class ArtistFormTest {

  private static final String FOLDER_NAME = "artists";

  @ScreenShotTest("img/pages/artists-list/shishkin.png")
  @User
  @ApiLogin
  @DisplayName("Авторизованный пользователь может добавить художника")
  void authorizedUserShouldCanAddArtist(BufferedImage expected) {
    final String name = randomName();
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .addArtist()
        .checkThatPageLoaded()
        .fillForm(
            name,
            "img/content/artists/shishkin.png",
            randomDescription()
        )
        .checkToastMessage("Добавлен художник: "+ name)
        .getCard(name)
        .compareImage(expected);
  }

  @ScreenShotTest("img/pages/artists-list/picasso.png")
  @Content(
      artists = {
          @Artist(
              photo = "img/content/artists/shishkin.png",
              name = "picasso the second",
              biography = "some interesting picasso biography"
          )
      }
  )
  @User
  @ApiLogin
  @DisplayName("Авторизованный пользователь может изменить художника")
  void authorizedUserShouldCanEditArtist(ContentJson content, BufferedImage expected) {
    final var artist = content.artists().iterator().next();
    final String name = randomName();
    final String biography = randomDescription();
    Selenide.open(ArtistDetailPage.url(artist.id()),ArtistDetailPage.class)
        .checkThatPageLoaded()
        .openEditForm()
        .fillName(name)
        .fillBiography(biography)
        .uploadImage("img/content/artists/picasso.png")
        .clickSubmitButton()
        .checkToastMessage("Обновлен художник: "+name)
        .toPage(ArtistDetailPage.class)
        .compareName(name)
        .compareBiography(biography)
        .checkToastHidden()
        .compareImage(expected);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Имя - обязательное поле")
  void nameShouldBeRequired() {
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .addArtist()
        .checkThatPageLoaded()
        .fillBiography(randomDescription())
        .uploadImage(randomFilePath(FOLDER_NAME))
        .clickSubmitButton()
        .assertNameRequired();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Биография - обязательное поле")
  void biographyShouldBeRequired() {
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .addArtist()
        .checkThatPageLoaded()
        .fillName(randomName())
        .uploadImage(randomFilePath(FOLDER_NAME))
        .clickSubmitButton()
        .assertBiographyRequired();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Изображение - обязательное поле")
  void imageShouldBeRequired() {
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .addArtist()
        .checkThatPageLoaded()
        .fillName(randomName())
        .fillBiography(randomDescription())
        .clickSubmitButton()
        .assertImageRequired();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("При загрузке файла больше 1МБ отображается ошибка")
  void oversizeImageShouldBeValidated() {
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .addArtist()
        .checkThatPageLoaded()
        .fillName(randomName())
        .uploadImage("img/content/oversize.png")
        .fillBiography(randomDescription())
        .clickSubmitButton()
        .checkToastFileSizeErrorMessage();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("При вводе имени меньше 3 символов отображается ошибка")
  void tooShortNameShouldBeValidated() {
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .addArtist()
        .checkThatPageLoaded()
        .fillName("ab")
        .uploadImage(randomFilePath(FOLDER_NAME))
        .fillBiography(randomDescription())
        .clickSubmitButton()
        .checkFormErrorMessage("Имя не может быть короче 3 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("При вводе биографии меньше 10 символов отображается ошибка")
  void tooShortDescriptionShouldBeValidated() {
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .addArtist()
        .checkThatPageLoaded()
        .fillName(randomName())
        .uploadImage(randomFilePath(FOLDER_NAME))
        .fillBiography(randomWord(9))
        .clickSubmitButton()
        .checkFormErrorMessage("Биография не может быть короче 10 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("При вводе имени больше 255 символов отображается ошибка")
  void tooLongNameShouldBeValidated() {
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .addArtist()
        .checkThatPageLoaded()
        .fillName(randomWord(260))
        .uploadImage(randomFilePath(FOLDER_NAME))
        .fillBiography(randomDescription())
        .clickSubmitButton()
        .checkFormErrorMessage("Имя не может быть длиннее 255 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("При вводе биографии больше 2000 символов отображается ошибка")
  void tooLongBiographyShouldBeValidated() {
    Selenide.open(ArtistPage.URL,ArtistPage.class)
        .checkThatPageLoaded()
        .addArtist()
        .checkThatPageLoaded()
        .fillName(randomName())
        .uploadImage(randomFilePath(FOLDER_NAME))
        .fillBiography(randomWord(2001))
        .clickSubmitButton()
        .checkFormErrorMessage("Биография не может быть длиннее 2000 символов");
  }
}
