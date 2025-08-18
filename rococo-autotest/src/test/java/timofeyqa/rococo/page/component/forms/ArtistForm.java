package timofeyqa.rococo.page.component.forms;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

public class ArtistForm extends Form<ArtistForm> {
  private final SelenideElement titleInput = self.$("input[name='name']");
  private final SelenideElement descriptionInput = self.$("textarea[name='biography']");
}
