package timofeyqa.rococo.page.component.forms;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;

public class PaintingForm extends Form<PaintingForm> {

  private final ElementsCollection authors = self.$$("select[name='authorId'] option");

  private final SelenideElement titleInput = self.$("input[name='title']");
  private final SelenideElement descriptionInput = self.$("textarea[name='description']");
  private final ElementsCollection museums = self.$$("select[name='museumId'] option");

  public PaintingForm checkThatPageLoaded(){
    self.shouldBe(visible);
    titleInput.shouldBe(visible);
    descriptionInput.shouldBe(visible);
    return this;
  }
}
