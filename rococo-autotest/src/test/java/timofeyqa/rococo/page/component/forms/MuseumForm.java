package timofeyqa.rococo.page.component.forms;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import timofeyqa.rococo.data.entity.Country;
import timofeyqa.rococo.model.dto.MuseumDto;
import timofeyqa.rococo.model.rest.MuseumJson;
import timofeyqa.rococo.page.component.FormList;
import timofeyqa.rococo.page.lists.MuseumPage;
import timofeyqa.rococo.service.MuseumClient;
import timofeyqa.rococo.service.db.MuseumDbClient;

import javax.annotation.Nullable;

import java.util.Optional;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.empty;
import static timofeyqa.rococo.condition.ValidationConditions.requiredInput;
import static timofeyqa.rococo.condition.ValidationConditions.requiredList;
import static timofeyqa.rococo.jupiter.extension.ContentExtension.content;
import static timofeyqa.rococo.utils.Waiter.waitForOptional;

public class MuseumForm extends Form<MuseumForm> {
  private final SelenideElement titleInput = self.$("input[name='title']");
  private final FormList countries = new FormList(self,"countryId",20);
  private final SelenideElement cityInput = self.$("input[name='city']");
  private final SelenideElement descriptionInput = self.$("textarea[name='description']");
  private final MuseumClient museumClient = new MuseumDbClient();

  public MuseumForm checkThatPageLoaded(){
    self.shouldBe(visible);
    titleInput.shouldBe(visible);
    cityInput.shouldBe(visible);
    descriptionInput.shouldBe(visible);
    return this;
  }

  @Step("Assert title required")
  public MuseumForm assertTitleRequired(){
    titleInput.shouldBe(requiredInput);
    return this;
  }

  @Step("Assert description required")
  public MuseumForm assertDescriptionRequired(){
    descriptionInput.shouldBe(requiredInput);
    return this;
  }

  @Step("Assert city required")
  public MuseumForm assertCityRequired(){
    cityInput.shouldBe(requiredInput);
    return this;
  }

  @Step("Assert country required")
  public MuseumForm assertCountryRequired(){
    countries.getSelf()
        .shouldBe(requiredList);
    return this;
  }

  @Step("Choose country {country}")
  public MuseumForm chooseCountry(Country country){
    countries
        .search(country.getName())
        .click();
    return this;
  }

  @Step("Fill title {title}")
  public MuseumForm fillTitle(String title){
    if (titleInput.is(not(empty))) {
      titleInput.clear();
    }
    titleInput.setValue(title);
    return this;
  }

  @Step("Fill description {description}")
  public MuseumForm fillDescription(String description){
    if (descriptionInput.is(not(empty))) {
      descriptionInput.clear();
    }
    descriptionInput.setValue(description);
    return this;
  }

  @Step("Fill city {city}")
  public MuseumForm fillCity(@Nullable String city){
    if (city != null) {
      if (cityInput.is(not(empty))) {
        cityInput.clear();
      }
      cityInput.setValue(city);
    }
    return this;
  }

  @Step("Fill museum form with title {title}, image : {imagePath}, description: {description}, city {city} and country {country} ")
  public MuseumPage fillForm(
      String title,
      String imagePath,
      String description,
      @Nullable String city,
      Country country
  ) {
    MuseumPage page = fillTitle(title)
        .uploadImage(imagePath)
        .fillDescription(description)
        .fillCity(city)
        .chooseCountry(country)
        .clickSubmitButton()
        .toPage(MuseumPage.class);

    updateContextContent(title);

    return page;
  }

  private void updateContextContent(String title) {
    Optional<MuseumDto> museumOpt = waitForOptional(()-> museumClient.findByTitle(title));
    museumOpt.ifPresent(museum -> content().museums().add(museum));
  }
}
