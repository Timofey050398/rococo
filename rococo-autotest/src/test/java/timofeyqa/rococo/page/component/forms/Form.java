package timofeyqa.rococo.page.component.forms;

import com.codeborne.selenide.SelenideElement;
import timofeyqa.rococo.page.component.BaseComponent;

import static com.codeborne.selenide.Selenide.$;

public abstract class Form<T extends Form<?>> extends BaseComponent<T> {

  protected SelenideElement imageInput = self.$("input[type='file']");
  protected SelenideElement closeModalButton = self.$("button[type='button']");
  protected SelenideElement submitButton = self.$("button[type='submit']");
  protected SelenideElement image = self.$("img");

  public Form() {
    super($("div.card.p-4"));
  }

  public abstract T checkThatPageLoaded();
}
