package timofeyqa.rococo.page.component.forms;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.visible;

public class ArtistForm extends Form<ArtistForm> {
  private final SelenideElement titleInput = self.$("input[name='name']");
  private final SelenideElement descriptionInput = self.$("textarea[name='biography']");

  public ArtistForm checkThatPageLoaded(){
    self.shouldBe(visible);
    titleInput.shouldBe(visible);
    descriptionInput.shouldBe(visible);
    return this;
  }
}
