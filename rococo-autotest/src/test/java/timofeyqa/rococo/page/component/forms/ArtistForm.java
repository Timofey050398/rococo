package timofeyqa.rococo.page.component.forms;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import timofeyqa.rococo.model.dto.ArtistDto;
import timofeyqa.rococo.model.rest.ArtistJson;
import timofeyqa.rococo.page.lists.ArtistPage;
import timofeyqa.rococo.service.ArtistClient;
import timofeyqa.rococo.service.db.ArtistDbClient;

import java.util.Optional;

import static com.codeborne.selenide.Condition.*;
import static timofeyqa.rococo.condition.ValidationConditions.requiredInput;
import static timofeyqa.rococo.jupiter.extension.ContentExtension.content;
import static timofeyqa.rococo.utils.Waiter.waitForOptional;

public class ArtistForm extends Form<ArtistForm> {
  private final SelenideElement titleInput = self.$("input[name='name']");
  private final SelenideElement biographyInput = self.$("textarea[name='biography']");
  private final ArtistClient artistClient = new ArtistDbClient();

  public ArtistForm checkThatPageLoaded(){
    self.shouldBe(visible);
    titleInput.shouldBe(visible);
    biographyInput.shouldBe(visible);
    return this;
  }

  @Step("Assert name required")
  public ArtistForm assertNameRequired(){
    titleInput.shouldBe(requiredInput);
    return this;
  }

  @Step("Assert biography required")
  public ArtistForm assertBiographyRequired(){
    biographyInput.shouldBe(requiredInput);
    return this;
  }

  @Step("Fill name {name}")
  public ArtistForm fillName(String name){
    if (titleInput.is(not(empty))) {
      titleInput.clear();
    }
    titleInput.setValue(name);
    return this;
  }

  @Step("Fill biography {biography}")
  public ArtistForm fillBiography(String biography){
    if (biographyInput.is(not(empty))) {
      biographyInput.clear();
    }
    biographyInput.setValue(biography);
    return this;
  }

  @Step("Fill artist form with title {name}, name : {imagePath}, description: {description}")
  public ArtistPage fillForm(
      String title,
      String imagePath,
      String description
  ) {
    ArtistPage page = fillName(title)
        .uploadImage(imagePath)
        .fillBiography(description)
        .clickSubmitButton()
        .toPage(ArtistPage.class);

    updateContextContent(title);

    return page;
  }

  private void updateContextContent(String name) {
    Optional<ArtistDto> artistDto = waitForOptional(() -> artistClient.findByName(name));
    artistDto
        .ifPresent(artist -> content().artists().add(artist));
  }
}
