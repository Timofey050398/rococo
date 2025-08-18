package timofeyqa.rococo.page.component.forms;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

public class MuseumForm extends Form<MuseumForm> {
  private final SelenideElement titleInput = self.$("input[name='title']");
  private final ElementsCollection countries = self.$$("select[name='countryId'] option");
  private final SelenideElement cityInput = self.$("input[name='city']");
  private final SelenideElement descriptionInput = self.$("textarea[name='description']");
}
