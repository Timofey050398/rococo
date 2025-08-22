package timofeyqa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class NotFoundComponent extends BaseComponent<NotFoundComponent> {

  private SelenideElement searchIcon = self.$("img");
  private SelenideElement headerText = self.$("p.text-xl");
  private SelenideElement bodyText = self.$("p.text-l");


  public NotFoundComponent() {
    super($("div.m-20.text-center"));
  }

  public void shouldShown(String xlText, String text){
    self.shouldBe(visible);
    searchIcon.shouldHave(attribute("alt","Иконка поиска"));
    headerText.shouldHave(text(xlText));
    bodyText.shouldHave(text(text));
  }
}
