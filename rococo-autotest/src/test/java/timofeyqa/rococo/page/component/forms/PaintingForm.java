package timofeyqa.rococo.page.component.forms;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import timofeyqa.rococo.model.dto.PaintingDto;
import timofeyqa.rococo.model.rest.PaintingJson;
import timofeyqa.rococo.page.BasePage;
import timofeyqa.rococo.page.component.FormList;
import timofeyqa.rococo.page.detail.ArtistDetailPage;
import timofeyqa.rococo.page.lists.PaintingsPage;
import timofeyqa.rococo.service.PaintingClient;
import timofeyqa.rococo.service.db.PaintingDbClient;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.Optional;

import static com.codeborne.selenide.Condition.*;
import static timofeyqa.rococo.condition.ValidationConditions.requiredInput;
import static timofeyqa.rococo.condition.ValidationConditions.requiredList;
import static timofeyqa.rococo.jupiter.extension.ContentExtension.content;
import static timofeyqa.rococo.utils.Waiter.waitForOptional;

@ParametersAreNonnullByDefault
public class PaintingForm extends Form<PaintingForm> {

  private final FormList authors = new FormList(self,"authorId",18);
  private final FormList museums = new FormList(self,"museumId",4);

  private final SelenideElement titleInput = self.$("input[name='title']");
  private final SelenideElement descriptionInput = self.$("textarea[name='description']");
  private final PaintingClient paintingClient = new PaintingDbClient();

  public PaintingForm() {
    super("content");
  }

  public PaintingForm checkThatPageLoaded(){
    self.shouldBe(visible);
    titleInput.shouldBe(visible);
    descriptionInput.shouldBe(visible);
    return this;
  }

  @Step("Assert title required")
  public PaintingForm assertTitleRequired(){
    titleInput.shouldBe(requiredInput);
    return this;
  }

  @Step("Assert description required")
  public PaintingForm assertDescriptionRequired(){
    descriptionInput.shouldBe(requiredInput);
    return this;
  }

  @Step("Assert author required")
  public PaintingForm assertAuthorRequired(){
    authors.getSelf().shouldBe(requiredList);
    return this;
  }

  @Step("Choose author {name}")
  public PaintingForm chooseAuthor(String name){
    authors
        .search(name)
        .click();
    return this;
  }

  @Step("Fill title {title}")
  public PaintingForm fillTitle(String title){
    if (titleInput.is(not(empty))) {
      titleInput.clear();
    }
    titleInput.setValue(title);
    return this;
  }

  @Step("Fill description {description}")
  public PaintingForm fillDescription(String description){
    if (descriptionInput.is(not(empty))) {
      descriptionInput.clear();
    }
    descriptionInput.setValue(description);
    return this;
  }

  @Step("Choose museum {museum}")
  public PaintingForm chooseMuseum(@Nullable String museum){
    if (museum != null) {
      museums
          .search(museum)
          .click();
    }
    return this;
  }

  @Step("Fill painting form from painting list with title {title}, image : {imagePath}, author {authorName}, description: {description} and museum name {museumName}")
  public PaintingsPage fillForm(
      String title,
      String imagePath,
      String authorName,
      String description,
      @Nullable String museumName
  ){
    return fillForm(
        title,
        imagePath,
        description,
        museumName,
        authorName,
        PaintingsPage.class
    );
  }


  @Step("Fill painting form from artist detail with title {title}, image : {imagePath}, description: {description} and museum name {museumName}")
  public ArtistDetailPage fillForm(
      String title,
      String imagePath,
      String description,
      @Nullable String museumName
  ){
    authors.list()
        .shouldBe(CollectionCondition.empty);
    return fillForm(
        title,
        imagePath,
        description,
        museumName,
        null,
        ArtistDetailPage.class
    );
  }

  private  <T extends BasePage<T>> T fillForm(
      String title,
      String imagePath,
      String description,
      @Nullable String museumName,
      @Nullable String authorName,
      Class<T> pageClass
  ) {
    PaintingForm form = fillTitle(title)
        .uploadImage(imagePath)
        .fillDescription(description)
        .chooseMuseum(museumName);

    if (authorName != null) {
      form.chooseAuthor(authorName);
    }

    T page = form.clickSubmitButton()
        .toPage(pageClass);

    updateContextContent(title);

    return page;
  }

  private void updateContextContent(String title) {
    Optional<PaintingDto> paintingJson =  waitForOptional(
        () -> paintingClient.findByTitle(title)
    );
    paintingJson.ifPresent(painting -> content().paintings().add(painting));
  }
}
