package timofeyqa.rococo.test.web.museum;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.data.entity.Country;
import timofeyqa.rococo.jupiter.annotation.*;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.page.detail.MuseumDetailPage;
import timofeyqa.rococo.page.lists.MuseumPage;

import java.awt.image.BufferedImage;

import static timofeyqa.rococo.utils.RandomDataUtils.*;

@WebTest
@DisplayName("Тесты компонента формы музея")
public class MuseumFormTest {

  private static final String FOLDER_NAME = "museums";

  @ScreenShotTest("img/pages/museums-list/british-museum.png")
  @User
  @ApiLogin
  @DisplayName("Авторизованный пользователь может добавить музей")
  void authorizedUserShouldCanAddMuseum(BufferedImage expected) {
    final String title = randomName();
    final String city = randomCity();
    final Country country = Country.random();
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .addMuseum()
        .checkThatPageLoaded()
        .fillForm(
            title,
            "img/content/museums/british-museum.png",
            randomDescription(),
            city,
            country
        )
        .checkToastMessage("Добавлен музей: "+title)
        .getCard(title)
        .compareGeo(country,city)
        .compareImage(expected);
  }

  @ScreenShotTest("img/pages/museums-list/dali-museum.png")
  @Content(museums = {@Museum(photo = "img/content/museums/british-museum.png")})
  @User
  @ApiLogin
  @DisplayName("Авторизованный пользователь может изменить музей")
  void authorizedUserShouldCanEditMuseum(ContentJson content, BufferedImage expected) {
    final var museum = content.museums().iterator().next();
    final String title = randomName();
    final String description = randomDescription();
    final String city = randomCity();
    Country country = Country.random();
    while (country.getName().equals(museum.geo().country().name())) {
      country = Country.random();
    }
    Selenide.open(MuseumDetailPage.url(museum.id()),MuseumDetailPage.class)
        .checkThatPageLoaded()
        .openEditForm()
        .fillTitle(title)
        .chooseCountry(country)
        .fillCity(city)
        .fillDescription(description)
        .uploadImage("img/content/museums/dali-museum.png")
        .clickSubmitButton()
        .checkToastMessage("Обновлен музей: "+title)
        .toPage(MuseumDetailPage.class)
        .compareTitle(title)
        .compareDescription(description)
        .compareGeo(country,city)
        .checkToastHidden()
        .compareImage(expected);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Наименование - обязательное поле")
  void titleShouldBeRequired() {
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .addMuseum()
        .checkThatPageLoaded()
        .fillDescription(randomDescription())
        .chooseCountry(Country.random())
        .fillCity(randomCity())
        .uploadImage(randomFilePath(FOLDER_NAME))
        .clickSubmitButton()
        .assertTitleRequired();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Описание - обязательное поле")
  void descriptionShouldBeRequired() {
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .addMuseum()
        .checkThatPageLoaded()
        .fillTitle(randomName())
        .fillCity(randomCity())
        .chooseCountry(Country.AFGHANISTAN)
        .uploadImage(randomFilePath(FOLDER_NAME))
        .clickSubmitButton()
        .assertDescriptionRequired();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Изображение - обязательное поле")
  void imageShouldBeRequired() {
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .addMuseum()
        .checkThatPageLoaded()
        .fillTitle(randomName())
        .fillCity(randomCity())
        .chooseCountry(Country.AFGHANISTAN)
        .fillDescription(randomDescription())
        .clickSubmitButton()
        .assertImageRequired();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Город - обязательное поле")
  void cityShouldBeRequired() {
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .addMuseum()
        .checkThatPageLoaded()
        .fillTitle(randomName())
        .fillDescription(randomDescription())
        .chooseCountry(Country.AFGHANISTAN)
        .clickSubmitButton()
        .assertCityRequired();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Страна - обязательное поле")
  void countryShouldBeRequired() {
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .addMuseum()
        .checkThatPageLoaded()
        .fillTitle(randomName())
        .fillDescription(randomDescription())
        .fillCity(randomCity())
        .clickSubmitButton()
        .assertCountryRequired();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("При загрузке файла больше 1МБ отображается ошибка")
  void oversizeImageShouldBeValidated() {
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .addMuseum()
        .checkThatPageLoaded()
        .fillTitle(randomName())
        .chooseCountry(Country.random())
        .fillCity(randomCity())
        .uploadImage("img/content/oversize.png")
        .fillDescription(randomDescription())
        .clickSubmitButton()
        .checkToastFileSizeErrorMessage();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("При вводе наименования меньше 3 символов отображается ошибка")
  void tooShortNameShouldBeValidated() {
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .addMuseum()
        .checkThatPageLoaded()
        .fillTitle("ab")
        .chooseCountry(Country.random())
        .fillCity(randomCity())
        .uploadImage(randomFilePath(FOLDER_NAME))
        .fillDescription(randomDescription())
        .clickSubmitButton()
        .checkFormErrorMessage("Название не может быть короче 3 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("При вводе описания меньше 10 символов отображается ошибка")
  void tooShortDescriptionShouldBeValidated() {
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .addMuseum()
        .checkThatPageLoaded()
        .fillTitle(randomName())
        .chooseCountry(Country.random())
        .fillCity(randomCity())
        .uploadImage(randomFilePath(FOLDER_NAME))
        .fillDescription(randomWord(9))
        .clickSubmitButton()
        .checkFormErrorMessage("Описание не может быть короче 10 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("При вводе города меньше 3 символов отображается ошибка")
  void tooShortCityShouldBeValidated() {
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .addMuseum()
        .checkThatPageLoaded()
        .fillTitle(randomName())
        .chooseCountry(Country.random())
        .fillCity(randomWord(2))
        .uploadImage(randomFilePath(FOLDER_NAME))
        .fillDescription(randomDescription())
        .clickSubmitButton()
        .checkFormErrorMessage("Город не может быть короче 3 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("При вводе наименования больше 255 символов отображается ошибка")
  void tooLongTitleShouldBeValidated() {
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .addMuseum()
        .checkThatPageLoaded()
        .fillTitle(randomWord(260))
        .chooseCountry(Country.random())
        .fillCity(randomCity())
        .uploadImage(randomFilePath(FOLDER_NAME))
        .fillDescription(randomDescription())
        .clickSubmitButton()
        .checkFormErrorMessage("Название не может быть длиннее 255 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("При вводе наименования больше 2000 символов отображается ошибка")
  void tooLongDescriptionShouldBeValidated() {
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .addMuseum()
        .checkThatPageLoaded()
        .fillTitle(randomName())
        .chooseCountry(Country.random())
        .fillCity(randomCity())
        .uploadImage(randomFilePath(FOLDER_NAME))
        .fillDescription(randomWord(2001))
        .clickSubmitButton()
        .checkFormErrorMessage("Описание не может быть длиннее 2000 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("При вводе города больше 255 символов отображается ошибка")
  void tooLongCityShouldBeValidated() {
    Selenide.open(MuseumPage.URL,MuseumPage.class)
        .checkThatPageLoaded()
        .addMuseum()
        .checkThatPageLoaded()
        .fillTitle(randomName())
        .chooseCountry(Country.random())
        .fillCity(randomWord(260))
        .uploadImage(randomFilePath(FOLDER_NAME))
        .fillDescription(randomDescription())
        .clickSubmitButton()
        .checkFormErrorMessage("Город не может быть длиннее 255 символов");
  }
}
